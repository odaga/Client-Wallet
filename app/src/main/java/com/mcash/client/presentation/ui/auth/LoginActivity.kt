package com.mcash.client.presentation.ui.auth

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.mcash.client.R
import com.mcash.client.databinding.ActivityLoginBinding
import com.mcash.client.core.base.BaseActivity
import com.mcash.client.core.utils.AlertType
import com.mcash.client.core.utils.Constants.Permissions.RECEIVE_SMS_PERMISSION
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.core.utils.ZeroTextRemover
import com.mcash.client.core.utils.removeFocus
import com.mcash.client.core.utils.requestPermissionWithRationale
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.MainViewModel
import com.mcash.client.presentation.workers.SessionWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private var userPhone: String = ""
    private var deviceToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                deviceToken = it.result
                Log.d("Token", deviceToken)
                Log.d("Firebase device Token for notifications", deviceToken)
            }
        }

//        autoFillCredential()

        with(binding) {
            with(viewModel) {

                loginState.observe(this@LoginActivity) {
                    when (it) {
                        is LoginState.Loading -> showProgressDialog()
                        is LoginState.Error -> {
                            showErrorDialog(it.message)
                            hideProgressDialog()
                            clPinVerify.visibility = View.VISIBLE
                            clCode.visibility = View.GONE

                            btnSend.text = getString(R.string.send_otp)
                            btnSend.isClickable = true
                        }

                        is LoginState.Success -> {
                            verifyOTPcode()

                            clPinVerify.visibility = View.GONE
                            clCode.visibility = View.VISIBLE
                            hideProgressDialog()
                            runCountdownTimer()
                        }

                        else -> {}
                    }
                }

                verifyOtpState.observe(this@LoginActivity) {
                    when (it) {
                        is VerifyOtpState.Loading -> showProgressDialog()
                        is VerifyOtpState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)
                            btnVerifyCode.text = getString(R.string.verify_otp)
                            btnVerifyCode.isClickable = true
                        }

                        is VerifyOtpState.Success -> {
                            runBlocking {
                                saveLoginState()
                                setUpSessionJob()
                            }
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                            hideProgressDialog()
                        }

                        else -> {}
                    }
                }
            }

            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            ccp.registerCarrierNumberEditText(etPhoneNumber)

            btnSend.setOnClickListener {
                when {
                    etPhoneNumber.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Phone number is required",
                            AlertType.FAILURE
                        )
                    }

                    etPin.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Pin is required",
                            AlertType.FAILURE
                        )
                    }

                    ccp.fullNumber.length < 12 -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Phone number is short",
                            AlertType.FAILURE
                        )
                    }

                    else -> {
                        userPhone = "0${ccp.fullNumber.takeLast(9)}"
                        viewModel.authenticateUser(
                            userPhone,
                            etPin.text.toString(),
                            device_id = getDeviceID(),
                            model = getPhoneModel(),
                            device_token = deviceToken
                        )


                        removeFocus()
                        btnSend.isClickable = false
                        btnSend.text = getString(R.string.loading)

                    }
                }
            }
        }
    }

    private fun runCountdownTimer() {
        with(binding) {
            mtvResend.isVisible = false
            mtvTimeCounter.isVisible = true

            object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    val formatter = DecimalFormat("##")
                    val yourFormattedString: String = formatter.format(millisUntilFinished / 1000)
                    mtvTimeCounter.text = "Resend in $yourFormattedString s"
                }

                override fun onFinish() {
                    mtvResend.isVisible = true
                    mtvTimeCounter.isVisible = false
                }
            }.start()
        }
    }

    private fun verifyOTPcode() {
        with(binding) {
            userPhone = "0${ccp.fullNumber.takeLast(9)}"
            codePinView.doOnTextChanged { text, _, _, _ ->
                if (text.toString().length == 4) {
                    btnVerifyCode.isClickable = false
                    btnVerifyCode.text = getString(R.string.loading)
                    removeFocus()

                    viewModel.verifyUserOtp(
                        userPhone,
                        etPin.text.toString(),
                        codePinView.text.toString(),
                        device_id = getDeviceID(),
                        model = getPhoneModel(),
                        device_token = deviceToken
                    )
                }
            }
            mtvCodeLabel.text = getString(
                R.string.enter_the_code_that_was_sent_to_your_phone_number,
                ccp.fullNumberWithPlus
            )
            mtvResend.setOnClickListener {
                viewModel.authenticateUser(
                    userPhone,
                    etPin.text.toString(),
                    device_id = getDeviceID(),
                    model = getPhoneModel(),
                    device_token = deviceToken
                )
            }

            btnVerifyCode.setOnClickListener {
                when {
                    codePinView.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "OTP code is required",
                            AlertType.FAILURE
                        )
                    }

                    else -> {
                        btnVerifyCode.isClickable = false
                        btnVerifyCode.text = getString(R.string.loading)
                        //removeFocus()

                        viewModel.verifyUserOtp(
                            userPhone,
                            etPin.text.toString(),
                            codePinView.text.toString(),
                            device_id = getDeviceID(),
                            model = getPhoneModel(),
                            device_token = deviceToken
                        )

                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        runBlocking {
            println("Device token is: $deviceToken")
            with(viewModel) {
                if (getAuthState()) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        runBlocking {
            println("Device token is: $deviceToken")
            with(viewModel) {
                if (getAuthState()) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }


    private fun setUpSessionJob() {
        val workRequest = OneTimeWorkRequestBuilder<SessionWorker>().setInitialDelay(
            MINUTES_TO_TOKEN_EXPIRY, TimeUnit.MINUTES
        ).build()

        val workManager = WorkManager.getInstance(this@LoginActivity).enqueue(workRequest)
        runBlocking {
            mainViewModel.saveSessionJobId(workRequest.id.toString())
        }

    }

    private fun autoFillCredential(){
        with(binding) {
            etPhoneNumber.setText(TEST_PHONE_NUMBER)
            etPin.setText(TEST_ACCOUNT_PIN)
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
        private const val MINUTES_TO_TOKEN_EXPIRY = 59L
        private const val TEST_PHONE_NUMBER = "779512013"
        private const val TEST_ACCOUNT_PIN = "1234"
    }
}
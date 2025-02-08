package com.mcash.client.presentation.ui.splash

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mcash.client.BuildConfig
import com.mcash.client.R
import com.mcash.client.core.base.BaseActivity
import com.mcash.client.core.utils.AlertType
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.databinding.LayoutVersionBinding
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.auth.LoginActivity
import com.mcash.client.presentation.ui.auth.LoginViewModel
import com.mcash.client.presentation.ui.kyc.RegisterActivity
import com.mcash.client.presentation.workers.AppSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setAppTheme()

        with(splashViewModel) {
            viewModelScope.launch {
                val version = BuildConfig.VERSION_CODE
                if (isNetworkAvailable(this@SplashActivity)) {

                    versionState.observe(this@SplashActivity) {
                        when (it) {
                            is SplashViewModel.VersionState.Loading -> {}
                            is SplashViewModel.VersionState.Error -> {
                                showAlert(
                                    getString(R.string.error),
                                    it.message,
                                    AlertType.FAILURE
                                )
                            }

                            is SplashViewModel.VersionState.Success -> {
                                if (it.data.toInt() != version) {
                                    val dialog =
                                        BottomSheetDialog(
                                            this@SplashActivity,
                                            R.style.AppBottomSheetDialogTheme
                                        )
                                    dialog.setCancelable(false)
                                    val dialogBinding = LayoutVersionBinding.inflate(layoutInflater)
                                    dialog.setContentView(dialogBinding.root)
                                    dialog.show()
                                    with(dialogBinding) {

                                        btnSkip.setOnClickListener {
                                            dialog.dismiss()
                                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
//                                            startActivity(
//                                                Intent(
//                                                    this@SplashActivity,
//                                                    RegisterActivity::class.java
//                                                )
//                                            )
//                                            finish()
                                        }
                                    }
                                } else {
                                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
//                                    startActivity(
//                                        Intent(
//                                            this@SplashActivity,
//                                            RegisterActivity::class.java
//                                        )
//                                    )
//                                    finish()

                                    with(loginViewModel) {
                                        runBlocking {
                                            if (getAuthState()) {
                                                startActivity(
                                                    Intent(
                                                        this@SplashActivity,
                                                        MainActivity::class.java
                                                    )
                                                )
                                                finish()
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                } else {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
//                    startActivity(Intent(this@SplashActivity, RegisterActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun setupWork() {
        val syncWorker = OneTimeWorkRequestBuilder<AppSyncWorker>().build()
        WorkManager.getInstance(this).enqueue(syncWorker)
    }

    private fun setAppTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }


    private fun isNetworkAvailable(context: Context?): Boolean {

        if (context == null) return false

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }

        return false
    }
}
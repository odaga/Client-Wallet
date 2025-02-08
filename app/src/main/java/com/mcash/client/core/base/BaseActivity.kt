package com.mcash.client.core.base


import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.mcash.client.R
import com.mcash.client.core.utils.ConfirmationClickLister
import com.mcash.client.core.utils.ConfirmationDialog
import com.mcash.client.core.utils.PinConfirmationDialog
import com.mcash.client.core.utils.ProgressUtils
import com.mcash.client.core.utils.SuccessDialogClickLister
import com.mcash.client.core.utils.showSuccessDialog
import timber.log.Timber


abstract class BaseActivity : AppCompatActivity() {

    private lateinit var progressUtils: ProgressUtils
    private lateinit var awaitDialog: ConfirmationDialog
    var pinDialog: PinConfirmationDialog? = null
    private lateinit var reviewManager: ReviewManager
    private lateinit var reviewRequest: Task<ReviewInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressUtils = ProgressUtils(this)
        awaitDialog = ConfirmationDialog(this)
        pinDialog = PinConfirmationDialog(this)
        reviewManager = ReviewManagerFactory.create(this)
//        reviewManager = FakeReviewManager(this)
        reviewRequest = reviewManager.requestReviewFlow()

        //startService(Intent(this, AutoLogoutService::class.java))
    }

    protected fun showProgressDialog() {
        progressUtils.showDialog()
    }

    protected fun hideProgressDialog() {
        progressUtils.hideDialog()
    }

    fun showAwaitConfirmation(
        title: String,
        message: String,
        clickListener: ConfirmationClickLister
    ) {
        awaitDialog.showAwaitDialog(title, message, clickListener)
    }

    fun dismissAwaitConfirmation() {
        awaitDialog.hideAwaitDialog()
    }

    fun showSuccessMessage(
        title: String,
        message: String,
        status: Int,
        dialogClickListener: SuccessDialogClickLister
    ) {
        showSuccessDialog(title, message, status, dialogClickListener)
    }


    fun startAppReview() {
        Timber.d("Starting app review...")
        reviewRequest.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                val reviewInfo = request.result
                val flow = reviewManager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    //Continue your application process
                    Timber.d("Review completed...")
                }
            } else {
                val exception = request.exception
                Timber.e("Review error: $exception")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setAppTheme()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressUtils.destroyDialog()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()

    }

    fun getDeviceID(): String {
        var deviceUniqueIdentifier: String? = null
        deviceUniqueIdentifier =
            Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        return deviceUniqueIdentifier
    }

    fun getPhoneModel(): String {
        var deviceUniqueIdentifier: String? = null
        deviceUniqueIdentifier = Build.MODEL

        return deviceUniqueIdentifier
    }

    /* show error alert dialog */
    fun showErrorDialog(errorMessage: String) {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(R.layout.error_alert_dialog, null)
        val button = view.findViewById<Button>(R.id.dialogDismiss_button)
        val message = view.findViewById<TextView>(R.id.dialogErrorMessage)
        val buttonClose = view.findViewById<ImageView>(R.id.dialog_close)

        buttonClose.setOnClickListener {
            builder.dismiss()
        }

        message.text = errorMessage
        builder.setView(view)
        button.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun setAppTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}
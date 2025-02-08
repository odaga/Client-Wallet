package com.mcash.client.core.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.gson.Gson
import com.mcash.client.R
import com.mcash.client.core.utils.ProgressUtils
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.auth.LoginActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>
) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    private lateinit var progressUtils: ProgressUtils

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onStart() {
        super.onStart()
        setAppTheme()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressUtils = ProgressUtils(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return _binding?.root
    }

    fun getParentActivity(): FragmentActivity {
        return requireActivity()
    }

    protected fun showProgressDialog() {
        progressUtils.showDialog()
    }

    protected fun hideProgressDialog() {
        progressUtils.hideDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun navigate(direction: NavDirections) = findNavController().navigate(direction)

    protected fun navigateWithBundle(direction: Int, bundle: Bundle?) =
        findNavController().navigate(direction, bundle)

    protected fun navigateClearBackstack(destination: Int, bundle: Bundle?) =
        findNavController().navigate(
            destination,
            bundle,
            NavOptions.Builder().setPopUpTo(destination, true).build()
        )


    protected fun navigateUp() = findNavController().navigateUp()

    fun getDeviceID(): String {
        var deviceUniqueIdentifier: String? = ""
        deviceUniqueIdentifier =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        return deviceUniqueIdentifier
    }

    fun getPhoneModel(): String {
        var deviceUniqueIdentifier: String? = ""
        deviceUniqueIdentifier = Build.MODEL

        return deviceUniqueIdentifier
    }

    fun getAndroidVersion(): Int {
       return Build.VERSION.SDK_INT
    }

    fun displayInformation(name: String?): String {
        val x = Html.fromHtml(name)
        return x.toString()
    }


    private fun startLocationUpdates() {
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val bestProvider = locationManager.getBestProvider(criteria, true)
        val minTime = 1000L // Minimum time interval between location updates (milliseconds)
        val minDistance = 1f // Minimum distance interval between location updates (meters)
        bestProvider?.let {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationManager.requestLocationUpdates(
                it,
                minTime,
                minDistance,
                locationListener
            )
        }
    }


    @SuppressLint("MissingPermission")
    fun passiveProvider(context: Context): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
    }


//    private fun showAlertDialog() {
//        val activity = activity as MainActivity
//        activity.pinDialog?.hidePinConfirmationDialog()
//        this.hideProgressDialog()
//
//        val km = activity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
//        val kl = km!!.newKeyguardLock("MyKeyguardLock")
//        kl.disableKeyguard()
//        val pm = activity.getSystemService(Context.POWER_SERVICE) as PowerManager?
//        val wakeLock: PowerManager.WakeLock = pm!!.newWakeLock(
//            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP
//                    or PowerManager.ON_AFTER_RELEASE, "MyWakeLock"
//        )
//        wakeLock.acquire()
//
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle("Title")
//        builder.setTitle("Session Expired")
//        builder.setMessage("You are going to be logged out. Please re-login")
//        builder.setPositiveButton(
//            "OK"
//        ) { p0, p1 ->
//            startActivity(Intent(requireContext(), LoginActivity::class.java))
//            activity.finish()
//        }
//
//        val alert = builder.create()
//        alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
//        alert.window?.addFlags(
//            (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
//                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
//                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
//                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
//        )
//        alert.show()
//        alert.setOnDismissListener {
//            startActivity(Intent(requireContext(), LoginActivity::class.java))
//            activity.finish()
//        }
//    }

    /* show error alert dialog */
    fun showErrorDialog(errorMessage: String) {
        if (errorMessage == UNAUTHENTICATED_USER_MESSAGE) {
            val activity = requireActivity() as MainActivity
            activity.showSessionTimeoutDialog()
        } else {
            val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
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
    }
    /**
     *
     *  Returns pair Pair<Date, Time> extracted from the
     *  current device Time
     *
     */
    fun getCurrentDateTime(): Pair<String, String> {
        val currentDateTime = LocalDateTime.now()

        // Define the formatter
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a", Locale.ENGLISH)

        val date = currentDateTime.format(formatter)
        val time = currentDateTime.format(timeFormatter)

        return Pair(date, time)
    }

    private fun setAppTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

     fun convertMapsToJson(maps: HashMap<String, String>): String {
        val gson = Gson()
        return gson.toJson(maps)
    }

    companion object {
        private const val UNAUTHENTICATED_USER_MESSAGE = "Un Authorized Access"
    }
}
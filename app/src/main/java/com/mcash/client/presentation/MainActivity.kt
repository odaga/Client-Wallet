package com.mcash.client.presentation

import android.Manifest
import android.app.KeyguardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.work.*
import com.mcash.client.R
import com.mcash.client.core.base.BaseActivity
import com.mcash.client.core.utils.Constants
import com.mcash.client.core.utils.hasPermission
import com.mcash.client.core.utils.requestPermissionWithRationale
import com.mcash.client.databinding.ActivityMainBinding
import com.mcash.client.presentation.ui.auth.IdleSessionDialog
import com.mcash.client.presentation.ui.auth.InvalidSessionDialog
import com.mcash.client.presentation.ui.auth.LoginActivity
import com.mcash.client.presentation.ui.home.HomeFragment
import com.mcash.client.presentation.ui.home.TransferBottomSheet
import com.mcash.client.presentation.ui.utilities.bundles.BundlesBottomSheet
import com.mcash.client.presentation.workers.AppSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : BaseActivity(),
    TransferBottomSheet.TransferItemClickListener,
    BundlesBottomSheet.BundlesItemClickListener,
    HomeFragment.HomeClickInterface, IdleSessionDialog.DialogClickListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var navOptions: NavOptions

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var transferBottomSheet: TransferBottomSheet

    private lateinit var idleSessionDialog: IdleSessionDialog
    private lateinit var invalidSessionDialog: InvalidSessionDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transferBottomSheet = TransferBottomSheet()

        idleSessionDialog = IdleSessionDialog(viewModel, this)
        invalidSessionDialog = InvalidSessionDialog(this)

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@MainActivity).apply {
                    setMessage(getString(R.string.are_you_sure_you_want_to_exit))
                    setPositiveButton(getString(R.string.yes)) { _, _ -> finish() }
                    setNegativeButton(getString(R.string.no)) { _, _ -> }
                    show()
                }
            }
        })

        with(binding) {
            supportActionBar?.hide()

            val navHostFragment = supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment_main
            ) as NavHostFragment
            navController = navHostFragment.findNavController()
            appBarConfiguration = AppBarConfiguration(navController.graph)

            navOptions = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .setPopUpTo(navController.graph.getStartDestination(), false)
                .build()

            btnBack.setOnClickListener { navController.navigateUp() }
        }

        // Accept permissions to load contacts
        if (hasPermission(Manifest.permission.READ_CONTACTS)) {
            viewModel.fetchContacts()
        } else {
            requestPermissionWithRationale(
                Manifest.permission.READ_CONTACTS,
                Constants.Permissions.READ_CONTACT_PERMISSION, getString(
                    R.string.contact_permission_rationale
                )
            )
            Timber.d("Phone permissions denied")
        }

        startSessionMonitoring()
        setupRecurringWork()
    }

    /**
     *  Set up user session monitoring
     */
    private fun startSessionMonitoring() {

        val workManager = WorkManager.getInstance(this@MainActivity)
        runBlocking {
            with(viewModel) {
                workManager.getWorkInfoByIdLiveData(UUID.fromString(getSessionJobId()))
                    .observe(this@MainActivity) { workInfo ->
                        if (workInfo != null) {
                            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                                Log.d(
                                    TAG,
                                    "SessionManager: Session job is has executed successfully"
                                )
                                showSessionTimeoutDialog()
                            }

                            if (workInfo.state == WorkInfo.State.ENQUEUED) {
                                Log.d(TAG, "SessionManager: Session job is enqueued")
                            }

                            if (workInfo.state == WorkInfo.State.BLOCKED) {
                                Log.d(TAG, "SessionManager: Session job is blocked")
                            }
                        }
                    }
            }
        }

    }

    private val listener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {

                R.id.fragment_promo_detail -> {
                    with(binding) {
                        mtvTitle.text = getString(R.string.promos_info)
                        toolbar.visibility = View.VISIBLE
                    }
                }

                R.id.fragment_payment_list -> {
                    with(binding) {
                        mtvTitle.text = getString(R.string.payments)
                        toolbar.visibility = View.VISIBLE
                    }
                }

                R.id.fragment_mobile_money -> {
                    with(binding) {
                        mtvTitle.text = getString(R.string.mobile_money)
                        toolbar.visibility = View.VISIBLE
                    }
                }

                R.id.fragment_bank_transfer -> {
                    with(binding) {
                        mtvTitle.text = getString(R.string.mobile_money)
                        toolbar.visibility = View.VISIBLE
                    }
                }

                else -> binding.toolbar.visibility = View.GONE
            }
        }

    private fun setupRecurringWork() {
        val backupWorkRequest = OneTimeWorkRequestBuilder<AppSyncWorker>().build()
        WorkManager.getInstance(this).enqueue(backupWorkRequest)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<AppSyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            AppSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        navController.removeOnDestinationChangedListener(listener)
        super.onPause()
    }

    override fun onWalletClicked() {
        navController.navigate(R.id.fragment_transfer_mcash_wallet, null, navOptions)
    }

    override fun onMobileMoneyCLicked() {
        navController.navigate(R.id.fragment_mobile_money, null, navOptions)
    }

    override fun onBankClicked() {
        navController.navigate(R.id.fragment_bank_transfer, null, navOptions)
    }

    // Home click listeners
    override fun onTransferClicked() {
        transferBottomSheet.show(supportFragmentManager, "TransferBottomSheet")
    }

    override fun onTopUpClicked() {
        navController.navigate(R.id.fragment_top_up)
    }

    fun setToolbarTitle(title: String) {
        binding.mtvTitle.text = title
    }

    override fun onVoiceClicked() {
        navController.navigate(R.id.voiceFragment, null, navOptions)
    }

    override fun onDataBundlesClicked() {
        navController.navigate(R.id.internetFragment, null, navOptions)
    }

     private fun closeUserSession() {
        runBlocking {
            // cancel session job
            try {
                viewModel.signOut()
                val workManager = WorkManager.getInstance(this@MainActivity)
                val sessionJobId = viewModel.getSessionJobId()
                if (sessionJobId.isNotEmpty())
                    workManager.cancelWorkById(UUID.fromString(sessionJobId))
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                throw e
            }
        }
    }


    override fun onUserInteraction() {
        super.onUserInteraction()
        //TODO: register user interaction and reset idle timer in the mainviewModel
//        if (viewModel.sessionDialogActive.value == false) {
//            viewModel.restartTimer()
//        }
    }


    override fun triggerLogout() {
        closeUserSession()
    }

    override fun keepSession() {
        TODO("Not yet implemented")
    }

    fun showSessionTimeoutDialog() {
        invalidSessionDialog.show(supportFragmentManager, idleSessionDialog.tag)
    }


}

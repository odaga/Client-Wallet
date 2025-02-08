package com.mcash.client.presentation.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.SuccessDialogClickLister
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.databinding.FragmentSettingsBinding
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.adapters.FaqAdapter
import com.mcash.client.presentation.ui.auth.ChangePinState
import com.mcash.client.presentation.ui.auth.LoginActivity
import com.mcash.client.presentation.ui.auth.LoginViewModel
import com.mcash.client.presentation.ui.home.HomeViewModel
import com.mcash.domain.model.NotificationEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    private val viewModel: HomeViewModel by viewModels()
    private val homeViewModel: LoginViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    var list: List<NotificationEntity> ?= null

    //@SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val user = viewModel.savedUser
            mtvUsername.text = user.fullName
            mtvUserPhone.text = user.phone

            lifecycleScope.launch {
                val x = viewModel.getNotifications()
                if (x>0) binding.textView.visibility = View.VISIBLE else binding.textView.visibility = View.GONE

                list = settingsViewModel.getNotificationsList()
            }


            with(settingsToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.my_account)
            }

            //change pin click
            reset.setOnClickListener {
                changePinContainer.visibility = View.VISIBLE
                faqContainer.visibility = View.GONE
                settingsList.visibility = View.GONE
                helpContainer.visibility = View.GONE
                aboutContainer.visibility = View.GONE
                notifyContainer.visibility = View.GONE
                //fragmentManager?.beginTransaction()?.replace(R.id.settingsContainer, ChangePinFragment(), null)?.addToBackStack(null)?.commit()
                //navigate(SettingsFragmentDirections.actionSettingsFragmentToChangePinFragment())


                //change pin layout toolbar
                with(changePinToolbar) {
                    btnBack.setOnClickListener {
                        settingsList.visibility = View.VISIBLE
                        changePinContainer.visibility = View.GONE
                    }
                    mtvTitle.text = getString(R.string.change)
                }
            }

            //notification click
            notification.setOnClickListener {
                notifyContainer.visibility = View.VISIBLE
                helpContainer.visibility = View.GONE
                settingsList.visibility = View.GONE
                changePinContainer.visibility = View.GONE
                aboutContainer.visibility = View.GONE
                faqContainer.visibility = View.GONE

                with(notifyToolbar) {
                    btnBack.setOnClickListener {
                        settingsList.visibility = View.VISIBLE
                        notifyContainer.visibility = View.GONE
                    }
                    mtvTitle.text = getString(com.mcash.client.R.string.notify)
                }

                val notifyAdapter = NotifyAdapter(object : UpdateNotification {
                    override fun onClick() {
                        lifecycleScope.launch {
                            list?.forEach {
                                val entity = NotificationEntity(it.id, it.title, it.message, it.date, true)
                                settingsViewModel.updateNotification(entity)
                            }
                        }
                    }

                })
                notifyRecycler.apply {
                    adapter = notifyAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
                notifyAdapter.submitList(list)

            }



            //faq click
            faq.setOnClickListener {
                faqContainer.visibility = View.VISIBLE
                helpContainer.visibility = View.GONE
                settingsList.visibility = View.GONE
                changePinContainer.visibility = View.GONE
                aboutContainer.visibility = View.GONE
                notifyContainer.visibility = View.GONE


                with(faqToolbar) {
                    btnBack.setOnClickListener {
                        settingsList.visibility = View.VISIBLE
                        changePinContainer.visibility = View.GONE
                        helpContainer.visibility = View.GONE
                        faqContainer.visibility = View.GONE
                    }
                    mtvTitle.text = getString(R.string.faq)
                }

                lifecycleScope.launch {
                    val list = settingsViewModel.getFAQList()

                    withContext(Dispatchers.Main) {
                        val adapter = FaqAdapter(list, requireContext())
                        faqRecycler.adapter = adapter
                        faqRecycler.layoutManager = LinearLayoutManager(requireContext())
                    }
                }

            }

            //help
            help.setOnClickListener {
                helpContainer.visibility = View.VISIBLE
                faqContainer.visibility = View.GONE
                settingsList.visibility = View.GONE
                changePinContainer.visibility = View.GONE
                aboutContainer.visibility = View.GONE
                notifyContainer.visibility = View.GONE

                lifecycleScope.launch(Dispatchers.Main) {
                    //val x =  settingsViewModel.getHelpInfo()
                    //Log.d("X", "$x")
                    //val encodedHtml = Base64.encodeToString(x.toByteArray(), Base64.NO_PADDING)
                    //helpInfo.loadData(encodedHtml, "text/html", "base64")
                    helpInfo.loadUrl("https://newtest.mcash.ug/wallet/help")
                    helpInfo.settings.javaScriptEnabled = true
                }

                with(helpToolbar) {
                    btnBack.setOnClickListener {
                        settingsList.visibility = View.VISIBLE
                        changePinContainer.visibility = View.GONE
                        helpContainer.visibility = View.GONE
                        faqContainer.visibility = View.GONE
                    }
                    mtvTitle.text = getString(R.string.help)
                }

            }

            //about
            about.setOnClickListener {
                aboutContainer.visibility = View.VISIBLE
                faqContainer.visibility = View.GONE
                helpContainer.visibility = View.GONE
                settingsList.visibility = View.GONE
                changePinContainer.visibility = View.GONE
                notifyContainer.visibility = View.GONE

                aboutSocials.visibility = View.VISIBLE


                with(aboutToolbar) {
                    btnBack.setOnClickListener {
                        settingsList.visibility = View.VISIBLE
                        changePinContainer.visibility = View.GONE
                        helpContainer.visibility = View.GONE
                        faqContainer.visibility = View.GONE
                        aboutContainer.visibility = View.GONE
                    }
                    mtvTitle.text = getString(R.string.about)
                }

                    website.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mcash.ug/"))
                        startActivity(intent)
                    }

                    linkedIn.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/m-cash-uganda/"))
                        startActivity(intent)
                    }

                    x.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Mcashuganda"))
                        startActivity(intent)

                    }

            }

            //logout
            btnLogout.setOnClickListener {
                lifecycleScope.launch {
                    settingsViewModel.signOut()

                    withContext(Dispatchers.Main) {
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }


            with(homeViewModel) {

                changePinState.observe(viewLifecycleOwner) {
                    when(it) {
                        is ChangePinState.Loading -> showProgressDialog()
                        is ChangePinState.Error -> {
                            hideProgressDialog()
                            //signOut(it.message)
                            showAlert(
                                getString(R.string.error),
                                it.message,
                                com.mcash.client.core.utils.AlertType.FAILURE
                            )
                        }
                        is ChangePinState.Success -> {
                            hideProgressDialog()
//                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                        finish()
                            val activity = requireActivity() as MainActivity

                            activity.showSuccessMessage(
                                "Reset Pin",
                                "Pin Successfully Changed",
                                1,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        settingsList.visibility = View.VISIBLE
                                        changePinContainer.visibility = View.GONE
                                        //navigate(SettingsFragmentDirections.actionSettingsFragmentToFragmentHome())
                                        exitTransition
                                    }
                                }
                            )

                        }
                        else -> {}
                    }
                }

            }


            btnReset.setOnClickListener {
                when{
                    oldPin.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Old Pin is required",
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }
                    newPin1.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "New Pin is required",
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }
                    newPin2.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Please Confirm Pin",
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }
                    newPin1.text.toString() != newPin2.text.toString() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "New Pins should match",
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }
                    else -> {
                        homeViewModel.changePin(
                            oldPin.text.toString(),
                            newPin1.text.toString(),
                            device_id = getDeviceID(),
                            model = getPhoneModel())
                    }
                }
            }

        }

    }
}
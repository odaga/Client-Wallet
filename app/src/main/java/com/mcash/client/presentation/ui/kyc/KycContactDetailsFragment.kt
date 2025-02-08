package com.mcash.client.presentation.ui.kyc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.AlertType
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.databinding.FragmentKycContactDetailsBinding
import com.mcash.client.presentation.ui.kyc.ClientBioFragment.Companion
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class KycContactDetailsFragment :
    BaseFragment<FragmentKycContactDetailsBinding>(FragmentKycContactDetailsBinding::inflate) {

    private val formViewModel: FormViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            formLabel = arguments?.getString("FORM_GROUP_LABEL").toString()
        }

        with(binding) {
            btnSave.setOnClickListener {
                if (phoneNumberField.text.toString().isEmpty()) {
                    showAlert(
                        "Required Input",
                        "Please enter your ${phoneNumberField.hint} to proceed",
                        AlertType.INFO,
                        2000
                    )
                } else if (emailField.text.toString().isEmpty()) {
                    showAlert(
                        "Required Input",
                        "Please enter your ${emailField.hint} to proceed",
                        AlertType.INFO,
                        2000
                    )
                } else if (addressField.text.toString().isEmpty()) {
                    showAlert(
                        "Required Input",
                        "Please enter your ${addressField.hint} to proceed",
                        AlertType.INFO,
                        2000
                    )
                } else {
                    runBlocking {
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap["phoneNumber"] = phoneNumberField.text.toString()
                        hashMap["emailAddress"] = emailField.text.toString()
                        hashMap["physicalAddress"] = addressField.text.toString()
                        formViewModel.saveKycFormData(
                            convertMapsToJson(hashMap),
                            formLabel,
                            FORM_TYPE
                        )
                        navigateUp()
                    }
                }
            }
        }
    }

    companion object {
        private var formLabel = ""
        private const val FORM_TYPE = "Text"
    }
}
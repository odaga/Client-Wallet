package com.mcash.client.presentation.ui.kyc

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.AlertType
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.databinding.FragmentKycNextOfKinBinding
import com.mcash.client.presentation.ui.kyc.KycContactDetailsFragment.Companion
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class KycNextOfKinFragment :
    BaseFragment<FragmentKycNextOfKinBinding>(FragmentKycNextOfKinBinding::inflate) {

    private val formViewModel: FormViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            if (arguments != null) {
                formLabel = arguments?.getString("FORM_GROUP_LABEL").toString()
            }

            btnSave.setOnClickListener {

                if (fullNameField.text.toString().isEmpty()) {
                    showAlert(
                        "Required Input",
                        "Please enter your ${fullNameField.hint} to proceed",
                        AlertType.INFO,
                        2000
                    )
                } else if (phoneNumberField.text.toString().isEmpty()) {
                    showAlert(
                        "Required Input",
                        "Please enter your ${phoneNumberField.hint} to proceed",
                        AlertType.INFO,
                        2000
                    )
                } else {
                    runBlocking {
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap["full_name"] = fullNameField.text.toString()
                        hashMap["nextOfKinPhoneNumber"] = phoneNumberField.text.toString()

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
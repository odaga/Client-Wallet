package com.mcash.client.presentation.ui.kyc

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.AlertType
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.databinding.FragmentClientBioBinding
import com.mcash.client.presentation.ui.kyc.KycContactDetailsFragment.Companion
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class ClientBioFragment :
    BaseFragment<FragmentClientBioBinding>(FragmentClientBioBinding::inflate) {

    private val formViewModel: FormViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (arguments != null) {
            formLabel = arguments?.getString("FORM_GROUP_LABEL").toString()
        }

        with(binding) {
            scanIdCodeCard.setOnClickListener {
                val bundle = Bundle()
                bundle.putBoolean("FIRST_ONBOARDING", true)
                navigateWithBundle(R.id.action_clientBioFragment_to_idScanFragment2, bundle)
            }

            btnSave.setOnClickListener {
                if (firstNameField.text.toString().isEmpty()) {
                    showAlert(
                        "Required Input",
                        "Please enter your ${firstNameField.hint} to proceed",
                        AlertType.INFO,
                        2000
                    )
                } else if (lastNameField.text.toString().isEmpty()) {
                    showAlert(
                        "Required Input",
                        "Please enter your ${lastNameField.hint} to proceed",
                        AlertType.INFO,
                        2000
                    )

                } else if (genderField.text.toString().isEmpty()) {
                    showAlert(
                        "Required Input",
                        "Please enter your ${genderField.hint} to proceed",
                        AlertType.INFO,
                        2000
                    )

                } else if (dateOfBirthField.text.toString().isEmpty()) {
                    showAlert(
                        "Required Input",
                        "Please enter your ${dateOfBirthField.hint} to proceed",
                        AlertType.INFO,
                        2000
                    )
                } else if (ninField.text.toString().isEmpty()) {
                    showAlert(
                        "Required Input",
                        "Please enter your ${ninField.hint} to proceed",
                        AlertType.INFO,
                        2000
                    )
                } else {
                    runBlocking {
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap["firstName"] = firstNameField.text.toString()
                        hashMap["lastName"] = lastNameField.text.toString()
                        hashMap["gender"] = genderField.text.toString()
                        hashMap["dateOfBirth"] = dateOfBirthField.text.toString()
                        hashMap["nin"] = ninField.text.toString()
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
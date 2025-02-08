package com.mcash.client.presentation.ui.kyc

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.databinding.FormGroupItemBinding
import com.mcash.client.databinding.FragmentKycFormGroupBinding
import com.mcash.client.presentation.ui.kyc.FaceCameraFragment.Companion
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class KycFormGroupFragment :
    BaseFragment<FragmentKycFormGroupBinding>(FragmentKycFormGroupBinding::inflate) {

    private val formViewModel: FormViewModel by viewModels()

    private val formGroup = listOf(
        "Contact Details",
        "Bio Information",
        "Next of Kin Details",
        "Face Photo",
        "Id Front Photo",
        "Id Back Photo"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            with(formViewModel) {
                verifyKycBtn.setOnClickListener {
                    if (allPermissionsGranted()) {
                        uploadKycForm()
                    } else {
                        requestPermissions()
                    }
                }

                formUploadState.observe(viewLifecycleOwner) {
                    when (it) {
                        is FormViewModel.UploadFormState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)
                        }

                        FormViewModel.UploadFormState.Initial -> Unit
                        FormViewModel.UploadFormState.Loading -> {
                            showProgressDialog()
                        }

                        is FormViewModel.UploadFormState.Success -> {
                            hideProgressDialog()
                            startActivity(
                                Intent(
                                    getParentActivity(),
                                    OnboardingStatusActivity::class.java
                                )
                            )
                        }
                    }
                }
            }


            val formGroupAdapter = FormGroupAdapter(formGroup,
                object : FormGroupClickListener {
                    override fun onGroupClicked(formLabel: String) {
                        val bundle = Bundle()
                        when (formLabel) {

                            "Contact Details" -> {
                                bundle.putString("FORM_GROUP_LABEL", formLabel)
                                navigateWithBundle(
                                    R.id.action_kycFormGroupFragment_to_kycContactDetailsFragment,
                                    bundle
                                )
                            }

                            "Bio Information" -> {
                                bundle.putString("FORM_GROUP_LABEL", formLabel)
                                navigateWithBundle(
                                    R.id.action_kycFormGroupFragment_to_clientBioFragment,
                                    bundle
                                )
                            }

                            "Next of Kin Details" -> {
                                bundle.putString("FORM_GROUP_LABEL", formLabel)
                                navigateWithBundle(
                                    R.id.action_kycFormGroupFragment_to_kycNextOfKinFragment,
                                    bundle
                                )
                            }

                            "Face Photo" -> {
                                bundle.putString("FORM_GROUP_LABEL", formLabel)
                                navigateWithBundle(
                                    R.id.action_kycFormGroupFragment_to_faceCameraFragment,
                                    bundle
                                )
                            }

                            "Id Front Photo" -> {
                                bundle.putString("FORM_GROUP_LABEL", formLabel)
                                navigateWithBundle(
                                    R.id.action_kycFormGroupFragment_to_faceCameraFragment,
                                    bundle
                                )
                            }

                            "Id Back Photo" -> {
                                bundle.putString("FORM_GROUP_LABEL", formLabel)
                                navigateWithBundle(
                                    R.id.action_kycFormGroupFragment_to_faceCameraFragment,
                                    bundle
                                )
                            }

                            else -> Unit
                        }
                    }
                })

            formGroupRv.adapter = formGroupAdapter
            formGroupRv.layoutManager = LinearLayoutManager(requireContext())

        }
    }


    inner class FormGroupAdapter(
        private val formGroups: List<String>,
        private val formGroupClickListener: FormGroupClickListener
    ) : RecyclerView.Adapter<FormGroupAdapter.ViewHolder>() {

        inner class ViewHolder(
            val binding: FormGroupItemBinding
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                FormGroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        override fun getItemCount(): Int {
            return formGroups.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val group = formGroups[position]

            with(holder.binding) {
                runBlocking {
                    label.text = group
                    formGroupCardNumber.text = buildString {
                        append((position + 1))
                    }
                    if (formViewModel.getFormSaveStatus(group))
                        formGroupCheck.isVisible = true
                }
                card.setOnClickListener {
                    formGroupClickListener.onGroupClicked(group)
                }
            }
        }
    }

    private fun uploadKycForm() {
        val location = passiveProvider(requireContext())
        location?.let {
            formViewModel.uploadKycForm(
                getDeviceID(),
                getPhoneModel(),
                getAndroidVersion().toString(),
                it.longitude.toString(),
                it.latitude.toString()
            )
        }
    }


    interface FormGroupClickListener {
        fun onGroupClicked(formLabel: String)
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                uploadKycForm()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            uploadKycForm()
        } else {
            Toast.makeText(requireContext(), "Permission denied by user", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "KycFormGroupFragment"
        private var formLabel = ""
        private const val REQUEST_CODE_PERMISSIONS = 101
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
            .apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }


}
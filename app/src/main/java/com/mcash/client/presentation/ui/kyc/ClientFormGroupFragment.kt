package com.mcash.client.presentation.ui.kyc

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.databinding.FragmentClientFormGroupBinding
import com.mcash.client.presentation.ui.auth.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientFormGroupFragment :
    BaseFragment<FragmentClientFormGroupBinding>(FragmentClientFormGroupBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            btnGetStarted.setOnClickListener {
                navigate(ClientFormGroupFragmentDirections.actionClientFormGroupFragmentToKycFormGroupFragment())
            }
            signInText.setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }

}
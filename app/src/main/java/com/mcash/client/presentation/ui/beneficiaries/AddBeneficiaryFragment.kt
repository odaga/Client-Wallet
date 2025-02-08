package com.mcash.client.presentation.ui.beneficiaries

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mcash.client.R
import com.mcash.client.databinding.FragmentAddBeneficiaryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBeneficiaryFragment : Fragment() {

    private var _binding: FragmentAddBeneficiaryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddBeneficiaryBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_add_beneficiary_to_fragment_beneficiary_list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
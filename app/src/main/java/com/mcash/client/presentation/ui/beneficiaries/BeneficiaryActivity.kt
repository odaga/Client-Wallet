package com.mcash.client.presentation.ui.beneficiaries

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.mcash.client.R
import com.mcash.client.databinding.ActivityBeneficiaryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BeneficiaryActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityBeneficiaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBeneficiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_beneficiary)
        appBarConfiguration = AppBarConfiguration(navController.graph)
    }
}
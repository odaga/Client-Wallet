package com.mcash.client.presentation.ui.kyc

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mcash.client.R
import com.mcash.client.core.base.BaseActivity
import com.mcash.client.databinding.ActivityOnboardingStatusBinding
import com.mcash.client.presentation.ui.auth.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingStatusActivity : BaseActivity() {
    private lateinit var binding: ActivityOnboardingStatusBinding

    private val formViewModel: FormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnHome.setOnClickListener {
                startActivity(Intent(this@OnboardingStatusActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
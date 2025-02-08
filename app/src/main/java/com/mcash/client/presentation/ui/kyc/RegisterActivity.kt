package com.mcash.client.presentation.ui.kyc

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.mcash.client.R
import com.mcash.client.core.base.BaseActivity
import com.mcash.client.databinding.ActivityMainBinding
import com.mcash.client.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var navController: NavController


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {

        }
    }
}
package com.mcash.client.presentation.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.auth.LoginActivity


class AutoLogoutService: Service() {
    val activity = MainActivity() as Context

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                startActivity(Intent(activity, LoginActivity::class.java))
                stopSelf()
                //startActivity(intent)
            }
        }.start()

        return START_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}


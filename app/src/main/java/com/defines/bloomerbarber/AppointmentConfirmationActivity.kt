package com.defines.bloomerbarber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class AppointmentConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_confirmation)
        Handler(Looper.getMainLooper()).postDelayed({
            onBackPressed()
        },2000)
    }
}
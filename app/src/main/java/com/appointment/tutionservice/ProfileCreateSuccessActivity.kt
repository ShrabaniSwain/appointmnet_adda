package com.appointment.tutionservice

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appointment.tutionservice.databinding.ActivityProfileCreateSuccessBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileCreateSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileCreateSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileCreateSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
                val intent = Intent(applicationContext, LoginMobileNoHome::class.java)
                startActivity(intent)
                Toast.makeText(applicationContext, "Login with your registered mobile number", Toast.LENGTH_SHORT).show()
                finish()
        }

    }
}
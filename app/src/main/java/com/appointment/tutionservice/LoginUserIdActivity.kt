package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appointment.tutionservice.databinding.ActivityLoginUserIdBinding

class LoginUserIdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginUserIdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUserIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, CustomerMainActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, RegisterNowActivity::class.java)
            startActivity(intent)
        }

        binding.tvUserIdPass.setOnClickListener {
            val intent = Intent(this, LoginMobileNoHome::class.java)
            startActivity(intent)
        }


    }
}
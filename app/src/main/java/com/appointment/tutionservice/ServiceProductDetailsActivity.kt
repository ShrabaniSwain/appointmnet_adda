package com.appointment.tutionservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appointment.tutionservice.databinding.ActivityServiceProductDetailsBinding

class ServiceProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
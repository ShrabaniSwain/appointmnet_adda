package com.appointment.tutionservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appointment.tutionservice.databinding.ActivitySpecialPriceBinding

class SpecialPriceActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpecialPriceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpecialPriceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}
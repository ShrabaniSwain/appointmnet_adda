package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivityMyProductBinding

class MyProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateButtonColors(true)

        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(applicationContext, AddProductActivity::class.java)
            startActivity(intent)
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val productAdapter = ProductAdapter()
        binding.rvService.layoutManager = LinearLayoutManager(applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvService.adapter = productAdapter


        binding.btnAppointment.setOnClickListener {
            updateButtonColors(false)
        }

        binding.btnEnquiry.setOnClickListener {
            updateButtonColors(true)
        }

    }

    private fun updateButtonColors(isEnquirySelected: Boolean) {
        if (isEnquirySelected) {
            binding.btnEnquiry.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.btnAppointment.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            binding.btnEnquiry.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red))
            binding.btnAppointment.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.grey))
        } else {
            binding.btnEnquiry.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            binding.btnAppointment.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.btnEnquiry.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.grey))
            binding.btnAppointment.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red))
        }
    }
}
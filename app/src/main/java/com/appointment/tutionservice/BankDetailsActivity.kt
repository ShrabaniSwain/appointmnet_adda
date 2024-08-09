package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appointment.tutionservice.databinding.ActivityBankDetailsBinding

class BankDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBankDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, PackageActivity::class.java)
            intent.putExtra("CARD_TYPE", Constant.CUSTOMER_CARD)
            startActivity(intent)
        }

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, LoginUserIdActivity::class.java)
            startActivity(intent)
        }
    }
}
package com.appointment.tutionservice

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appointment.tutionservice.databinding.ActivityNotificationDetailsBinding
import com.appointment.tutionservice.databinding.ActivityProviderNotificationBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NotificationDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationDetailsBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        Glide.with(this)
            .load(Constant.NOTIFICATION_IMAGE)
            .apply(RequestOptions.placeholderOf(R.drawable.noimageavailbale))
            .into(binding.ivImage)

        binding.tvNotificationDeatils.text = Constant.NOTIFICATION_DETAILS + "\n" + Constant.ADDITIONAL_DATA
    }
}
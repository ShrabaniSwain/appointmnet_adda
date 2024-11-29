package com.appointment.tutionservice

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivityProviderNotificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProviderNotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProviderNotificationBinding
    private lateinit var notification: List<Notification>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllNotifications()

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getAllNotifications() {
        val call = RetrofitClient.api.getAllNotifications(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                Log.i("TAG", "onResponse: " + response)
                if (response.isSuccessful) {
                    notification = response.body()?.data?.notifications ?: emptyList()

                    if (notification.isEmpty()){
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                    else {
                        val adapter = ProviderNotificationAdapter(notification, applicationContext)
                        binding.rvNotification.adapter = adapter
                        binding.rvNotification.layoutManager = LinearLayoutManager(
                            applicationContext,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }

                    Log.i("TAG", "bannerImage: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "bannerImage: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

            }
        })
    }
}
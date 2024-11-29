package com.appointment.tutionservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivityNotificationDetailsBinding
import com.appointment.tutionservice.databinding.ActivityVideoPlayBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VIdeoPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayBinding
    private lateinit var notification: List<TutorialData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoTutoriaList()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun videoTutoriaList() {
        binding.progressBar.visibility = View.VISIBLE
        val call = RetrofitClient.api.videoTutoriaList(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<TutorialResponse> {
            override fun onResponse(call: Call<TutorialResponse>, response: Response<TutorialResponse>) {
                if (response.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    notification = response.body()?.data ?: emptyList()
                    if (notification.isEmpty()){
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                    else {
                        val adapter = VideoAdapterList(notification, applicationContext)
                        binding.rvVideo.adapter = adapter
                        binding.rvVideo.layoutManager = LinearLayoutManager(
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

            override fun onFailure(call: Call<TutorialResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

            }
        })
    }
}
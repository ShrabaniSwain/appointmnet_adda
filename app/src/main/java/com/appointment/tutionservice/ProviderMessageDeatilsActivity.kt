package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivityProviderMessageDeatilsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProviderMessageDeatilsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProviderMessageDeatilsBinding
    private var message: List<JobPostMessageData> = mutableListOf()
    private lateinit var messageAdapter: CustomerMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderMessageDeatilsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getJobPostMessages()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString()
            if (message.isBlank()) {
                Toast.makeText(
                    applicationContext,
                    "Please enter some text to send",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val messageData = JobPostMessage(
                    Constant.JOB_POST_ID,
                    Constant.CUSTOMER_ID,
                    message,
                    0,
                    Constant.MOBILE_NO,
                    Constant.API_KEY,
                    Utility.getDeviceId(applicationContext),
                    Utility.deviceType,
                    Utility.deviceToken,
                    Constant.APP_USER_ID.toString(),
                    Constant.APP_VERSION_NAME.toString(),
                    Constant.APP_USER_KEY,
                    0.0,
                    0.0
                )
                Log.i("TAG", "onCreate: $messageData")
                sendJobPostMessage(messageData)
            }
        }
    }

    private fun getJobPostMessages() {
        showProgressBar()

        val call = RetrofitClient.api.getJobPostMessages(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString(),
            Constant.JOB_POST_ID,
            Constant.CUSTOMER_ID
        )
        call.enqueue(object : Callback<JobPostMessagesResponse> {
            override fun onResponse(call: Call<JobPostMessagesResponse>, response: Response<JobPostMessagesResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        hideProgressBar()
                        val business = response.body()
                        message = business?.data?.jobPostMessages ?: emptyList()
                        messageAdapter = CustomerMessageAdapter(this@ProviderMessageDeatilsActivity, message)
                        binding.recyclerViewChat.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                        binding.recyclerViewChat.adapter = messageAdapter

                        Log.i("TAG", "getServiceNameByType: ${response.body()}")
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<JobPostMessagesResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "getServiceNameByType: ${t.message}")

            }
        })
    }

    private fun sendJobPostMessage(messageData: JobPostMessage) {
        showProgressBar()
        val call = RetrofitClient.api.sendJobPostMessage(messageData)
        call.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                hideProgressBar()
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        val updateProfileResponse = response.body()
                        Log.i("TAG", "onResponse: $updateProfileResponse")
                        val intent = Intent(applicationContext, ProviderMessageDeatilsActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(applicationContext, updateProfileResponse?.msg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "appUserServicesUpdate onResponse error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "appUserServicesUpdate onFailure: ${t.message}")
            }
        })
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}
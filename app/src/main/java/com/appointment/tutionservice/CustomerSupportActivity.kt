package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.Constant.API_KEY
import com.appointment.tutionservice.Constant.APP_USER_ID
import com.appointment.tutionservice.Constant.APP_USER_KEY
import com.appointment.tutionservice.Constant.APP_VERSION_NAME
import com.appointment.tutionservice.Constant.MOBILE_NO
import com.appointment.tutionservice.databinding.ActivityCustomerSupportBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerSupportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerSupportBinding
    private lateinit var support: List<SupportComplain>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val supportComplain = ServiceComplainData(MOBILE_NO,  API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceType,
            Utility.deviceToken,
            APP_USER_ID.toString(),
            APP_VERSION_NAME.toString(),
            APP_USER_KEY,
            0.0,
            0.0)
        getServiceComplain(supportComplain)

        binding.btnSendEnquiry.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val etSubjectSelect = binding.etSubjectSelect.text.toString()
            val etRequirements = binding.etRequirements.text.toString()

            if (etSubjectSelect.isBlank() || etRequirements.isBlank()) {
                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }else {
                sendServiceComplain(
                    etSubjectSelect,
                    etRequirements,
                    MOBILE_NO,
                    API_KEY,
                    Utility.getDeviceId(applicationContext),
                    Utility.deviceType,
                    Utility.deviceToken,
                    APP_USER_ID.toString(),
                    APP_VERSION_NAME.toString(),
                    APP_USER_KEY,
                    0.0,
                    0.0
                )
            }
        }
    }

    private fun sendServiceComplain(
        subject: String,
        message: String,
        user_mobile: String,
        api_key: String,
        device_id: String,
        device_type: String,
        device_token: String,
        app_user_id: String,
        app_version: String,
        app_user_key: String,
        lat: Double,
        lng: Double
    ) {
        val addUserDetails = SendServiceRequest(subject,message,user_mobile,api_key,device_id,device_type,device_token,app_user_id,app_version,app_user_key,lat,lng)
        Log.i("TAG", "addCustomer: $addUserDetails")
        val call = RetrofitClient.api.sendServiceComplain(addUserDetails)
        call.enqueue(object : Callback<ServiceResponse> {
            override fun onResponse(call: Call<ServiceResponse>, response: Response<ServiceResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.status == 1) {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, CustomerMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ServiceResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getServiceComplain(supportComplain: ServiceComplainData) {
        binding.progressBar.visibility = View.VISIBLE

        val call = RetrofitClient.api.getServiceComplain(supportComplain)
        call.enqueue(object : Callback<SupportComplainResponse> {
            override fun onResponse(call: Call<SupportComplainResponse>, response: Response<SupportComplainResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.status == 1) {
                            support = response.body()?.data ?: emptyList()
                            val supportDeatilsAdapter = SupportDeatilsAdapter(support)
                            binding.rvSupport.layoutManager =
                                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                            binding.rvSupport.adapter = supportDeatilsAdapter
                            binding.progressBar.visibility = View.GONE
                        } else {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        response.body()?.msg,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visibility = View.GONE
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SupportComplainResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}
package com.appointment.tutionservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.appointment.tutionservice.databinding.ActivityAppointmentListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppointmentListBinding
    private var appointment: List<Business> = mutableListOf()
    private lateinit var appointmentListAdapter: AppointmentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getServiceDetails()
        binding.tvTitle.text = Constant.JOBTITLE
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun getServiceDetails() {
        showProgressBar()

        val call = RetrofitClient.api.getBookAppointment(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString(), Constant.SERVICE_ID, "1"
        )
        val mobileNo = Constant.MOBILE_NO
        val apiKey = Constant.API_KEY
        val deviceId = Utility.getDeviceId(applicationContext)
        val deviceToken = Utility.deviceToken
        val appUserKey = Constant.APP_USER_KEY
        val appUserId = Constant.APP_USER_ID.toString()
        val appVersionName = Constant.APP_VERSION_NAME.toString()
        val serviceId = Constant.SERVICE_ID
        val someValue = "1"

        Log.d("Constants", "MOBILE_NO: $mobileNo, API_KEY: $apiKey, DEVICE_ID: $deviceId, DEVICE_TOKEN: $deviceToken, APP_USER_KEY: $appUserKey, APP_USER_ID: $appUserId, APP_VERSION_NAME: $appVersionName, SERVICE_ID: $serviceId, SOME_VALUE: $someValue")
        call.enqueue(object : Callback<BusinessResponse> {
            override fun onResponse(call: Call<BusinessResponse>, response: Response<BusinessResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        hideProgressBar()
                        val business = response.body()
                        appointment = business?.business ?: emptyList()
                        appointmentListAdapter = AppointmentListAdapter(applicationContext, appointment)
                        binding.rvService.adapter = appointmentListAdapter
                        binding.rvService.layoutManager = GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)

                        Log.i("TAG", "getServiceNameByType: ${response.body()}")
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<BusinessResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "getServiceNameByType: ${t.message}")

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
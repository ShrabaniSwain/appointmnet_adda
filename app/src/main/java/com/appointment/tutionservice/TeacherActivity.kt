package com.appointment.tutionservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.appointment.tutionservice.databinding.ActivityTeacherBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherBinding
    private var service: List<ServiceItem> = mutableListOf()
    private lateinit var teacherAdapter: TeacherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getServiceNameByType()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.tvTitle.text = Constant.JOBTITLE

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = p0.toString()

                val filteredList = service.filter { marketDetail ->
                    marketDetail.service_name.contains(searchText, ignoreCase = true)
                }

                teacherAdapter.updateList(filteredList)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

    }

    private fun getServiceNameByType() {
        showProgressBar()

        val call = RetrofitClient.api.getServiceNameByType(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString(), Constant.SERVICE_CAT_TYPE, Constant.CAT_TYPE, "0"
        )

        Log.d("TAG", "MOBILE_NO: ${Constant.MOBILE_NO}")
        Log.d("TAG", "API_KEY: ${Constant.API_KEY}")
        Log.d("TAG", "Device ID: ${Utility.getDeviceId(applicationContext)}")
        Log.d("TAG", "Device Token: ${Utility.deviceToken}")
        Log.d("TAG", "APP_USER_KEY: ${Constant.APP_USER_KEY}")
        Log.d("TAG", "APP_USER_ID: ${Constant.APP_USER_ID}")
        Log.d("TAG", "APP_VERSION_NAME: ${Constant.APP_VERSION_NAME}")
        Log.d("TAG", "SERVICE_CAT_TYPE: ${Constant.SERVICE_CAT_TYPE}")
        Log.d("TAG", "CAT_TYPE: ${Constant.CAT_TYPE}")

        call.enqueue(object : Callback<ServiceProviderResponse> {
            override fun onResponse(call: Call<ServiceProviderResponse>, response: Response<ServiceProviderResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        hideProgressBar()
                        service = response.body()!!.data.service_name
                        teacherAdapter = TeacherAdapter(service, applicationContext)
                        binding.rvService.adapter = teacherAdapter
                        binding.rvService.layoutManager = GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)


                        Log.i("TAG", "getServiceNameByType: ${response.body()}")
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ServiceProviderResponse>, t: Throwable) {
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
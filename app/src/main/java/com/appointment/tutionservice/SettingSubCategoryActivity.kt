package com.appointment.tutionservice

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivitySettingSubCategoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingSubCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingSubCategoryBinding
    private var service: List<ServiceItem> = mutableListOf()
    private lateinit var adapter: SubCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingSubCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivClose.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        getServiceNameByType()

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = p0.toString()

                val filteredList = service.filter { marketDetail ->
                    marketDetail.service_name.contains(searchText, ignoreCase = true)
                }

                adapter.updateList(filteredList)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.btnNext.setOnClickListener {
            val selectedServices = adapter.getSelectedServices()
            if (selectedServices.isNotEmpty()) {
                val serviceCategoryResponse = ServiceCategoryResponse(
                    services = selectedServices,
                    app_user_type = "2",
                    user_mobile = Constant.MOBILE_NO,
                    api_key = Constant.API_KEY,
                    device_id = Utility.getDeviceId(applicationContext),
                    device_type = Utility.deviceType,
                    device_token = Utility.deviceToken,
                    app_user_id = Constant.APP_USER_ID.toString(),
                    app_version = Constant.APP_VERSION_NAME.toString(),
                    app_user_key = Constant.APP_USER_KEY,
                    lat = 0.0,
                    lng = 0.0
                )
                Log.i("TAG", "onCreate: $serviceCategoryResponse")
                callAppUserServicesUpdate(serviceCategoryResponse)
            } else {
                Toast.makeText(applicationContext, "Please select at least one service", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun callAppUserServicesUpdate(serviceCategoryResponse: ServiceCategoryResponse) {
        showProgressBar()
        val call = RetrofitClient.api.appUserServicesUpdate(serviceCategoryResponse)
        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                hideProgressBar()
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        val updateProfileResponse = response.body()
                        Log.i("TAG", "onResponse: $updateProfileResponse")
                        val intent = Intent(applicationContext, SettingsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "appUserServicesUpdate onResponse error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "appUserServicesUpdate onFailure: ${t.message}")
            }
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
            Constant.APP_VERSION_NAME.toString(), "all", "5", "0"
        )
        call.enqueue(object : Callback<ServiceProviderResponse> {
            override fun onResponse(call: Call<ServiceProviderResponse>, response: Response<ServiceProviderResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        hideProgressBar()
                        service = response.body()!!.data.service_name
                        adapter = SubCategoryAdapter(service, applicationContext)
                        binding.rvList.adapter = adapter
                        binding.rvList.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)


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
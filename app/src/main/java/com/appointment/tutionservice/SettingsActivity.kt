package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivitySettingsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var service: List<ServiceItem> = mutableListOf()
    private lateinit var adapter: SettingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getServiceNameByType()

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnNext.setOnClickListener {
            val intent = Intent(applicationContext, ProfileCurrentLocationActivity::class.java)
            startActivity(intent)
        }
        binding.location.setOnClickListener {
            val intent = Intent(applicationContext, ProfileCurrentLocationActivity::class.java)
            startActivity(intent)
        }
        binding.scanned.setOnClickListener {
            val intent = Intent(applicationContext, ProfileScannerActivity::class.java)
            startActivity(intent)
        }
        binding.profile.setOnClickListener {
            val intent = Intent(applicationContext, ProviderProfileActivity::class.java)
            startActivity(intent)
        }

        binding.etSearch.setOnClickListener {
            val intent = Intent(applicationContext, SettingSubCategoryActivity::class.java)
            startActivity(intent)
        }

//        binding.etSearch.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                val searchText = p0.toString()
//
//                val filteredList = service.filter { marketDetail ->
//                    marketDetail.service_name.contains(searchText, ignoreCase = true)
//                }
//
//                adapter.updateList(filteredList)
//            }
//
//            override fun afterTextChanged(p0: Editable?) {}
//        })

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
            Constant.APP_VERSION_NAME.toString(), "all", "5","0"
        )
        call.enqueue(object : Callback<ServiceProviderResponse> {
            override fun onResponse(call: Call<ServiceProviderResponse>, response: Response<ServiceProviderResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        hideProgressBar()
                        service = response.body()!!.data.service_name
                        adapter = SettingAdapter(service, applicationContext)
                        binding.rvList.adapter = adapter
                        adapter.updateList(service)
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
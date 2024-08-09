package com.appointment.tutionservice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivityMoreBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMoreBinding
    private lateinit var featuredService: List<FeaturedService>
    private var service: List<FeaturedService> = mutableListOf()
    private lateinit var customerServiceAdapter: MoreServiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = p0.toString()

                val filteredList = service.filter { marketDetail ->
                    marketDetail.service_name.contains(searchText, ignoreCase = true)
                }

                customerServiceAdapter.updateList(filteredList)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        fetchBannerData()
    }

    private fun fetchBannerData() {
        showProgressBar()
        val call = RetrofitClient.api.getBanner(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    featuredService = response.body()?.data?.featuredServices ?: emptyList()
                    service = response.body()!!.data.featuredServices
                    customerServiceAdapter = MoreServiceAdapter(applicationContext, service)
                    binding.rvService.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    binding.rvService.adapter = customerServiceAdapter

                    Log.i("TAG", "bannerImage: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "bannerImage: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

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
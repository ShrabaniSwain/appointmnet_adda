package com.appointment.tutionservice

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivitySearchServiceBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchServiceBinding
    private var service: List<SearchBar> = mutableListOf()
    private lateinit var adapter: SearchServiceAdapter
    private  var appUserKey = Constant.APP_USER_KEY
    private  var appUserID = Constant.APP_USER_ID.toString()
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var type = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllServicesData()

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

                if (filteredList.isEmpty()){
                    type = 2
                }else{
                    type = 1
                }
                    if (searchText.length >= 3) {
                        val searchKeyword = SearchKeyword(searchText,type.toString(),"1",Constant.MOBILE_NO, Constant.API_KEY, Utility.getDeviceId(applicationContext),Utility.deviceType,Utility.deviceToken,
                            Constant.APP_USER_ID.toString(), Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, 0.0, 0.0)
                        saveKeywordToApi(searchKeyword)
                        Log.i("TAG", "onTextChanged: $searchKeyword")
                    }

                if (binding.etSearch.text.toString().isBlank()){
                    binding.rvService.visibility = View.GONE
                }else{
                    binding.rvService.visibility = View.VISIBLE
                }

                adapter = SearchServiceAdapter(service, applicationContext)
                binding.rvService.adapter = adapter
                binding.rvService.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                adapter.updateList(filteredList)

            }

            override fun afterTextChanged(p0: Editable?) {}
        })

    }

    private fun getAllServicesData() {
        val call = RetrofitClient.api.getAllServicesData(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            appUserKey,
            appUserID,
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<SearchBarResponse> {
            override fun onResponse(call: Call<SearchBarResponse>, response: Response<SearchBarResponse>) {
                if (response.isSuccessful) {

                    service = response.body()?.data ?: emptyList()

                    Log.i("TAG", "response: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "bannerImage: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<SearchBarResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

            }
        })
    }

    private fun saveKeywordToApi(keyword: SearchKeyword) {
        val call = RetrofitClient.api.saveKeyword(keyword)
        call.enqueue(object : Callback<KeywordResponse> {
            override fun onResponse(call: Call<KeywordResponse>, response: Response<KeywordResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        Log.i("TAG", "Keyword saved successfully: ${response.body()}")
                    }
                } else {
                    Log.e("TAG", "Error saving keyword: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<KeywordResponse>, t: Throwable) {
                Log.e("TAG", "Failed to save keyword: ${t.message}")
            }
        })
    }
}
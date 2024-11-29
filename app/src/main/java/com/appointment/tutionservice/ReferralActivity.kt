package com.appointment.tutionservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivityReferralBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReferralActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReferralBinding
    private var referral: List<ReferralIdData> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReferralBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUserUsedReferralId()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getUserUsedReferralId() {
        showProgressBar()

        val call = RetrofitClient.api.getUserUsedReferralId(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )
        call.enqueue(object : Callback<ReferralIdResponse> {
            override fun onResponse(call: Call<ReferralIdResponse>, response: Response<ReferralIdResponse>) {
                if (response.isSuccessful) {
                    Log.i("TAG", "onResponse: " + response)
                    if (response.body()?.status == 1) {
                        hideProgressBar()

                        referral = response.body()?.data ?: emptyList()

                        if (referral.isEmpty()){
                            binding.NoData.visibility = View.VISIBLE
                        }
                        else {
                            binding.NoData.visibility = View.GONE
                            val adapter = ReferralListAdapter(applicationContext, referral)
                            binding.rvReferralHistory.adapter = adapter
                            binding.rvReferralHistory.layoutManager = LinearLayoutManager(
                                applicationContext,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        }

                        Log.i("TAG", "response: ${response.body()}")
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ReferralIdResponse>, t: Throwable) {
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
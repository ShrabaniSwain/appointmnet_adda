package com.appointment.tutionservice

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivityWalletHistoryBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class WalletHistoryActivity : AppCompatActivity() , PaymentResultListener {
    private lateinit var binding: ActivityWalletHistoryBinding
    lateinit var amount: EditText
    private var wallet: List<Wallet> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getWalletHistoryData()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.rechargeBtn.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater =
                LayoutInflater.from(this).inflate(R.layout.wallet_dialog, null)
            dialogBuilder.setView(inflater)
            val close = inflater.findViewById<ImageView>(R.id.ivClose)
        amount = inflater.findViewById(R.id.tvAmount)
        val save = inflater.findViewById<TextView>(R.id.btnPay)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            close.setOnClickListener {
                dialog.dismiss()
            }


            save.setOnClickListener {

                if (amount.text.toString().isEmpty()){
                    Toast.makeText(
                        applicationContext,
                        "Please enter an amount",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    val price = (amount.text.toString().toFloat() * 100).roundToInt().toInt()
                    Checkout.preload(applicationContext)
                    val co = Checkout()
                    co.setKeyID(Constant.RAZORPAY_KEY)
                    val obj = JSONObject()
                    try {
                        obj.put("name", Constant.Provider_Name)
                        obj.put("description", "Payment")
                        obj.put("theme.color", "")
                        obj.put("currency", "INR")
                        obj.put("amount", price)
                        obj.put("prefill.contact", Constant.MOBILE_NO)
                        obj.put("prefill.email", Constant.EMAIL)
                        co.open(this@WalletHistoryActivity, obj)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    dialog.dismiss()
                }
            }


            dialog.show()
        }
    }

    private fun getWalletHistoryData() {
        val call = RetrofitClient.api.getWalletHistoryData(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<WalletResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WalletResponse>, response: Response<WalletResponse>) {
                if (response.isSuccessful) {

                    wallet = response.body()?.data ?: emptyList()

                    binding.tvTotalAmount.text = "₹" + (response.body()?.total_amount?.total ?: "0")
                    val adapter = WalletHistoryAdapter(applicationContext, wallet)
                    binding.rvWalletHistory.adapter = adapter
                    binding.rvWalletHistory.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    Log.i("TAG", "response: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "bannerImage: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<WalletResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

            }
        })
    }


    private fun rechargeWallet(paymentRequest: PaymentRequest) {
        val call = RetrofitClient.api.rechargeWallet(paymentRequest)
        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.status == 1) {
                            val intent = Intent(applicationContext, ProviderMainActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(applicationContext, "Wallet has been successfully recharged!", Toast.LENGTH_SHORT).show()
                        } else {
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
                        response.body()?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onPaymentSuccess(p0: String?) {
        try {
            val paymentDetails = mutableListOf<String>()
            if (p0 != null) {
                paymentDetails.add(p0)
            }
            val paymentRequest = PaymentRequest(amount.text.toString(), paymentDetails, Constant.MOBILE_NO, Constant.API_KEY, Utility.getDeviceId(applicationContext), Utility.deviceType,
                Utility.deviceToken, Constant.APP_USER_ID.toString(), Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, 0.0, 0.0)

            rechargeWallet(paymentRequest)
            Log.i("TAG", "onPaymentSuccess: $paymentRequest")
            Toast.makeText(applicationContext, "Payment is successful", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error in onPaymentSuccess: " + e.message)
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Failed due to error", Toast.LENGTH_SHORT).show();
    }
}
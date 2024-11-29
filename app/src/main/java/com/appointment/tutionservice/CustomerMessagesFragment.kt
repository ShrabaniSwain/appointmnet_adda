package com.appointment.tutionservice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.FragmentCustomerMessagesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerMessagesFragment : Fragment() {

    private lateinit var binding: FragmentCustomerMessagesBinding
    private var message: List<UserMessage> = mutableListOf()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCustomerMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAppUserMessages()
        binding.pullToRefresh.setOnRefreshListener {
            getAppUserMessages()

        }
    }

    private fun getAppUserMessages() {
        showProgressBar()

        val call = RetrofitClient.api.getAppUserMessages(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(requireContext()),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString(), "0"
        )
        call.enqueue(object : Callback<UserMessagesResponse> {
            override fun onResponse(call: Call<UserMessagesResponse>, response: Response<UserMessagesResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        hideProgressBar()
                        binding.pullToRefresh.isRefreshing = false
                        val business = response.body()
                        message = business?.data?.appUserMessages ?: emptyList()
                        if (message.isEmpty()){
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                        else {
                            messageAdapter = MessageAdapter(requireContext(), message)
                            binding.rvMessage.layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            binding.rvMessage.adapter = messageAdapter
                        }

                        Log.i("TAG", "getServiceNameByType: ${response.body()}")
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<UserMessagesResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "getServiceNameByType: ${t.message}")

            }
        })
    }

    private fun showProgressBar() {
        if (isAdded) {
            binding.progressBar.visibility = View.VISIBLE
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    private fun hideProgressBar() {
        if (isAdded) {
            if (isAdded && activity != null) {
                binding.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

}
package com.appointment.tutionservice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.Constant.API_KEY
import com.appointment.tutionservice.Constant.APP_USER_ID
import com.appointment.tutionservice.Constant.APP_USER_KEY
import com.appointment.tutionservice.Constant.APP_VERSION_NAME
import com.appointment.tutionservice.Constant.MOBILE_NO
import com.appointment.tutionservice.databinding.FragmentCustomerNotificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerNotificationFragment : Fragment() {

    private lateinit var binding: FragmentCustomerNotificationBinding
    private lateinit var notification: List<Notification>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCustomerNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAllNotifications()
    }

    private fun getAllNotifications() {
        val call = RetrofitClient.api.getAllNotifications(
            MOBILE_NO,
            API_KEY,
            Utility.getDeviceId(requireContext()),
            Utility.deviceToken,
            APP_USER_KEY,
            APP_USER_ID.toString(),
            APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                if (response.isSuccessful) {
                    if (isAdded) {
                        notification = response.body()?.data?.notifications ?: emptyList()
                        if (notification.isEmpty()) {
                            binding.tvNoData.visibility = View.VISIBLE
                        } else {
                            val adapter =
                                CustometrNotificationAdapter(notification, requireContext())
                            binding.rvNotification.adapter = adapter
                            binding.rvNotification.layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        }
                    }

                    Log.i("TAG", "bannerImage: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "bannerImage: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

            }
        })
    }
}
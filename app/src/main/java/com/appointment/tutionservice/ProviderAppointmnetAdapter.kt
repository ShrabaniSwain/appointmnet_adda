package com.appointment.tutionservice

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appointment.tutionservice.databinding.ProviderAppointmentsItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class ProviderAppointmnetAdapter(val context: Context, val appointment: List<AppointmentProvider>) :
    RecyclerView.Adapter<ProviderAppointmnetAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.provider_appointments_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = appointment[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return appointment.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ProviderAppointmentsItemBinding.bind(itemView)

        fun bind(notificationText: AppointmentProvider) {
            Utility.itemBackGround(itemView)
            binding.tvName.text = notificationText.userProfileName
            binding.tvPriorityRate.text = notificationText.jobPriority
            binding.tvLocation.text = notificationText.city + "," + notificationText.state
            binding.tvEmail.text = notificationText.userEmail
            binding.tvPhoneNo.text = notificationText.userMobile
            binding.tvPrice.text = notificationText.customerBudget
            Log.i("TAG", "jobstaus: " + notificationText.applyJobPostStatus+ " " + notificationText.jobPostStatus)
            binding.tvDetails.text = notificationText.jobDescription

            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(notificationText.doc)
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val formattedDate = date?.let { dateFormat.format(it) }
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val formattedTime = date?.let { timeFormat.format(it) }
            binding.tvDate.text = formattedDate
            binding.tvTime.text = formattedTime

            binding.imageView.setOnClickListener {
                val phoneNumber = binding.tvPhoneNo.text.toString()
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$phoneNumber")
                context.startActivity(dialIntent)
            }

            binding.ivMessage.setOnClickListener {
                Constant.CUSTOMER_ID = notificationText.customerId
                Constant.JOB_POST_ID = notificationText.jobPostId
                val intent = Intent(context, ProviderMessageDeatilsActivity::class.java)
                itemView.context.startActivity(intent)
            }
            Glide.with(context).load(notificationText.service_image ?: "").into(binding.ivImage)


            val statusArray = arrayOf("Active", "Confirmed", "On the way for service", "Completed", "Cancelled", "Expired", "Pending", "In Progress", "Active")

            binding.tvRejected.setOnClickListener {
                val adapter =
                    ArrayAdapter(context, android.R.layout.simple_list_item_1, statusArray)
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Select Status")
                builder.setAdapter(adapter) { _, which ->
                    val selectedStatus = statusArray[which]
                    binding.tvRejected.text = selectedStatus
                    val status = when (selectedStatus) {
                        "Active" -> "1"
                        "Confirmed" -> "2"
                        "On the way for service" -> "3"
                        "Completed" -> "4"
                        "Cancelled" -> "5"
                        "Expired" -> "6"
                        "Pending" -> "7"
                        "In Progress" -> "8"
                        else -> "1"
                    }

                    val jobStatusChange = JobStatusChange(notificationText.jobPostId, status, "2", Constant.MOBILE_NO, Constant.API_KEY,
                        Utility.getDeviceId(context), Utility.deviceType,Utility.deviceToken,Constant.APP_USER_ID.toString(), Constant.APP_VERSION_NAME.toString(),Constant.APP_USER_KEY,0.0,0.0)
                    changeJobStatus(jobStatusChange)
                    Log.i("TAG", "bind: " + jobStatusChange)
                }
                builder.show()
            }
        }

        private fun changeJobStatus(jobStatusChange: JobStatusChange) {
            val call = RetrofitClient.api.changeJobStatus(jobStatusChange)
            call.enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                    if (response.isSuccessful) {
                        val updateProfileResponse = response.body()
                        Log.i("TAG", "onResponse: " + response.body())
                        updateProfileResponse?.let {
                            if (it.status == 1) {
                                Toast.makeText(
                                    context,
                                    "Job status changed successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("API", "API call failed with code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    Log.e("API", "API call failed with exception: ${t.message}")
                    Toast.makeText(
                        context,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}
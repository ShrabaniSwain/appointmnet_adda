package com.appointment.tutionservice

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appointment.tutionservice.databinding.RequestAppointmentItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class RequestAppointmentAdapter(val context: Context, val appointment: List<Appointment>) : RecyclerView.Adapter<RequestAppointmentAdapter.CardViewHolder>() {

    private var filteredMarketItems: List<Appointment> = appointment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.request_appointment_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = filteredMarketItems[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return filteredMarketItems.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = RequestAppointmentItemBinding.bind(itemView)

        fun bind(supportDetails : Appointment) {
//            Utility.itemBackGround(itemView)
            Glide.with(context).load(supportDetails.service_image ?: "").into(binding.imageView)
            binding.tvSpecialist.text = supportDetails.service_name
            binding.tvName.text = supportDetails.user_profile_name
            binding.tvEmail.text = supportDetails.user_email
            binding.tvLocation.text = supportDetails.user_address
            binding.tvPhoneNo.text = supportDetails.user_mobile
            binding.tvPrice.text =  supportDetails.customer_budget
            val rating = supportDetails.ratings.toFloat()
            binding.ratingBar.rating = rating
            binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                if (fromUser) {
                    Log.d("RatingBar", "User rated: $rating")
                    val review = Review(supportDetails.customer_id, supportDetails.customer_rating_id, supportDetails.service_provider_id, "", supportDetails.job_post_id, rating.toInt(), 1, Constant.MOBILE_NO,
                        Constant.API_KEY, Utility.getDeviceId(context), Utility.deviceType, Utility.deviceToken, Constant.APP_USER_ID.toString(), Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, 0.0,0.0)
                    appUserReview(review)
                    Log.i("TAG", "bind: " + review)
                }
            }

            val statusNumber = supportDetails.job_post_status
            val status = when (statusNumber) {
                "1" -> "Active"
                "2" -> "Confirmed"
                "3" -> "On the way for service"
                "4" -> "Completed"
                "5" -> "Cancelled"
                "6" -> "Expired"
                "7" -> "Pending"
                "8" -> "In Progress"
                else -> "Active"
            }
            binding.tvConfirmed.text =  status
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(supportDetails.doc)
            val outputFormat = SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault())
            val formattedDate = date?.let { outputFormat.format(it) }
            binding.tvDate.text = formattedDate

            binding.btnCallNow.setOnClickListener {
                val phoneNumber = binding.tvPhoneNo.text.toString()
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$phoneNumber")
                context.startActivity(dialIntent)
            }

            binding.tvWhatsapp.setOnClickListener {
                val phoneNumber = binding.tvPhoneNo.text.toString()
                val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun appUserReview(rating: Review) {
            val call = RetrofitClient.api.appUserReview(rating)
            call.enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == 1) {
                            Toast.makeText(context, "Rating send successfully", Toast.LENGTH_SHORT).show()
                            Log.i("TAG", "Rating send successfully: ${response.body()}")
                        }
                    } else {
                        Log.e("TAG", "Error saving keyword: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    Log.e("TAG", "Failed to save keyword: ${t.message}")
                }
            })
        }

    }

    fun updateListAppointmnet(newList: List<Appointment>) {
        filteredMarketItems = newList
        notifyDataSetChanged()
    }
}
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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appointment.tutionservice.databinding.CustomerEnquiryItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class CustomerEnquiryDialogAdapter(val context: Context, val bids: List<JobPostLog>) : RecyclerView.Adapter<CustomerEnquiryDialogAdapter.CardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.customer_enquiry_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = bids[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return bids.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = CustomerEnquiryItemBinding.bind(itemView)

        fun bind(supportDetails : JobPostLog) {
            Utility.itemBackGround(itemView)
            binding.tvName.text = supportDetails.user_profile_name
            binding.tvName.setOnClickListener {
                Constant.SERVICE_PROVIDER_ID = supportDetails.app_user_id
                Constant.PROVIDER_MOBILE = supportDetails.user_mobile
                Constant.PROVIDER_KEY = supportDetails.app_user_key
                Constant.PROVIDER_ID = supportDetails.app_user_id
                val intent = Intent(context, ProviderProfileDetails::class.java)
                itemView.context.startActivity(intent)
            }
            binding.tvBidsAmount.text = supportDetails.amount
            binding.tvstar.text = supportDetails.remarks
            val rating = supportDetails.ratings.toFloat()
            binding.ratingBar.rating = rating
            Glide.with(context)
                .load(supportDetails.profile_image)
                .into(binding.imageView)

            val dateStr = supportDetails.doc
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(dateStr)
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = date?.let { dateFormatter.format(it) }
            binding.tvMemberDate.text = formattedDate

            binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                if (fromUser) {
                    Log.d("RatingBar", "User rated: $rating")
                    val review = Review(Constant.APP_USER_ID.toString(), supportDetails.customer_rating_id, supportDetails.app_user_id, "", supportDetails.job_post_log_id, rating.toInt(), 1, Constant.MOBILE_NO,
                        Constant.API_KEY, Utility.getDeviceId(context), Utility.deviceType, Utility.deviceToken, Constant.APP_USER_ID.toString(), Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, 0.0,0.0)
                    appUserReview(review)
                    Log.i("TAG", "bind: " + review)
                }
            }
            if (supportDetails.profile_image.isNullOrEmpty()) {
                Glide.with(context).load(supportDetails.profile_image).into(binding.imageView)
            }

            binding.btnCall.setOnClickListener {
                val phoneNumber = supportDetails.user_mobile
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$phoneNumber")
                context.startActivity(dialIntent)
            }

            binding.btnWhatsapp.setOnClickListener {
                val phoneNumber = supportDetails.user_mobile
                val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
                }
            }

            binding.btnMessage.setOnClickListener {
                val appUserId = supportDetails.app_user_id
                if (!appUserId.isNullOrEmpty()) {
                    Constant.CUSTOMER_ID = appUserId
                    val intent = Intent(context, CustomerMessageDeatilsActivity::class.java)
                    itemView.context.startActivity(intent)
                }
            }

            binding.confirmed.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Are you sure you want to confirm this bid amount for this service provider?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        val dealAction = JobDealAction("","2", Constant.JOB_POST_ID, supportDetails.job_post_log_id, supportDetails.app_user_id, Constant.STATUS.toInt(), Constant.MOBILE_NO,
                            Constant.API_KEY, Utility.getDeviceId(context), Utility.deviceType, Utility.deviceToken, Constant.APP_USER_ID.toString(), Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, "", 0.0, 0.0)
                        Toast.makeText(context, "Confirmed", Toast.LENGTH_SHORT).show()
                        jobDealAction(dealAction)
                        Log.i("TAG", "dealAction: $dealAction")
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
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

        private fun jobDealAction(dealAction: JobDealAction) {
            val call = RetrofitClient.api.jobDealAction(dealAction)
            call.enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                    if (response.isSuccessful) {
                        val updateProfileResponse = response.body()
                        Log.i("TAG", "onResponse: $updateProfileResponse")
                        Toast.makeText(context, "Confirmed", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                        Log.i("TAG", "appUserServicesUpdate onResponse error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "appUserServicesUpdate onFailure: ${t.message}")
                }
            })
        }

    }
}
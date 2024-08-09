package com.appointment.tutionservice

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.appointment.tutionservice.Constant.PROVIDER_ID
import com.appointment.tutionservice.Constant.SERVICE_PROVIDER_ID
import com.appointment.tutionservice.databinding.AppointmentListItemBinding

class AppointmentListAdapter(val context: Context, val appointment: List<Business>) : RecyclerView.Adapter<AppointmentListAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.appointment_list_item, parent, false)
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
        private val binding = AppointmentListItemBinding.bind(itemView)

        fun bind(notificationText: Business) {
            Utility.itemBackGround(itemView)
            itemView.setOnClickListener {
                SERVICE_PROVIDER_ID = notificationText.app_user_id
                Constant.PROVIDER_MOBILE = notificationText.service_mobile
                Constant.PROVIDER_KEY = notificationText.service_user_key
                PROVIDER_ID = notificationText.app_user_id
                val intent = Intent(context, ProviderProfileDetails::class.java)
                itemView.context.startActivity(intent)
            }
            binding.tvName.text = notificationText.user_profile_name
            binding.tvSpecialist.text = notificationText.service_name
            binding.tvDetails.text = notificationText.service_description
            val rating = notificationText.rating.average_rating.toFloat()
            binding.ratingBar.rating = rating
            binding.tvLocation.text = notificationText.street_name + "," + notificationText.locality
            Glide.with(context).load(notificationText.profile_image ?: "")
                .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView)

            binding.btnBookAppointment.setOnClickListener {
                SERVICE_PROVIDER_ID = notificationText.app_user_id
                Log.i("TAG", "bind: " + notificationText.app_user_id)
                if (Constant.Question) {
                    val intent = Intent(context, SelectQuestionOne::class.java)
                    itemView.context.startActivity(intent)

                }
                else {
                    val intent = Intent(context, SelectquestionThree::class.java)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}
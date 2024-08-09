package com.appointment.tutionservice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appointment.tutionservice.databinding.EnquiryItemListBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ProviderEnquiryItemAdapter(val context: Context, val enquiry: List<Enquiry>) : RecyclerView.Adapter<ProviderEnquiryItemAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.enquiry_item_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = enquiry[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return enquiry.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = EnquiryItemListBinding.bind(itemView)

        fun bind(notificationText: Enquiry) {
            Utility.itemBackGround(itemView)
            binding.tvName.text = notificationText.jobTitle
            binding.tvNameDetails.text = notificationText.serviceName
            binding.tvDetails.text = notificationText.jobDescription
            Glide.with(context).load(notificationText.service_image ?: "").into(binding.ivImage)
            binding.tvLocation.text = notificationText.city + "," + notificationText.state
            binding.tvEmail.text = notificationText.userEmail
            binding.tvPhoneNo.text = notificationText.userMobile
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(notificationText.doc)
            val outputFormat = SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault())
            val formattedDate = date?.let { outputFormat.format(it) }
            binding.tvDate.text = formattedDate

        }
    }
}
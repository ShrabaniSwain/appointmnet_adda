package com.appointment.tutionservice

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appointment.tutionservice.databinding.EnquiryItemListBinding
import com.bumptech.glide.request.RequestOptions
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
            if (!Constant.is_membership_package_expire){
                binding.tvEmail.text = notificationText.userEmail
                binding.tvPhoneNo.text = notificationText.userMobile
            }
            else{
                binding.tvEmail.text = "*************"
                binding.tvPhoneNo.text = "**********"


                itemView.setOnClickListener {
                    val dialogBuilder = AlertDialog.Builder(context)
                    val inflater =
                        LayoutInflater.from(context).inflate(R.layout.subscription_message_dialog, null)
                    dialogBuilder.setView(inflater)
                    val close = inflater.findViewById<ImageView>(R.id.ivClose)
                    val btn = inflater.findViewById<TextView>(R.id.btnPay)
                    val dialog = dialogBuilder.create()
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    close.setOnClickListener {
                        dialog.dismiss()
                    }

                    btn.setOnClickListener {
                        val intent = Intent(context, UpdatePackageActivity::class.java)
                        itemView.context.startActivity(intent)
                        dialog.dismiss()
                    }

                    dialog.show()

                }
            }

            binding.tvName.text = notificationText.jobTitle
            binding.tvNameDetails.text = notificationText.serviceName
            binding.tvDetails.text = notificationText.jobDescription
            Glide.with(context).load(notificationText.service_image ?: "") .apply(
                RequestOptions.placeholderOf(R.drawable.noimageavailbale)).into(binding.ivImage)
            binding.tvLocation.text = notificationText.city + "," + notificationText.state
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(notificationText.doc)
            val outputFormat = SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault())
            val formattedDate = date?.let { outputFormat.format(it) }
            binding.tvDate.text = formattedDate

        }
    }
}
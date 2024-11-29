package com.appointment.tutionservice

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.Constant.NOTIFICATION_DETAILS
import com.appointment.tutionservice.Constant.NOTIFICATION_IMAGE
import com.appointment.tutionservice.databinding.CustomerNotificationItemListBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CustometrNotificationAdapter(private val notification: List<Notification>, val context: Context) : RecyclerView.Adapter<CustometrNotificationAdapter.CardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.customer_notification_item_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = notification[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return notification.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = CustomerNotificationItemListBinding.bind(itemView)

        fun bind(notificationText: Notification) {
            Utility.itemBackGround(itemView)
            itemView.setOnClickListener {
                NOTIFICATION_DETAILS = notificationText.notificationMessage
                NOTIFICATION_IMAGE = notificationText.pushImageUrl
                Constant.ADDITIONAL_DATA = notificationText.additionalData

                val intent = Intent(context, NotificationDetailsActivity::class.java)
                itemView.context.startActivity(intent)
            }
            binding.tvNotificationdetails.text = notificationText.notificationMessage
            binding.tvPaymentReminder.text = notificationText.notificationTitle
            val dateStr = notificationText.date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(dateStr)
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = date?.let { dateFormatter.format(it) }
            binding.tvPaymentDate.text = formattedDate
        }
    }
}
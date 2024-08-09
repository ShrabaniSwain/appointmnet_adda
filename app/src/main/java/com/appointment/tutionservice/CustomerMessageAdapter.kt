package com.appointment.tutionservice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.appointment.tutionservice.databinding.ChatItemReceivedBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CustomerMessageAdapter(val context: Context, val message: List<JobPostMessageData>) : RecyclerView.Adapter<CustomerMessageAdapter.ReceivedMessageViewHolder>() {

    private val SENT_MESSAGE = 0
    private val RECEIVED_MESSAGE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceivedMessageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_received, parent, false)
        return ReceivedMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceivedMessageViewHolder, position: Int) {
          val data= message[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return message.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < message.size) {
            SENT_MESSAGE
        } else {
            RECEIVED_MESSAGE
        }
    }


    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ChatItemReceivedBinding.bind(itemView)

        fun bind(message: JobPostMessageData) {
            binding.apply {
                if (message.message_type == "sent") {
                    imageView.visibility = View.VISIBLE
                    textMessage.visibility = View.VISIBLE
                    textRecivedMessage.visibility = View.GONE
                    imageViewRecieved.visibility = View.GONE
                    tvText.text = message.message
                    tvName.text = message.fromName
                    Glide.with(context)
                        .load(message.fromNameImage)
                        .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                        .into(binding.imageView)
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val date = inputFormat.parse(message.doc)
                    val outputFormat = SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault())
                    val formattedDate = date?.let { outputFormat.format(it) }
                    tvDateTime.text = formattedDate
                } else {
                    textMessage.visibility = View.GONE
                    imageView.visibility = View.GONE
                    textRecivedMessage.visibility = View.VISIBLE
                    imageViewRecieved.visibility = View.VISIBLE
                    tvRecivedText.text = message.message
                    tvRecivedName.text = message.fromName
                    Glide.with(context)
                        .load(message.fromNameImage)
                        .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                        .into(binding.imageViewRecieved)
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val date = inputFormat.parse(message.doc)
                    val outputFormat = SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault())
                    val formattedDate = date?.let { outputFormat.format(it) }
                    tvRecivedDateTime.text = formattedDate
                }
            }
        }
    }
}
package com.appointment.tutionservice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ChatItemReceivedBinding
import com.appointment.tutionservice.databinding.ChatItemSentBinding

class ProviderChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENT_MESSAGE = 0
    private val RECEIVED_MESSAGE = 1

    private val dummySentMessages = listOf(
        "Hello! This is a sent message.",
        "How are you?",
        "This is another sent message."
    )

    private val dummyReceivedMessages = listOf(
        "Hi! I'm good. How about you?",
        "This is a received message.",
        "I'm doing great! Thanks."
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENT_MESSAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == SENT_MESSAGE) {
            (holder as SentMessageViewHolder).bind(dummySentMessages.getOrNull(position) ?: "")
        } else {
            val receivedPosition = position - dummySentMessages.size // Adjusting position for received messages
            (holder as ReceivedMessageViewHolder).bind(dummyReceivedMessages.getOrNull(receivedPosition) ?: "")
        }
    }

    override fun getItemCount(): Int {
        return dummySentMessages.size + dummyReceivedMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < dummySentMessages.size) {
            SENT_MESSAGE
        } else {
            RECEIVED_MESSAGE
        }
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ChatItemSentBinding.bind(itemView)

        fun bind(message: String) {
            binding.tvText.text = message
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ChatItemReceivedBinding.bind(itemView)

        fun bind(message: String) {
            binding.tvText.text = message
        }
    }
}
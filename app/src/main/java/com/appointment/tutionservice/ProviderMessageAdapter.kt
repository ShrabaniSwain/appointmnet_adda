package com.appointment.tutionservice

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemMessageListBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ProviderMessageAdapter(val context: Context, val message: List<UserMessage>) : RecyclerView.Adapter<ProviderMessageAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = message[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return message.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemMessageListBinding.bind(itemView)

        fun bind(supportDetails : UserMessage) {
            binding.tvNoOfMsg.text = "(" + supportDetails.messageCount + ")"
            binding.message.text = supportDetails.message
            binding.tvName.text = supportDetails.userProfileName
            binding.tvDeatils.text = supportDetails.jobPostCode
            Log.i("TAG", "IdDetails: " + supportDetails.toId + "from" + supportDetails.fromId)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(supportDetails.doc)
            val outputFormat = SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault())
            val formattedDate = date?.let { outputFormat.format(it) }
            binding.tvDateTime.text = formattedDate
            itemView.setOnClickListener {
                Constant.CUSTOMER_ID = supportDetails.fromId
                Constant.TO_ID = supportDetails.toId
                Constant.JOB_POST_ID = supportDetails.jobPostId
                val intent = Intent(context, ProviderMessageDeatilsActivity::class.java)
                context.startActivity(intent)
            }
        }

    }
}
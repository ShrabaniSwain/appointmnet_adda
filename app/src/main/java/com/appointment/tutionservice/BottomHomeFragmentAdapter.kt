package com.appointment.tutionservice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.BottomHomeFragmentItemBinding

class BottomHomeFragmentAdapter(val context: Context) : RecyclerView.Adapter<BottomHomeFragmentAdapter.CardViewHolder>() {

    private val notificationData = listOf(
        "Sudip Das"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_home_fragment_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = notificationData[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return notificationData.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = BottomHomeFragmentItemBinding.bind(itemView)

        fun bind(notificationText: String) {
            Utility.itemBackGround(itemView)
            binding.tvName.text = notificationText
        }
    }
}
package com.appointment.tutionservice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.SupportDetailsItemBinding

class SupportDeatilsAdapter(private val support: List<SupportComplain>) : RecyclerView.Adapter<SupportDeatilsAdapter.CardViewHolder>() {

    private val supportDetails = listOf(
        "Lorem Ipsum Dolor Sit Amet",
        "Lorem Ipsum Dolor Sit Amet",
        "Lorem Ipsum Dolor Sit Amet"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.support_details_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = support[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return support.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = SupportDetailsItemBinding.bind(itemView)

        fun bind(supportDetails : SupportComplain) {
            Utility.itemBackGround(itemView)
            binding.supportDetails.text = supportDetails.subject
            binding.tvSupportFullDetails.text = supportDetails.message
            binding.tvConfirmed.text = supportDetails.status
            binding.tvIdNo.text = supportDetails.supportComplainId

        }

    }
}
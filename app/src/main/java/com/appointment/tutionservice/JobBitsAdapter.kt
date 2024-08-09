package com.appointment.tutionservice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JobBitsAdapter(private val jobBits: List<JobBit>) :
    RecyclerView.Adapter<JobBitsAdapter.JobBitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobBitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bids_item, parent, false)
        return JobBitViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobBitViewHolder, position: Int) {
        val jobBit = jobBits[position]
        holder.bind(jobBit)
    }

    override fun getItemCount() = jobBits.size

    class JobBitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bidData: TextView = itemView.findViewById(R.id.bidData)

        fun bind(jobBit: JobBit) {
            bidData.text =  "Your last bid amount: ${jobBit.amount}"
        }
    }
}
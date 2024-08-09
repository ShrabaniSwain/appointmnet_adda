package com.appointment.tutionservice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.SelectBoardBinding

class SelectMediumAdapter() : RecyclerView.Adapter<SelectMediumAdapter.CardViewHolder>() {

    private val selectMedium = listOf(
        "English",
        "Hindi",
        "Benlogi"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_board, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = selectMedium[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return selectMedium.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = SelectBoardBinding.bind(itemView)
        private var isItemSelected = false

        fun bind(notificationText: String) {
            binding.selectBoardName.text = notificationText


            binding.cardView.setOnClickListener {
                isItemSelected = !isItemSelected
                if (isItemSelected) {
                    binding.selectBoardName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                    binding.cardView.setBackgroundResource(R.drawable.green_background_color)
                } else {
                    binding.selectBoardName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.color_primary))
                    binding.cardView.setBackgroundResource(R.drawable.border_color)
                }
            }

        }

    }
}
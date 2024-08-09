package com.appointment.tutionservice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.SelectBoardBinding

class ChoosefeeAdapter() : RecyclerView.Adapter<ChoosefeeAdapter.CardViewHolder>() {
    private var selectedPosition: Int? = null

    private val chooseFee = listOf(
        "₹500",
        "₹1000",
        "₹1500"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_board, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = chooseFee[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return chooseFee.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var isItemSelected = false
        private val binding = SelectBoardBinding.bind(itemView)

        fun bind(notificationText: String) {
            binding.selectBoardName.text = notificationText
            setSelected(adapterPosition == selectedPosition)

            setSelected(isSelected = false)

            binding.cardView.setOnClickListener {
                if (adapterPosition == selectedPosition) {
                    setSelected(isSelected = false)
                    selectedPosition = null
                    Constant.BUDGET = ""
                } else {
                    selectedPosition?.let { prevSelectedPosition ->
                        notifyItemChanged(prevSelectedPosition)
                    }
                    setSelected(isSelected = true)
                    selectedPosition = adapterPosition

                    Constant.BUDGET = binding.selectBoardName.text.toString()
                }
            }

        }

        private fun setSelected(isSelected: Boolean) {
            if (isSelected) {
                binding.selectBoardName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.cardView.setBackgroundResource(R.drawable.green_background_color)
            } else {
                binding.selectBoardName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.color_primary))
                binding.cardView.setBackgroundResource(R.drawable.border_color)
            }
        }

    }
}
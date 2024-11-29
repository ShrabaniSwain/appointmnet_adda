package com.appointment.tutionservice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemUpdatePackageBinding
import com.appointment.tutionservice.databinding.ItemWalletHistoryBinding

class WalletHistoryAdapter(val context: Context, val wallet: List<Wallet>) : RecyclerView.Adapter<WalletHistoryAdapter.CardViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wallet_history, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = wallet[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return wallet.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemWalletHistoryBinding.bind(itemView)

        fun bind(wallet: Wallet) {
//            Utility.itemBackGround(itemView)
            binding.tvTotalAmount.text = "₹"+ wallet.amount
            binding.tvRemaining.text = wallet.purpose
            binding.tvDate.text = wallet.doc
            if (wallet.type == "Credit") {
                binding.tvCredit.visibility = View.VISIBLE
            }
            else{
                binding.tvDebit.visibility = View.VISIBLE
            }
        }
    }


}

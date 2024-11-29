package com.appointment.tutionservice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemReferrallListBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ReferralListAdapter(val context: Context, val referral: List<ReferralIdData>) : RecyclerView.Adapter<ReferralListAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_referrall_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = referral[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return referral.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemReferrallListBinding.bind(itemView)

        fun bind(referral: ReferralIdData) {
            Utility.itemBackGround(itemView)
            binding.tvName.text = referral.user_profile_name
            binding.tvEmail.text = referral.user_email
            binding.tvPhone.text = referral.user_mobile
            binding.tvSubscriptionName.text = referral.subscription_name
            binding.tvActiveDate.text = referral.active_date
            binding.tvExpiryDate.text = referral.expiry_date

            Glide.with(context).load(referral.profile_img_path ?: "")
                .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView)
        }
    }
}
package com.appointment.tutionservice

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemUpdatePackageBinding

class UpdatePackageAdapter(val context: Context, val packageData: List<MembershipPackage>,val onClick :(data :MembershipPackage)->Unit) : RecyclerView.Adapter<UpdatePackageAdapter.CardViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_update_package, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = packageData[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return packageData.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUpdatePackageBinding.bind(itemView)

        fun bind(notificationText: MembershipPackage) {
            Utility.itemBackGround(itemView)
            binding.title.text = notificationText.packageName

            val htmlText = notificationText.packageDescription
            val cleanText = Html.fromHtml(htmlText).toString()
            binding.featuresDes.text = cleanText
            binding.validateDays.text = notificationText.validityDays + " Days"
            binding.priceAmount.text = "Rs. "+ notificationText.packagePrice
            binding.btnCallNow.setOnClickListener {

                onClick.invoke(notificationText)

            }
        }
    }


}



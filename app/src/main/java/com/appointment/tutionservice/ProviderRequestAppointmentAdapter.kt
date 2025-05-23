package com.appointment.tutionservice

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appointment.tutionservice.databinding.RequestAppointmentItemBinding
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.Locale

class ProviderRequestAppointmentAdapter(val context: Context, val appointment: List<Appointment>) : RecyclerView.Adapter<ProviderRequestAppointmentAdapter.CardViewHolder>() {

    private var filteredMarketItems: List<Appointment> = appointment


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.request_appointment_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = filteredMarketItems[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return filteredMarketItems.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = RequestAppointmentItemBinding.bind(itemView)

        fun bind(supportDetails : Appointment) {
            Utility.itemBackGround(itemView)
            Glide.with(context).load(supportDetails.service_image ?: "")
                .apply(
                RequestOptions.placeholderOf(R.drawable.noimageavailbale))
                .into(binding.imageView)
            binding.tvSpecialist.text = supportDetails.service_name
            binding.tvName.text = supportDetails.user_profile_name
            binding.tvEmail.text = supportDetails.user_email
            binding.tvLocation.text = supportDetails.user_address
            binding.tvPhoneNo.text = supportDetails.user_mobile
            binding.tvPrice.text = supportDetails.customer_budget
            binding.ratingBar.visibility = View.GONE
            if (!supportDetails.package_expiry){
                binding.btnSubscribe.visibility = View.GONE
                binding.tvEmail.text = "************"
                binding.tvPhoneNo.text = "**********"
                binding.btnCallNow.isClickable = false
                binding.tvWhatsapp.isClickable = false
                binding.tvWhatsapp.isEnabled = false
                binding.btnCallNow.isEnabled = false
                itemView.setOnClickListener {
                    val dialogBuilder = AlertDialog.Builder(context)
                    val inflater =
                        LayoutInflater.from(context).inflate(R.layout.subscription_message_dialog, null)
                    dialogBuilder.setView(inflater)
                    val close = inflater.findViewById<ImageView>(R.id.ivClose)
                    val btn = inflater.findViewById<TextView>(R.id.btnPay)
                    val dialog = dialogBuilder.create()
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    close.setOnClickListener {
                        dialog.dismiss()
                    }

                    btn.setOnClickListener {
                        val intent = Intent(context, UpdatePackageActivity::class.java)
                        itemView.context.startActivity(intent)
                        dialog.dismiss()
                    }

                    dialog.show()

                }
            }
            else{
                binding.btnSubscribe.visibility = View.GONE
                binding.btnCallNow.isClickable = true
                binding.tvWhatsapp.isClickable = true
                binding.tvWhatsapp.isEnabled = true
                binding.btnCallNow.isEnabled = true
            }
            binding.btnSubscribe.setOnClickListener {
                val intent = Intent(context, UpdatePackageActivity::class.java)
                itemView.context.startActivity(intent)
            }
            val statusNumber = supportDetails.job_post_status
            val status = when (statusNumber) {
                "1" -> "Active"
                "2" -> "Confirmed"
                "3" -> "On the way for service"
                "4" -> "Completed"
                "5" -> "Cancelled"
                "6" -> "Expired"
                "7" -> "Pending"
                "8" -> "In Progress"
                else -> "Active"
            }
            binding.tvConfirmed.text =  status
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(supportDetails.doc)
            val outputFormat = SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault())
            val formattedDate = date?.let { outputFormat.format(it) }
            binding.tvDate.text = formattedDate

            binding.btnCallNow.setOnClickListener {
                val phoneNumber = binding.tvPhoneNo.text.toString()
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$phoneNumber")
                context.startActivity(dialIntent)
            }

            binding.tvWhatsapp.setOnClickListener {
                val phoneNumber = binding.tvPhoneNo.text.toString()
                val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    fun updateListAppointmnet(newList: List<Appointment>) {
        filteredMarketItems = newList
        notifyDataSetChanged()
    }
}
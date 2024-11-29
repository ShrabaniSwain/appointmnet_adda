package com.appointment.tutionservice

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.CrmItemListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrmProviderAdapter(
    private val crmList: List<CrmList>,
    private val listener: SelectAllListener, private val context: Context
    ) : RecyclerView.Adapter<CrmProviderAdapter.CardViewHolder>() {

    private var isSelectAllChecked = false
    private val selectedItems = mutableSetOf<CrmList>()

    fun setSelectAll(isChecked: Boolean) {
        isSelectAllChecked = isChecked
        if (isChecked) {
            selectedItems.addAll(crmList)
        } else {
            selectedItems.clear()
        }
        notifyDataSetChanged()
        listener.onSelectAllChanged(selectedItems.size == crmList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.crm_item_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = crmList[position]
        holder.bind(item)

        holder.binding.selectAll.isChecked = selectedItems.contains(item)

        holder.binding.selectAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(item)
            } else {
                selectedItems.remove(item)
            }
            listener.onSelectAllChanged(selectedItems.isNotEmpty())
        }

        holder.itemView.setOnClickListener {
            holder.binding.selectAll.isChecked = !holder.binding.selectAll.isChecked
        }
    }

    override fun getItemCount(): Int = crmList.size

    fun getSelectedPhoneNumbers(): List<String> {
        return selectedItems.map { it.mobile }
    }

    fun getSelectedUserId(): List<String> {
        return selectedItems.map { it.id }
    }
    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = CrmItemListBinding.bind(itemView)

        fun bind(crmList: CrmList) {
            Utility.itemBackGround(itemView)
            binding.tvName.text = crmList.name
            binding.tvLocation.text = crmList.address
            binding.tvEmail.text = crmList.email
            binding.tvPhoneNo.text = crmList.mobile

            binding.btnCall.setOnClickListener {
                val phoneNumber = crmList.mobile
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$phoneNumber")
                context.startActivity(dialIntent)
            }

            binding.btnWhatsapp.setOnClickListener {
                val phoneNumber = crmList.mobile
                val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
                }
            }

            binding.ivDelete.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Delete CRM Profile")
                builder.setMessage("Are you sure you want to delete the CRM profile?")

                builder.setPositiveButton("Yes") { _, _ ->
                    val delete = CrmProfileDelete(crmList.id,Constant.MOBILE_NO,Constant.API_KEY,Utility.getDeviceId(context),Utility.deviceType, Utility.deviceToken, Constant.APP_USER_ID.toString(), Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, 0.0, 0.0)
                    appCrmDocumentDelete(delete)
                    Log.i("TAG", "bind: " + delete)
                    Toast.makeText(context, "Deleting CRM profile...", Toast.LENGTH_SHORT).show()
                }

                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()

            }
        }

        private fun appCrmDocumentDelete(delete: CrmProfileDelete) {
            val call = RetrofitClient.api.appCrmDocumentDelete(delete)
            call.enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                    if (response.isSuccessful) {
                        val intent = Intent(context, ProviderMainActivity::class.java)
                        itemView.context.startActivity(intent)
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                            Log.i("TAG", "Rating send successfully: ${response.body()}")
                    } else {
                        Log.e("TAG", "Error saving keyword: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    Log.e("TAG", "Failed to save keyword: ${t.message}")
                }
            })
        }

    }

    interface SelectAllListener {
        fun onSelectAllChanged(isChecked: Boolean)
    }
}


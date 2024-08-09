package com.appointment.tutionservice

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.FragmentCrmBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrmFragment : Fragment(), CrmProviderAdapter.SelectAllListener {

    private lateinit var binding: FragmentCrmBinding
    private lateinit var crmList: List<CrmList>
    private var isSelectAllChecked = false
    private lateinit var crmAdapter: CrmProviderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCrmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCrmDocumentListing()
        binding.selectAll.setOnCheckedChangeListener { _, isChecked ->
            isSelectAllChecked = isChecked
            crmAdapter.setSelectAll(isChecked)
            binding.btnNotice.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        binding.btnNotice.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.send_notice_dialog_box, null)
            dialogBuilder.setView(inflater)
            val close = inflater.findViewById<ImageView>(R.id.ivClose)
            val pushCard = inflater.findViewById<ConstraintLayout>(R.id.pushCard)
            val smsCard = inflater.findViewById<ConstraintLayout>(R.id.smsCard)
            val dialog = dialogBuilder.create()

            pushCard.setOnClickListener {
                showMessageDialog()
            }

            smsCard.setOnClickListener {
                showMessageDialog()
//                sendSmsToSelectedItems()
            }

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            close.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.btnAddNow.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.add_new_dialog_box, null)
            dialogBuilder.setView(inflater)
            val close = inflater.findViewById<ImageView>(R.id.ivClose)
            val whatsappCard = inflater.findViewById<ConstraintLayout>(R.id.whatsappCard)
            val smsCard = inflater.findViewById<ConstraintLayout>(R.id.smsCard)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            close.setOnClickListener {
                dialog.dismiss()
            }

            whatsappCard.setOnClickListener {
                val intent = Intent(requireContext(), AddCustomerActivity::class.java)
                startActivity(intent)
            }

            smsCard.setOnClickListener {
                val intent = Intent(requireContext(), ImportRecordActivity::class.java)
                startActivity(intent)
            }

            dialog.show()
        }
    }

    private fun getCrmDocumentListing() {
        showProgressBar()
        val call = RetrofitClient.api.getCrmDocumentListing(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(requireContext()),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<CrmListResponse> {
            override fun onResponse(call: Call<CrmListResponse>, response: Response<CrmListResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        hideProgressBar()
                        crmList = response.body()?.data ?: emptyList()

                        crmAdapter = CrmProviderAdapter(crmList, this@CrmFragment, requireContext())
                        binding.rvCrm.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        binding.rvCrm.adapter = crmAdapter

                        Log.i("TAG", "bannerImage: ${response.body()}")
                    }
                } else {
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "bannerImage: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<CrmListResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

            }
        })
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onSelectAllChanged(isChecked: Boolean) {
        binding.btnNotice.visibility = if (isChecked) View.VISIBLE else View.GONE
    }

    private fun showMessageDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.message_dialog_box, null)
        dialogBuilder.setView(inflater)
        val messageTextView = inflater.findViewById<EditText>(R.id.etRequirements)
        val okButton = inflater.findViewById<TextView>(R.id.btnSend)
        val ivClose = inflater.findViewById<ImageView>(R.id.ivClose)

        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        okButton.setOnClickListener {
            val selectedPhoneNumbers = crmAdapter.getSelectedPhoneNumbers()
            if (selectedPhoneNumbers.isNotEmpty()) {
                val sendMessage = SmsRequest(messageTextView.text.toString(),Constant.Provider_Name,selectedPhoneNumbers,Constant.API_KEY,Utility.getDeviceId(requireContext()), Utility.deviceType, Utility.deviceToken,
                    Constant.APP_USER_ID.toString(), Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, 0.0, 0.0)
                sendSmsToUser(sendMessage)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "No items selected", Toast.LENGTH_SHORT).show()
            }


        }

    }

    private fun sendSmsToUser(smsRequest: SmsRequest) {
        val call = RetrofitClient.api.sendSmsToUser(smsRequest)
        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: $updateProfileResponse")
                    Toast.makeText(context, "Message send successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "appUserServicesUpdate onResponse error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "appUserServicesUpdate onFailure: ${t.message}")
            }
        })
    }

    private fun sendSmsToSelectedItems() {
        val selectedPhoneNumbers = crmAdapter.getSelectedPhoneNumbers()
        if (selectedPhoneNumbers.isNotEmpty()) {
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:" + selectedPhoneNumbers.joinToString(";"))
                putExtra("sms_body", "")
            }
            startActivity(smsIntent)
        } else {
            Toast.makeText(requireContext(), "No items selected", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.appointment.tutionservice

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.appointment.tutionservice.databinding.ActivityCrmProviderBinding

class CrmProviderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrmProviderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrmProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSendNotice.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this).inflate(R.layout.send_notice_dialog_box, null)
            dialogBuilder.setView(inflater)
            val close = inflater.findViewById<ImageView>(R.id.ivClose)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            close.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.btnAddNow.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this).inflate(R.layout.add_new_dialog_box, null)
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
                val intent = Intent(applicationContext, AddCustomerActivity::class.java)
                startActivity(intent)
            }

            smsCard.setOnClickListener {
                val intent = Intent(applicationContext, ImportRecordActivity::class.java)
                startActivity(intent)
            }

            dialog.show()
        }

    }

}
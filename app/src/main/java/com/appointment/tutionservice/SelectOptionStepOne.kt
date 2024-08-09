package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.appointment.tutionservice.databinding.ActivitySelectOptionStepOneBinding

class SelectOptionStepOne : AppCompatActivity() {
    private lateinit var binding: ActivitySelectOptionStepOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectOptionStepOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(applicationContext, SelectQuestionOne::class.java)
            startActivity(intent)
        }
        val selectBoardAdapter = SelectBoardAdapter()
        binding.rvBoard.adapter = selectBoardAdapter
        binding.rvBoard.layoutManager = GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)

    }


}
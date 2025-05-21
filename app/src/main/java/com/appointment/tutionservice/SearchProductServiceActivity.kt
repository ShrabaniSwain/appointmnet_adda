package com.appointment.tutionservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivitySearchProductServiceActivtyBinding

class SearchProductServiceActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchProductServiceActivtyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchProductServiceActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dummyProducts = listOf(
            mapOf("product_name" to "Laptop", "product_price" to "55000"),
            mapOf("product_name" to "Smartphone", "product_price" to "20000"),
            mapOf("product_name" to "Headphones", "product_price" to "3000"),
            mapOf("product_name" to "Smartwatch", "product_price" to "5000"),
            mapOf("product_name" to "Bluetooth Speaker", "product_price" to "2500")
        )

        val adapter = SearchProductListAdapter(dummyProducts, this)
        binding.rvService.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.rvService.adapter = adapter

    }
}
package com.appointment.tutionservice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemMyProductBinding

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val dummyData = listOf(
        Pair("Product 1", R.drawable.business),
        Pair("Product 2", R.drawable.business),
        Pair("Product 3", R.drawable.business),
        Pair("Product 4", R.drawable.business)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemMyProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val (nameOfTheProduct, imageProduct) = dummyData[position]
        holder.bind(nameOfTheProduct, imageProduct)
    }

    override fun getItemCount(): Int = dummyData.size

    inner class ProductViewHolder(private val binding: ItemMyProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(nameOfTheProduct: String, imageProduct: Int) {
            binding.nameOfTheProduct.text = nameOfTheProduct
            binding.imageProduct.setImageResource(imageProduct)
        }
    }
}

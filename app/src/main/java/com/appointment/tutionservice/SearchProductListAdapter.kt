package com.appointment.tutionservice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemProductListBinding

class SearchProductListAdapter(
    private var productList: List<Map<String, String>>,
    private val context: Context
) : RecyclerView.Adapter<SearchProductListAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemProductListBinding.bind(itemView)

        fun bind(product: Map<String, String>) {
            binding.tvProductName.text = product["product_name"]
        }
    }

}

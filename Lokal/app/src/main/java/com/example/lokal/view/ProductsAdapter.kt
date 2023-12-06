package com.example.lokal.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lokal.R
import com.example.lokal.databinding.ProductListItemBinding
import com.example.lokal.models.ProductInfo


class ProductsAdapter(
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {
    private var productsList: List<ProductInfo> = ArrayList()

    interface OnItemClickListener {
        fun onItemClick(product: ProductInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductListItemBinding.inflate(inflater, parent, false)
        return ProductsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        productsList[position].let { product ->
            with(holder) {
                bind(product)
                itemView.setOnClickListener {
                    onItemClickListener.onItemClick(product)
                }
            }
        }
    }

    inner class ProductsViewHolder(private val binding: ProductListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductInfo) {
            with(binding) {
                productTitle.text = item.title
                productPrice.text = "Buy at $${item.price}"

                val url: String = item.thumbnail
                Glide
                    .with(productThumbnail.context)
                    .load(url)
                    .into(productThumbnail)
            }
        }
    }

    fun submitList(newProductList: List<ProductInfo>) {
        productsList = newProductList
        notifyDataSetChanged()
    }

}
package com.example.lokal.models

import java.io.Serializable

data class Products(
    val products: List<ProductInfo>
)


data class ProductInfo(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String,
    val category: String,
    val thumbnail: String,
    val images: List<String>
) : Serializable

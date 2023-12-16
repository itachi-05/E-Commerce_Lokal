package com.example.lokal.repository

import com.example.lokal.models.Products
import com.example.lokal.utils.ResponseResult

interface ProductsRepository {
    suspend fun getProductsInfo(): ResponseResult<Products>
}
package com.example.lokal.network

import com.example.lokal.models.Products
import retrofit2.Response
import retrofit2.http.GET

interface ApiServices {
    @GET("products")
    suspend fun getProductsInfo() : Response<Products>
}
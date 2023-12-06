package com.example.lokal.repository

import com.example.lokal.models.Products
import com.example.lokal.network.ApiServices
import com.example.lokal.utils.ResponseHandler
import com.example.lokal.utils.ResponseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductsRepository(
    private val apiServices: ApiServices,
    private val responseHandler: ResponseHandler
) {
    suspend fun getProductsInfo(): ResponseResult<Products> =
        withContext(Dispatchers.IO) {
            try {
                responseHandler.callAPI {
                    withContext(Dispatchers.IO) {
                        apiServices.getProductsInfo()
                    }
                }
            } catch (e: Exception) {
                ResponseResult.Error("something went wrong")
            }
        }
}
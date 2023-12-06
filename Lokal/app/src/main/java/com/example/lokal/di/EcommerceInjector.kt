package com.example.lokal.di


import com.example.lokal.network.ApiServices
import com.example.lokal.repository.ProductsRepository
import com.example.lokal.utils.ResponseHandler
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class EcommerceInjector {

    private val baseUrl = "https://dummyjson.com/"
    private lateinit var apiServices: ApiServices

    @Synchronized
    fun getApiServices(): ApiServices {
        if (!::apiServices.isInitialized) {
            apiServices = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiServices::class.java)
        }
        return apiServices
    }

    //ResponseHandler
    private lateinit var responseHandler: ResponseHandler
    private fun getResponseHandler(): ResponseHandler {
        if (!::responseHandler.isInitialized) {
            responseHandler = ResponseHandler()
        }
        return ResponseHandler()
    }

    //use-cases
    private lateinit var productsRepository: ProductsRepository
    fun getProductsListUseCase(): ProductsRepository {
        if (!::productsRepository.isInitialized) {
            productsRepository = ProductsRepository(getApiServices(), getResponseHandler())
        }
        return productsRepository
    }
}
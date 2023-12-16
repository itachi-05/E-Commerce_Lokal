package com.example.lokal.di

import com.example.lokal.network.ApiServices
import com.example.lokal.repository.ProductsRepository
import com.example.lokal.repository.ProductsRepositoryImpl
import com.example.lokal.utils.Constants
import com.example.lokal.utils.ResponseHandler
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface EcommerceInjector {
    val baseUrl: String
    fun getApiServices(): ApiServices
    fun getResponseHandler(): ResponseHandler
    fun getProductsListUseCase(): ProductsRepository
}


class EcommerceInjectorImpl : EcommerceInjector {
    override val baseUrl: String by lazy { Constants.baseUrl }

    private lateinit var apiServices: ApiServices

    @Synchronized
    override fun getApiServices(): ApiServices {
        if (!::apiServices.isInitialized) {
            apiServices = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiServices::class.java)
        }
        return apiServices
    }

    private lateinit var responseHandler: ResponseHandler
    override fun getResponseHandler(): ResponseHandler {
        if (!::responseHandler.isInitialized) {
            responseHandler = ResponseHandler()
        }
        return ResponseHandler()
    }

    private lateinit var productsRepository: ProductsRepository
    override fun getProductsListUseCase(): ProductsRepository {
        if (!::productsRepository.isInitialized) {
            productsRepository = ProductsRepositoryImpl(getApiServices(), getResponseHandler())
        }
        return productsRepository
    }
}
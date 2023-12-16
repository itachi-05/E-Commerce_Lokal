package com.example.lokal

import android.app.Application
import com.example.lokal.di.EcommerceInjector
import com.example.lokal.di.EcommerceInjectorImpl

class Ecommerce : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        injection = EcommerceInjectorImpl()
    }

    companion object {
        private lateinit var instance: Ecommerce
        private lateinit var injection: EcommerceInjector

        @JvmStatic
        fun getInstance(): Ecommerce {
            return instance
        }

        @JvmStatic
        fun getInjector(): EcommerceInjector {
            return injection
        }
    }
}
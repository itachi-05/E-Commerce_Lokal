package com.example.lokal

import android.app.Application
import com.example.lokal.di.EcommerceInjector

class Ecommerce : Application() {
    private lateinit var injection: EcommerceInjector
    override fun onCreate() {
        super.onCreate()
        instance = this
        injection = EcommerceInjector()
    }

    fun getInjector(): EcommerceInjector {
        return injection
    }

    companion object {
        private lateinit var instance: Ecommerce

        @JvmStatic
        fun getInstance(): Ecommerce {
            return instance
        }
    }
}
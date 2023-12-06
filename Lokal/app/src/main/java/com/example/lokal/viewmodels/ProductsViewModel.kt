package com.example.lokal.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lokal.models.Products
import com.example.lokal.repository.ProductsRepository
import com.example.lokal.utils.ResponseResult
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val repository: ProductsRepository
) : ViewModel() {
    private val _productsLiveData = MutableLiveData<ResponseResult<Products>>()
    val productsLiveData get() = _productsLiveData

    fun getAllProducts() {
        _productsLiveData.postValue(ResponseResult.Loading())
        viewModelScope.launch {
            _productsLiveData.postValue(repository.getProductsInfo())
        }
    }

    companion object {
        class Factory(
            private val repository: ProductsRepository
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductsViewModel(repository) as T
            }
        }
    }
}
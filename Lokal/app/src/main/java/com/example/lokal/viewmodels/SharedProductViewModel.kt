package com.example.lokal.viewmodels

import androidx.lifecycle.ViewModel
import com.example.lokal.models.ProductInfo
import com.example.lokal.models.Products

class SharedProductViewModel : ViewModel() {
    var productInfo: ProductInfo? = null
    var allProductsList: List<ProductInfo>? = null
    var filteredItemsList: MutableSet<String>? = null
    var finalItemsSelectedHasMap: HashMap<Int, HashSet<String>>? = null
}
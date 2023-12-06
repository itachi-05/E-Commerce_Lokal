package com.example.lokal.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lokal.Ecommerce
import com.example.lokal.R
import com.example.lokal.databinding.FragmentHomeBinding
import com.example.lokal.models.ProductInfo
import com.example.lokal.models.Products
import com.example.lokal.utils.FilterBy
import com.example.lokal.utils.ResponseResult
import com.example.lokal.utils.ResponseSortManager
import com.example.lokal.viewmodels.ProductsViewModel
import com.example.lokal.viewmodels.SharedProductViewModel


class HomeFragment : Fragment(), SortBottomSheet.SortBSListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var productsViewModel: ProductsViewModel
    private lateinit var sharedProductViewModel: SharedProductViewModel
    private lateinit var localProductsList: List<ProductInfo>
    private lateinit var productsAdapter: ProductsAdapter
    private val handler by lazy { Handler(Looper.getMainLooper()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        productsViewModel = getProductsViewModel()
        sharedProductViewModel =
            ViewModelProvider(requireActivity())[SharedProductViewModel::class.java]
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        bindObservers()
    }

    private fun getProductsViewModel(): ProductsViewModel {
        return ViewModelProvider(
            this, ProductsViewModel.Companion.Factory(
                Ecommerce.getInstance().getInjector().getProductsListUseCase()
            )
        )[ProductsViewModel::class.java]
    }


    private fun bindViews() {
        productsViewModel.getAllProducts()
        setUpSortByBottomSheet()
        setUpFilterByFragment()
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
//                updatingProductListPB.visibility = View.VISIBLE
                if (::productsAdapter.isInitialized && ::localProductsList.isInitialized) {
                    productsAdapter.submitList(localProductsList)
                }
                handler.postDelayed({
//                    updatingProductListPB.visibility = View.GONE
                    swipeRefreshLayout.isRefreshing = false
                }, 1500)
            }
        }
    }


    private fun bindObservers() {
        with(binding) {
            productsViewModel.productsLiveData.observe(viewLifecycleOwner) {
                updatingProductListPB.visibility = View.VISIBLE
                when (it) {
                    is ResponseResult.Loading -> {
                        showToast("Loading products")
                    }

                    is ResponseResult.Error -> {
                        showToast(it.message.toString())
                    }

                    is ResponseResult.Success -> {
                        setProductsAdapter(it.data)
                    }
                }
                handler.postDelayed({
                    updatingProductListPB.visibility = View.GONE
                }, 1500)
            }
        }
    }


    override fun sortOption(optionKeyMap: HashMap<String, String>) {
//        showToast("Changed option is : $optionKeyMap")
        val productsCopyList = localProductsList
        with(binding) {
            updatingProductListPB.visibility = View.VISIBLE
            // Sort By
            val sortVia = when (optionKeyMap["optionType"]) {
                "Price - Low to High" -> ResponseSortManager.PRICE_LOW_TO_HIGH
                "Price - High to Low" -> ResponseSortManager.PRICE_HIGH_TO_LOW
                "Discount" -> ResponseSortManager.DISCOUNT
                else -> ResponseSortManager.RATING
            }
            val sortedArray = sortArray(productsCopyList, sortVia)
            productsAdapter.submitList(sortedArray)
            handler.postDelayed({
                updatingProductListPB.visibility = View.GONE
            }, 1200)
        }
    }


    private fun setUpSortByBottomSheet() {
        val sortingOptions =
            listOf("Price - Low to High", "Price - High to Low", "Rating", "Discount")
        val sortByBottomSheet = SortBottomSheet.newInstance(sortingOptions)
        sortByBottomSheet.setBottomSheetListener(this)
        binding.sortByFeature.setOnClickListener {
            sortByBottomSheet.show(
                requireActivity().supportFragmentManager,
                "sortBySelected"
            )
        }
    }


    private fun <T> sortArray(array: List<T>, optionType: ResponseSortManager): List<T> {
        return when (optionType) {
            ResponseSortManager.PRICE_LOW_TO_HIGH -> array.sortedBy { (it as ProductInfo).price }
            ResponseSortManager.PRICE_HIGH_TO_LOW -> array.sortedByDescending { (it as ProductInfo).price }
            ResponseSortManager.RATING -> array.sortedByDescending { (it as ProductInfo).rating }
            ResponseSortManager.DISCOUNT -> array.sortedBy { (it as ProductInfo).discountPercentage }
        }
    }


    private fun setUpFilterByFragment() {
//        val filteringOptions = listOf("Brand", "Rating", "Stock", "Discount percentage", "Category")
        binding.filterByFeature.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_FilterOptionFragment)
        }
    }


    private fun setProductsAdapter(products: Products?) {
        val productsList = products?.products
        if (productsList.isNullOrEmpty()) {
            showToast("No products found")
        } else {
            localProductsList = productsList
            sharedProductViewModel.allProductsList = localProductsList
            productsAdapter = ProductsAdapter(
                object : ProductsAdapter.OnItemClickListener {
                    override fun onItemClick(product: ProductInfo) {
                        sharedProductViewModel.productInfo = product
                        findNavController().navigate(
                            R.id.action_homeFragment_to_productFragment
                        )
                    }
                })
            if (!sharedProductViewModel.finalItemsSelectedHasMap.isNullOrEmpty()) {
                val filterSet = sharedProductViewModel.finalItemsSelectedHasMap.orEmpty()
                val productList = getFilteredList(filterSet)
                /**
                 * TODO
                 * Filtering + sorting : pending
                 * Multiple filter select : pending
                 * Single filter select : done
                 * sort by : 3 options : done
                 **/
                productsAdapter.submitList(productList)
            } else {
                productsAdapter.submitList(localProductsList)
            }
            binding.productsRv.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.productsRv.adapter = productsAdapter
        }
    }

    private fun getFilteredList(filterSet: Map<Int, MutableSet<String>>): ArrayList<ProductInfo> {
        val productList: HashSet<ProductInfo> = HashSet()
        localProductsList.forEach { productInfo ->
            for ((filterType, filterValues) in filterSet) {
                when (FilterBy.values()
                    .firstOrNull { it.filterType == filterType }) {
                    FilterBy.Brand -> if (filterValues.contains(productInfo.brand)) {
                        productList.add(productInfo)
                    }

                    FilterBy.Rating -> {
                        manageRatingRange(productInfo, filterValues, productList)
                    }

                    FilterBy.Stock -> {
                        manageStockRange(productInfo, filterValues, productList)
                    }

                    FilterBy.Category -> if (filterValues.contains(productInfo.category)) {
                        productList.add(productInfo)
                    }

                    else -> continue
                }
            }
        }
        return ArrayList(productList)
    }

    private fun manageRatingRange(
        productInfo: ProductInfo,
        filterValues: MutableSet<String>,
        productList: HashSet<ProductInfo>
    ) {
        filterValues.forEach {
            val rating = it.split("_")
            try {
                val rangeStart = rating[0].toDouble()
                val rangeEnd = rating[1].toDouble()
                if (productInfo.rating in rangeStart..rangeEnd) {
                    productList.add(productInfo)
                }
            } catch (e: Exception) {
                showToast("Something went wrong in Ratings\nTry again later")
                return@forEach
            }
            return@forEach
        }
    }

    private fun manageStockRange(
        productInfo: ProductInfo,
        filterValues: MutableSet<String>,
        productList: HashSet<ProductInfo>
    ) {
        filterValues.forEach {
            val stockAvailable = it.split("_")
            try {
                val rangeStart = stockAvailable[0].toInt()
                val rangeEnd = stockAvailable[1].toInt()
                if (productInfo.stock in rangeStart..rangeEnd) {
                    productList.add(productInfo)
                }
            } catch (e: Exception) {
                showToast("Something went wrong in Stocks\nTry again later")
                return@forEach
            }
            return@forEach
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
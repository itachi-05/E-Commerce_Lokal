package com.example.lokal.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lokal.R
import com.example.lokal.databinding.FragmentFilterOptionBinding
import com.example.lokal.models.ProductInfo
import com.example.lokal.utils.Constants
import com.example.lokal.utils.FilterBy
import com.example.lokal.viewmodels.SharedProductViewModel

class FilterOptionFragment : Fragment() {
    private var _binding: FragmentFilterOptionBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedProductViewModel: SharedProductViewModel
    private var allProductsInfo: List<ProductInfo>? = null
    private var finalItemsSelectedSet: MutableSet<String> = mutableSetOf()
    private var maximumStockAvailable: Int = -1
    private var minimumStockAvailable: Int = -1
    private var finalItemsSelectedHasMap: HashMap<Int, HashSet<String>> = HashMap()
    private val colorStateListActive by lazy {
        ContextCompat.getColorStateList(requireContext(), R.color.button_active)
    }
    private val colorStateListDeActive by lazy {
        ContextCompat.getColorStateList(requireContext(), R.color.button_de_active)
    }
    private val drawable by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.baseline_check_24
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFilterOptionBinding.inflate(inflater, container, false)
        sharedProductViewModel =
            ViewModelProvider(requireActivity())[SharedProductViewModel::class.java]
        allProductsInfo = sharedProductViewModel.allProductsList
        maximumStockAvailable = allProductsInfo?.maxByOrNull { it.stock }?.stock ?: 0
        minimumStockAvailable = allProductsInfo?.minByOrNull { it.stock }?.stock ?: 0
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        resetFilters()
    }


    private fun resetFilters() {
        addProductInfo(0)
        with(binding) {
            setTintAndDeactivateOthers(
                productBrand,
                productCategory,
                productStock,
                productRating
            )
            updateRightLayout(0)
        }
    }


    private fun bindViews() {
        with(binding) {
            applyButton.setOnClickListener {
//                sharedProductViewModel.filteredItemsList = finalItemsSelectedSet
                sharedProductViewModel.finalItemsSelectedHasMap = finalItemsSelectedHasMap
                findNavController().navigate(R.id.action_FilterOptionFragment_to_homeFragment)
            }

            clearFilters.setOnClickListener {
                resetFilters()
                sharedProductViewModel.finalItemsSelectedHasMap = null
            }

            productBrand.setOnClickListener {
                setTintAndDeactivateOthers(
                    productBrand,
                    productCategory,
                    productStock,
                    productRating
                )
                updateRightLayout(0)
            }

            productCategory.setOnClickListener {
                setTintAndDeactivateOthers(
                    productCategory,
                    productBrand,
                    productStock,
                    productRating
                )
                updateRightLayout(1)
            }

            productRating.setOnClickListener {
                setTintAndDeactivateOthers(
                    productRating,
                    productBrand,
                    productCategory,
                    productStock
                )
                updateRightLayout(2)
            }

            productStock.setOnClickListener {
                setTintAndDeactivateOthers(
                    productStock,
                    productBrand,
                    productCategory,
                    productRating
                )
                updateRightLayout(3)
            }
        }
    }


    private fun updateRightLayout(filterType: Int) {
        addProductInfo(filterType)
    }


    private fun addProductInfo(filterType: Int) {
        binding.filteredItemOptionsLL.removeAllViews()
        when (FilterBy.values().firstOrNull { it.filterType == filterType }) {
            FilterBy.Rating -> {
                customRatingView()
                return
            }

            FilterBy.Stock -> {
                customStockView()
                return
            }

            else -> {}
        }
        with(binding) {
            val currentNonDuplicateSet: MutableSet<String> = mutableSetOf()
            val allFilteredInfo = allProductsInfo?.toHashSet()
            allFilteredInfo?.forEach { productInfo ->
                val checkBox = CheckBox(requireContext())
                checkBox.text =
                    when (FilterBy.values().firstOrNull { it.filterType == filterType }) {
                        FilterBy.Brand -> productInfo.brand
                        FilterBy.Category -> productInfo.category
                        FilterBy.Rating -> productInfo.rating.toString()
                        FilterBy.Stock -> productInfo.stock.toString()
                        else -> productInfo.brand
                    }
                val filterText = checkBox.text.toString()
                if (!currentNonDuplicateSet.contains(filterText)) {
                    checkBox.setPadding(20)
                    checkBox.layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                    )
                    filteredItemOptionsLL.addView(checkBox)
                    val sharedModelMap = sharedProductViewModel.finalItemsSelectedHasMap
                    sharedModelMap?.let { sharedViewModelHashMap ->
                        if (sharedViewModelHashMap.contains(filterType) &&
                            sharedViewModelHashMap[filterType]?.contains(filterText) == true
                        ) {
                            checkBox.isChecked = true
                        }
                    }
                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            finalItemsSelectedHasMap.computeIfAbsent(filterType) { HashSet() }
                                .add(filterText)
                        } else {
                            finalItemsSelectedHasMap[filterType]?.remove(filterText)
                        }
                    }
                    currentNonDuplicateSet.add(filterText)
                }
            }
        }
    }


    private fun customRatingView2() {
        /**
         * Range of rating:
         * 1 - 2 (2 excluded)
         * 2 - 3 (3 excluded)
         * 3 - 4 (4 excluded)
         * 4 - 4.5 (4.5 excluded)
         * 4.5 - 5 ( 5 included)
         */
        with(binding.filteredItemOptionsLL) {
            for (i in 1..4) {
                if (i != 4) {
                    // range will be [i, i+1)
                    val checkBox = CheckBox(requireContext())
                    with(checkBox) {
                        text = "$i - ${i + 1}"
                        setPadding(20)
                        layoutParams = RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT
                        )
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {

                            } else {

                            }
                        }
                    }
                    addView(checkBox)
                } else {
                    // range will be [4, 4.5) and [4.5, 5]
                    val checkBoxA = CheckBox(requireContext())
                    val checkBoxB = CheckBox(requireContext())
                    with(checkBoxA) {
                        text = "4 - 4.5"
                        setPadding(20)
                        layoutParams = RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT
                        )
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {

                            } else {

                            }
                        }
                    }
                    with(checkBoxB) {
                        text = "4.5 - 5"
                        setPadding(20)
                        layoutParams = RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT
                        )
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {

                            } else {

                            }
                        }
                    }
                    addView(checkBoxA)
                    addView(checkBoxB)
                    break
                }
            }
        }
    }


    private fun customRatingView() {
        /**
         * Range of rating:
         * 1 - 2 (2 excluded)
         * 2 - 3 (3 excluded)
         * 3 - 4 (4 excluded)
         * 4 - 4.5 (4.5 excluded)
         * 4.5 - 5 ( 5 included)
         */
        with(binding.filteredItemOptionsLL) {
            for (i in 1..3) {
                val checkBox = createCheckBox(i.toDouble(), (i + 1).toDouble(), 0)
                addView(checkBox)
            }

            val checkBoxA = createCheckBox(4.0, 4.5, 0)
            val checkBoxB = createCheckBox(4.6, 5.0, 0)
            addView(checkBoxA)
            addView(checkBoxB)
        }
    }


    private fun customStockView() {
        if (minimumStockAvailable != -1 && maximumStockAvailable != -1) {
            val averageOfStocks = (minimumStockAvailable + maximumStockAvailable) / 5
            Log.e("stockInfo1", "$minimumStockAvailable - $maximumStockAvailable")
            var startRange = minimumStockAvailable
            for (i in (minimumStockAvailable + averageOfStocks)..maximumStockAvailable step averageOfStocks) {
                val checkBox =
                    createCheckBox(startRange, i - 1, 1) // startRange inclusive but i not inclusive
                startRange = i
                binding.filteredItemOptionsLL.addView(checkBox)
            }
        }
    }


    private fun createCheckBox(rangeStart: Any, rangeEnd: Any, checkBoxType: Int): CheckBox {
        val checkBox = CheckBox(requireContext())
        checkBox.text = when {
            checkBoxType == 0 && (rangeEnd as Double) == 5.0 -> "$rangeStart - $rangeEnd"
            checkBoxType == 0 && (rangeStart as Double) < 4.0 -> "$rangeStart - $rangeEnd"
            else -> "$rangeStart - $rangeEnd"
        }
        checkBox.setPadding(20)
        checkBox.layoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        )
        val key = if (checkBoxType == 0) Constants.ratingKey else Constants.stockKey
        val sharedModelMap = sharedProductViewModel.finalItemsSelectedHasMap
        val checkBoxValue = rangeStart.toString() + "_" + rangeEnd.toString()
        sharedModelMap?.let { sharedViewModelHashMap ->
            if (sharedViewModelHashMap.contains(key) &&
                sharedViewModelHashMap[key]?.contains(checkBoxValue) == true
            ) {
                checkBox.isChecked = true
            }
        }
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            val value = rangeStart.toString() + "_" + rangeEnd.toString()
            finalItemsSelectedHasMap
                .computeIfAbsent(key) { HashSet() }
                .run { if (isChecked) add(value) else remove(value) }
        }
        return checkBox
    }


    private fun setTintAndDeactivateOthers(
        view: TextView,
        vararg otherViews: TextView
    ) {
        view.backgroundTintList = colorStateListActive
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            drawable,
            null
        )
        otherViews.forEach {
            it.backgroundTintList = colorStateListDeActive
            it.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                null,
                null
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
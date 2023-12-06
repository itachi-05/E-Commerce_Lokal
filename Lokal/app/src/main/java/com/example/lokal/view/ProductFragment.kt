package com.example.lokal.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lokal.R
import com.example.lokal.databinding.FragmentProductBinding
import com.example.lokal.models.ProductInfo
import com.example.lokal.viewmodels.SharedProductViewModel
import com.google.android.material.button.MaterialButton


class ProductFragment : Fragment() {
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedProductViewModel: SharedProductViewModel
    private var productInfo: ProductInfo? = null
    private lateinit var productImageAdapter: ProductImagesAdapter
    private val activeColor by lazy {
        ContextCompat.getColorStateList(
            requireContext(),
            R.color.button_carousel_active
        )
    }
    private val inActiveColor by lazy {
        ContextCompat.getColorStateList(
            requireContext(),
            R.color.button_carousel_inactive
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        sharedProductViewModel =
            ViewModelProvider(requireActivity())[SharedProductViewModel::class.java]
        productInfo = sharedProductViewModel.productInfo
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        with(binding) {
            with(lottieAnimationMoveDown) {
                playAnimation()
                setOnClickListener {
                    pauseAnimation()
                    visibility = View.GONE
                    lottieAnimationMoveUp.visibility = View.VISIBLE
                    lottieAnimationMoveUp.playAnimation()
                    remainingInfoLL.visibility = View.VISIBLE
                }
            }

            with(lottieAnimationMoveUp) {
                setOnClickListener {
                    pauseAnimation()
                    visibility = View.GONE
                    lottieAnimationMoveDown.visibility = View.VISIBLE
                    lottieAnimationMoveDown.playAnimation()
                    remainingInfoLL.visibility = View.GONE
                }
            }
        }
        productInfo?.let {
            productImageAdapter = ProductImagesAdapter(requireContext(), it.images)
            binding.productImageViewPager.adapter = productImageAdapter
        }
    }

    private fun bindViews() {
        with(binding) {
            productInfo?.let {
                productTitle.text = it.title
                // x - discountPercent% of x = cur_price
                productDescription.text = it.description
                productDiscountPercentage.text = "${it.discountPercentage}% off"
                val actualPrice = it.price / (1 - it.discountPercentage / 100)
                val formattedPrice = String.format("%.2f", actualPrice)
                val spannableString = SpannableString(formattedPrice)
                spannableString.setSpan(StrikethroughSpan(), 0, formattedPrice.length, 0)
                val price = SpannableStringBuilder()
                    .append("$")
                    .append(spannableString)
                    .append("\nAt $${it.price}")
                productPrice.text = price
                productStock.text = "In stock ${it.stock}"
                productCategory.text = "Category in ${it.category}"
                productBrand.text = it.brand
                productRating.text = "${it.rating} Rating"

                // add custom dynamic views
                val imagesCount = it.images
                for ((index, image) in imagesCount.withIndex()) {
                    val imageButton = MaterialButton(requireContext())

                    with(imageButton) {
                        cornerRadius = 5
                        backgroundTintList = inActiveColor
                        val layoutParams = LinearLayoutCompat.LayoutParams(60, 40)
                        layoutParams.weight = 1f / (imagesCount.size)
                        layoutParams.setMargins(10)
                        this.layoutParams = layoutParams
                        tag = index
                    }
                    imageButtonsLL.addView(imageButton)

                    if (index == 0) {
                        imageButton.backgroundTintList = activeColor
                    }
                    productImageViewPager.isUserInputEnabled = false

                    imageButton.setOnClickListener { view ->
                        val currentPosition = productImageViewPager.currentItem
                        val targetPosition = view.tag as? Int ?: 0

                        // Reset background color for all buttons
                        for (i in 0 until imageButtonsLL.childCount) {
                            val button = imageButtonsLL.getChildAt(i) as? MaterialButton
                            button?.backgroundTintList = inActiveColor
                        }

                        if (currentPosition != targetPosition) {
                            productImageViewPager.offscreenPageLimit = 1
                            productImageViewPager.setCurrentItem(targetPosition, false)
                        }

                        // Set the active color for the selected button
                        imageButton.backgroundTintList = activeColor
                    }
                }
            }
            addToCartButton.setOnClickListener {
                showToast("Added to cart")
            }
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
package com.example.lokal.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.setPadding
import com.example.lokal.databinding.CustomBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortBottomSheet(private val sheetOptions: List<String>) :
    BottomSheetDialogFragment() {
    private var _binding: CustomBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var bottomSheetListener: SortBSListener? = null
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    fun setBottomSheetListener(listener: SortBSListener) {
        bottomSheetListener = listener
    }

    companion object {
        fun newInstance(sheetOptions: List<String>): SortBottomSheet {
            return SortBottomSheet(sheetOptions)
        }
    }

    interface SortBSListener {
        fun sortOption(optionKeyMap: HashMap<String, String>)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CustomBottomSheetBinding.inflate(inflater, container, false)

        for (option in sheetOptions) {
            val radioButton = RadioButton(requireContext())
            radioButton.text = option
            radioButton.layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )
            radioButton.setPadding(35)
            binding.sortByRadioGroup.addView(radioButton)
            radioButton.setOnClickListener {
                val hashMap: HashMap<String, String> = hashMapOf(
                    "optionType" to option
                )
                bottomSheetListener?.sortOption(hashMap)
                handler.postDelayed({
                    dismiss()
                }, 500)
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
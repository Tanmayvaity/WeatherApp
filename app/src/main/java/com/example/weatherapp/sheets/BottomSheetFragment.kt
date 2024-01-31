package com.example.weatherapp.sheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.example.weatherapp.R
import com.example.weatherapp.databinding.AboutThisAppSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: AboutThisAppSheetLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AboutThisAppSheetLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.apply {
            settings.setSupportMultipleWindows(true)
            settings.setSupportZoom(true)
            isVerticalScrollBarEnabled = true


            loadUrl("https://github.com/Tanmayvaity/WeatherApp")
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }




}
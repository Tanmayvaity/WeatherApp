package com.example.weatherapp.sheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.weatherapp.R
import com.example.weatherapp.adapter.ActionAdapter
import com.example.weatherapp.databinding.AboutThisAppSheetLayoutBinding
import com.example.weatherapp.databinding.ActionSheetLayoutBinding
import com.example.weatherapp.models.Action
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: ActionSheetLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActionSheetLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionList = listOf(
            Action(
                "Download",
                R.drawable.ic_download
            ),
            Action(
                "Save",
                R.drawable.ic_bookmark_border
            ),
            Action(
                "Share",
                R.drawable.ic_share
            )
        )

        val actionAdapter = ActionAdapter(requireContext(),actionList)
        binding.lvAction.adapter = actionAdapter
    }



}
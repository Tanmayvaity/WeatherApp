package com.example.weatherapp.sheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.ListView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import com.example.weatherapp.R
import com.example.weatherapp.adapter.ActionAdapter
import com.example.weatherapp.databinding.AboutThisAppSheetLayoutBinding
import com.example.weatherapp.databinding.ActionSheetLayoutBinding
import com.example.weatherapp.models.Action
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: ActionSheetLayoutBinding? = null
    private var status:Boolean = false
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


        binding.download.tvActionTopic.text = "Download"
        binding.download.ivAction.setImageResource(R.drawable.ic_download)
        binding.share.tvActionTopic.text = "Share"
        binding.share.ivAction.setImageResource(R.drawable.ic_share)

        val isBookmarkChecked = arguments?.getBoolean("isChecked")
        if(isBookmarkChecked!=null){
            binding.cbSheetBookmark.isChecked = isBookmarkChecked
        }

        binding.clBookmark.setOnClickListener{
            checkboxStateLogic()
        }

        binding.cbSheetBookmark.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                setFragmentResult("checkboxStatus", bundleOf("isChecked" to isChecked ))
            }
        })





//        val actionList = listOf(
//            Action(
//                "Download",
//                R.drawable.ic_download
//            ),
//            Action(
//                "Save",
//                R.drawable.ic_bookmark_border
//            ),
//            Action(
//                "Share",
//                R.drawable.ic_share
//            )
//        )
//
//        val actionAdapter = ActionAdapter(requireContext(),actionList)
//        binding.lvAction.adapter = actionAdapter
//
//
//        binding.lvAction.setOnItemClickListener(object: AdapterView.OnItemClickListener{
//            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//
//            }
//        })
    }


    private fun checkboxStateLogic(){
        if(binding.cbSheetBookmark.isChecked){
            binding.cbSheetBookmark.isChecked = false
            status = false

        }else{
            binding.cbSheetBookmark.isChecked = true
            status = true

        }

        setFragmentResult("checkboxStatus", bundleOf("isChecked" to status ))
    }

}
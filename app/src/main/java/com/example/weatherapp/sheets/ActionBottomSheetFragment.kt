package com.example.weatherapp.sheets

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.ListView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import com.example.weatherapp.R
import com.example.weatherapp.adapter.ActionAdapter
import com.example.weatherapp.databinding.AboutThisAppSheetLayoutBinding
import com.example.weatherapp.databinding.ActionSheetLayoutBinding
import com.example.weatherapp.fragments.WeatherFragment
import com.example.weatherapp.models.Action
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: ActionSheetLayoutBinding? = null
    private var status:Boolean = false
    private val binding get() = _binding!!
    private lateinit var writeRequestPermissionLauncher: ActivityResultLauncher<String>


    override fun onAttach(context: Context) {
        super.onAttach(context)

        writeRequestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if(isGranted){
                Log.d(WeatherFragment.TAG,"WRITE PERMISSION GRANTED")
            }else{
                // cannot save images
                rationaleDialog()
            }
        }
    }
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

        binding.download.root.setOnClickListener{
            saveViewAsImagePermissionLogic()

        }
    }

    private fun saveViewAsImagePermissionLogic() {
        when{
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q->{
                (parentFragment as WeatherFragment).saveViewAsImage()
            }

            shouldShowRequestPermissionRationale(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) ->{
                rationaleDialog()
            }

            else -> {
                writeRequestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }

        }
    }

    private fun rationaleDialog(){
        val rationalDialog = AlertDialog.Builder(requireContext())
            .setTitle("Weather App")
            .setMessage("You won't be able to save images without this")
            .setNegativeButton("Settings", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, p1: Int) {
                    val writePermissionIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    writePermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = Uri.fromParts("package", context?.packageName, null)
                    writePermissionIntent.data = uri
                    startActivity(writePermissionIntent)
                    dialog?.dismiss()
                }
            })
            .setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, p1: Int) {
                    dialog?.dismiss()
                }
            })
            .show()
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
package com.example.weatherapp.fragments

import android.Manifest
import android.animation.LayoutTransition
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.transition.TransitionManager
import android.util.Log
import android.util.Log.i
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class WeatherFragment : Fragment() {

    private lateinit var _binding: FragmentWeatherBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val binding get() = _binding

    private var isVisible = false
    private lateinit var locationRequestPermissionLauncher: ActivityResultLauncher<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationRequestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d(TAG, "location permission granted")
                } else {
                    showRationalDialog()
                }
            }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        checkForPermissions(requireContext())
    }



    private fun checkForPermissions(context: Context) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,null).addOnSuccessListener {
                    location ->
                    val lat = location.latitude
                    val long = location.longitude
                    Log.d(TAG, "lat $lat long $long")
                }
                Log.d(TAG,"permission granted lol")

            }
           shouldShowRequestPermissionRationale(
               Manifest.permission.ACCESS_COARSE_LOCATION
           )->{
               Log.d(TAG, "why here")

//               showRationalDialog()
           }
            else ->{
                locationRequestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        }
    }


    private fun showRationalDialog() {
        val rationalDialog = AlertDialog.Builder(requireContext())
            .setTitle("Weather App")
            .setMessage("The app won't be able to automatically detect your current information")
            .setNegativeButton("Settings", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, p1: Int) {
                    val locationIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    locationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = Uri.fromParts("package", context?.packageName, null)
                    locationIntent.data = uri
                    startActivity(locationIntent)
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


    override fun onDestroy() {
        Log.d(TAG, "Fragment : onDestroy")
        super.onDestroy()

    }


    companion object {

        const val TAG = "WeatherFragment"
        fun newInstance() = WeatherFragment()

    }
}
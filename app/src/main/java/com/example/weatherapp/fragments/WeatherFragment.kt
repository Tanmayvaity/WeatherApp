package com.example.weatherapp.fragments

import android.animation.LayoutTransition
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log.i
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.databinding.FragmentWeatherBinding


class WeatherFragment : Fragment() {

    private lateinit var _binding : FragmentWeatherBinding
    private val binding get() = _binding

    private var isVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWeatherBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.cardViewContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
//
//        binding.cardViewContainer.setOnClickListener{
//            TransitionManager.beginDelayedTransition(binding.cardViewContainer)
//            if(binding.tvLorem.visibility == View.GONE){
//                binding.tvLorem.visibility = View.VISIBLE
//            }else{
//                binding.tvLorem.visibility = View.GONE
//            }
//
//
//        }

        initCardViewData()

        binding.tvShowMore.setOnClickListener{
            TransitionManager.beginDelayedTransition(binding.weatherDetailsCard)
            if(isVisible){
                binding.sunriseLayout.root.visibility = View.GONE
                binding.daylightLayout.root.visibility = View.GONE
                binding.sunsetLayout.root.visibility = View.GONE
                binding.maxMinLayout.root.visibility = View.GONE
                binding.uvIndexLayout.root.visibility = View.GONE
                binding.pressureLayout.root.visibility = View.GONE
                binding.preciLayout.root.visibility = View.GONE
                binding.humidityLayout.root.visibility = View.GONE
                binding.windLayout.root.visibility = View.GONE

                isVisible = false
            }else{
                binding.sunriseLayout.root.visibility = View.VISIBLE
                binding.daylightLayout.root.visibility = View.VISIBLE
                binding.sunsetLayout.root.visibility = View.VISIBLE
                binding.maxMinLayout.root.visibility = View.VISIBLE
                binding.uvIndexLayout.root.visibility = View.VISIBLE
                binding.pressureLayout.root.visibility = View.VISIBLE
                binding.preciLayout.root.visibility = View.VISIBLE
                binding.humidityLayout.root.visibility = View.VISIBLE
                binding.windLayout.root.visibility = View.VISIBLE
                isVisible = true
            }

        }





    }

    private fun initCardViewData(){
        binding.daylightLayout.tvHeading.text = "Daylight"
        binding.daylightLayout.tvData.text  = "13 h 40 min"

        binding.sunsetLayout.tvHeading.text = "Sunset"
        binding.sunsetLayout.tvData.text = "20:02"

        binding.maxMinLayout.tvHeading.text = "max-min"
        binding.maxMinLayout.tvData.text = "15-9"

        binding.uvIndexLayout.tvHeading.text = "UV index"
        binding.uvIndexLayout.tvData.text = "8"

        binding.pressureLayout.tvHeading.text = "Pressure"
        binding.pressureLayout.tvData.text = "762 mmHg"

        binding.preciLayout.tvHeading.text = "Precipprob"
        binding.preciLayout.tvData.text = "0%"

        binding.humidityLayout.tvHeading.text = "Humidity"
        binding.windLayout.tvData.text = "4 m/s,SW"

    }

    companion object {

        fun newInstance() = WeatherFragment()

    }
}
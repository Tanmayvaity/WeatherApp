package com.example.weatherapp.fragments

import android.Manifest
import android.animation.LayoutTransition
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.provider.Settings
import android.transition.TransitionManager
import android.transition.Visibility
import android.util.Log
import android.util.Log.i
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherapp.Keys

import com.example.weatherapp.R
import com.example.weatherapp.api.WeatherApiInstance
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.models.Weather
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class WeatherFragment : Fragment() {

    private lateinit var _binding: FragmentWeatherBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var weatherInformation: Weather? = null

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.tbWeather)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        checkForPermissions(requireContext())



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"Fragment:onCreate")
    }

    override fun onStart() {
        super.onStart()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private  fun fetchData(lat:Double, long:Double){

        var weather : Weather? = null

        val supervisorJob = SupervisorJob()


        binding.progressbar.isVisible = true
        lifecycleScope.launch(Dispatchers.IO + supervisorJob) {
             val weatherInfo : Deferred<Weather?> = async {
                 startCoroutineToFetchData(lat, long, Keys.weatherApiKey)


             }

            weather = weatherInfo.await()

            setWeatherData(weather)

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun startCoroutineToFetchData(lat:Double, long:Double, apiKey:String):Weather?{
        var  weather : Weather? = null
        val response = try{
            WeatherApiInstance.api.getWeather(lat,long,apiKey)
        }catch (e:IOException){
            withContext(Dispatchers.Main){
                binding.progressbar.isVisible = false
            }
            Log.e(TAG,"something wrong with internet connection")
            return null
        }catch (e: HttpException){
            withContext(Dispatchers.Main){
                binding.progressbar.isVisible = false
            }
            Log.e(TAG,"unexpected response")
            return null
        }

        if(response.isSuccessful && response.body()!=null){
            Log.d(TAG,"${response.raw().request.url}")
             weather = response.body()
            Log.d(TAG,"$weather")



            setWeatherData(weather)

            withContext(Dispatchers.Main){
                binding.llContainer.isVisible = true
                binding.tvToolbar.isVisible = true
            }

        }else{
            Log.e(TAG,"Response not successful")
        }

        withContext(Dispatchers.Main){
            binding.progressbar.isVisible = false
        }

        return weather
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun setWeatherData(weather: Weather?) {
        withContext(Dispatchers.Main){
            weather?.let{weather ->
                binding.tvToolbar.text = weather.name.toString()
                binding.tvTemperature.text = "${weather.main.temp.toString().toCelsius()}\u00B0"
                binding.tvWeatherConditions.text = weather.weather[0].weatherCondition.toString()
                Log.d(TAG,"${weather.clouds.cloudPercent}")
                binding.tvCloudPercent.text = when(weather.clouds.cloudPercent){
                    in 0 until 10 -> "Clear Sky"
                    in 10 until 20 -> "Mostly Clear"
                    in 20 until 50 -> "Partly Cloudy"
                    in 50 until 80 -> "Mostly Cloudy"
                    else -> "Overcast"

                }

                binding.tvHumidityPercent.text = "${weather.main.humidity.toString()}%"
                binding.tvVisibility.text = "${weather.visibility.toDouble()/1000} km"
                binding.tvWindSpeed.text = "${weather.wind.speed} m/sec"
                binding.tvPressure.text = "${weather.main.pressure} hPa"
                if(weather.rain?.volumeForOneHour != null){
                    binding.tvRain.text = "${weather.rain.volumeForOneHour}"
                }else{
                    binding.rainContainer.isVisible = false
                }

                if(weather.snow?.volumeForOneHour != null){
                    binding.tvSnow.text = "${weather.snow.volumeForOneHour}"
                }else{
                    binding.snowContainer.isVisible = false
                }

//                val value = weather.sys.sunrise.toString().toDate().formatTo("HH:mm a")


                val sunriseTime = weather.sys.sunrise.convertUnixUTCTimeToString()
                val sunsetTime = weather.sys.sunset.convertUnixUTCTimeToString()
                binding.tvSunrise.text = sunriseTime.toString()
                binding.tvSunset.text = sunsetTime.toString()

                binding.tvFeelsLike.text = "Feels Like ${weather.main.feelsLike.toString().toCelsius()}\u00B0"

//                val isoDateFormat = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(weather.sys.sunrise.toLong()))

//                val inputFormatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.ENGLISH)
//                val outputFormatter : DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm a",Locale.ENGLISH)
//                val date : LocalDateTime = LocalDateTime.parse(isoDateFormat.toString(),inputFormatter)
//                val formattedDate : String  = outputFormatter.format(date)

            }
        }
    }

    private fun String.toCelsius():String {
        val celsius = (this.toDouble()-273.15)
        return celsius.toLong().toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun String.convertUnixUTCTimeToString():String{
        val instant = Instant.ofEpochSecond(this.toLong())
        val zone : ZoneId = ZoneId.of("Asia/Kolkata")
        val zdt:ZonedDateTime = instant.atZone(zone)
        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm a ")
        val formattedString: String = zdt.format(outputFormatter)

        return formattedString
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkForPermissions(context: Context) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,null).addOnSuccessListener {
                    location ->
                    if(location!=null){
                        val lat = location.latitude
                        val long = location.longitude
                        Log.d(TAG, "lat $lat long $long")

                        fetchData(lat,long)


                    }else{
                        Toast.makeText(requireContext(),"turn on the location of your device",Toast.LENGTH_SHORT).show()
                    }

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

    fun String.toDate(dateFormat: String = "HH:mm a", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)!!
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }


    companion object {

        const val TAG = "WeatherFragment"
        fun newInstance() = WeatherFragment()

    }
}
package com.example.weatherapp.fragments

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.Keys
import com.example.weatherapp.api.WeatherApiInstance
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.models.GeoCoding
import com.example.weatherapp.models.Weather
import com.example.weatherapp.viewmodels.WeatherFragmentViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.io.Serializable
import java.net.SocketException
import java.text.SimpleDateFormat
import java.time.Instant
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
    private val weatherFragmentViewModel:WeatherFragmentViewModel by viewModels()
    private val binding get() = _binding
    private var weather : Weather? = null
    private var id:Int = 0
    private var call = true
    private lateinit var locationRequestPermissionLauncher: ActivityResultLauncher<String>

   override fun onAttach(context: Context) {
        super.onAttach(context)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d(TAG, "location permission granted")
                    checkForPermissions(requireContext())
                } else {
                    showRationalDialog()
                }
            }


       Log.d(TAG,"WeatherFragment:onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"WeatherFragment:onCreate")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG,"WeatherFragment:onCreateView")
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.tbWeather)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        Log.d(TAG,"WeatherFragment:onViewCreated")

        val geoCodingData : GeoCoding? = arguments?.getSerializable(LocationFragment.LOCATION_DATA,GeoCoding::class.java)


        if(geoCodingData!=null){
            val lat = geoCodingData.lat
            val lon = geoCodingData.lon
            fetchData(lat,lon)
        }else{
            checkForPermissions(requireContext())
        }

        binding.refresh.setOnRefreshListener {
            if(geoCodingData!=null){
                val lat = geoCodingData.lat
                val lon = geoCodingData.lon
                fetchData(lat,lon)
            }else{
                checkForPermissions(requireContext())
            }
        }
    }

    private fun checkForPermissions(context: Context) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                binding.refresh.isRefreshing = true
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,null).addOnSuccessListener {
                        location ->
                    if(location!=null){
                        val lat = location.latitude
                        val long = location.longitude
                        Log.i(TAG, "lat $lat long $long")
                        fetchData(lat,long)
                    }else{
                        Toast.makeText(requireContext(),"turn on the location of your device",Toast.LENGTH_SHORT).show()
                    }
                }
                Log.d(TAG,"ACCESS_COARSE_LOCATION : permission granted ")
            }
            shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )->{
                Log.d(TAG, "rationale dialog")
                showRationalDialog()
            }
            else ->{
                locationRequestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private  fun fetchData(lat:Double, long:Double){
        val supervisorJob = SupervisorJob()
        lifecycleScope.launch(Dispatchers.IO + supervisorJob) {
             val weatherInfo : Deferred<Weather?> = async {
                 startCoroutineToFetchData(lat, long, Keys.weatherApiKey)
             }
            weather = weatherInfo.await()
            if(weather == null){
                Log.d(TAG,"null")
            }
            setWeatherData(weather)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun startCoroutineToFetchData(lat:Double, long:Double, apiKey:String):Weather?{
        var  weather : Weather? = null
        withContext(Dispatchers.Main){
            binding.progressbar.isVisible = false
            binding.refresh.isRefreshing = true
        }
        val response = try{

            WeatherApiInstance.api.getWeather(lat,long,apiKey)
        }catch (e:IOException){
            withContext(Dispatchers.Main){
                binding.progressbar.isVisible = false
                binding.refresh.isRefreshing = false
               Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
            }
            Log.e(TAG,"No internet Connection")
            return null
        }catch (e:SocketException){
            withContext(Dispatchers.Main){
                binding.progressbar.isVisible = false
                binding.refresh.isRefreshing = false
                Toast.makeText(requireContext(),"Something wrong with internet connection",Toast.LENGTH_SHORT).show()
            }
            Log.e(TAG,"Something wrong with internet connection")
            return null
        }catch (e: HttpException){
            withContext(Dispatchers.Main){
                binding.progressbar.isVisible = false
                binding.refresh.isRefreshing = false
            }
            Log.e(TAG,"Exception for an unexpected, non-2xx HTTP response")
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
            binding.refresh.isRefreshing = false
        }
        return weather
    }



    private suspend fun setWeatherData(weather: Weather?) {
        withContext(Dispatchers.Main){
            if(weather!=null){
                setData(weather)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData(weather:Weather){
        binding.tvToolbar.text = weather.name.toString()
        binding.tvTemperature.text = "${weather.main.temp.toString().toCelsius()}\u00B0"
        binding.tvWeatherConditions.text = weather.weather[0].weatherCondition.toString()
        Log.d(TAG,"Clouds percent : ${weather.clouds.cloudPercent}")
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
        const val WEATHER_DATA_TAG = "WeatherData"
        fun newInstance() = WeatherFragment()

    }

    override fun toString(): String {
        return "WeatherFragment"

    }
}
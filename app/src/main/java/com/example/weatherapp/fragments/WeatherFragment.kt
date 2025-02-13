package com.example.weatherapp.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.Keys
import com.example.weatherapp.api.ApiResult
import com.example.weatherapp.api.ApiStatus
import com.example.weatherapp.api.WeatherApiInstance
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.models.GeoCoding
import com.example.weatherapp.models.Weather
import com.example.weatherapp.sheets.ActionBottomSheetFragment
import com.example.weatherapp.viewmodels.WeatherFragmentViewModel
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
import java.net.SocketException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class WeatherFragment : Fragment() {

    private lateinit var _binding: FragmentWeatherBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var weatherInformation: Weather? = null
    private val weatherFragmentViewModel: WeatherFragmentViewModel by viewModels()
    private val binding get() = _binding
    private var weather: Weather? = null
    private var id: Int = 0
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



        Log.d(TAG, "WeatherFragment:onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "WeatherFragment:onCreate")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "WeatherFragment:onCreateView")
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.tbWeather)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        Log.d(TAG, "WeatherFragment:onViewCreated")

        val geoCodingData: GeoCoding? =
            arguments?.getSerializable(LocationFragment.LOCATION_DATA, GeoCoding::class.java)


        if (geoCodingData != null) {
            val lat = geoCodingData.lat
            val lon = geoCodingData.lon
            fetchData(lat, lon)
        } else {
            checkForPermissions(requireContext())
        }

        binding.refresh.setOnRefreshListener {
            if (geoCodingData != null) {
                val lat = geoCodingData.lat
                val lon = geoCodingData.lon
                fetchData(lat, lon)

            } else {
                checkForPermissions(requireContext())
            }
        }

        binding.cbBookmark.setOnTouchListener(object : OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    val aboutBottomSheet = ActionBottomSheetFragment()
                    val args = Bundle()
                    args.putBoolean("isChecked", binding.cbBookmark.isChecked)
                    aboutBottomSheet.arguments = args
                    val fm = childFragmentManager
                    aboutBottomSheet.show(fm, "AboutBottomSheetFragment")
                    return true
                }

                return false

            }

        })


        childFragmentManager.setFragmentResultListener(
            "checkboxStatus",
            viewLifecycleOwner,
            object :
                FragmentResultListener {
                override fun onFragmentResult(requestKey: String, result: Bundle) {
                    val cbBookmarkCheckedStatus = result.getBoolean("isChecked")
                    binding.cbBookmark.isChecked = cbBookmarkCheckedStatus
                }
            })

    }

    public fun saveViewAsImage() {
        val height = binding.refresh.getChildAt(0).height
        val width = binding.refresh.getChildAt(0).width
        val bitmap = getViewToBitmap(height, width, binding.refresh)

        val resolver = requireActivity().applicationContext.contentResolver
        val imageCollection =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            }else{
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val time = Calendar.getInstance()

        Log.d(TAG, time.toString())
        val imageDetails = ContentValues().apply{
            put(MediaStore.Images.Media.DISPLAY_NAME,"${time.time.time}.png")
            put(MediaStore.Images.Media.MIME_TYPE,"image/png")
            put(MediaStore.Images.Media.WIDTH,bitmap.width)
            put(MediaStore.Images.Media.HEIGHT,bitmap.height)
        }

        try{
            resolver.insert(imageCollection,imageDetails)?.also{uri->
                resolver.openOutputStream(uri)?.use{stream ->
                    if(bitmap.compress(
                        Bitmap.CompressFormat.PNG,100,stream
                    )) {
                        Toast.makeText(requireContext(),"view saved successfully",Toast.LENGTH_SHORT).show()
                    }else{
                        throw IOException("Couldn't save bitmap")
                    }

                }
            } ?: throw IOException("Couldn't create MediaStore entry")
        }catch (e:Exception){
            e.printStackTrace()
        }



    }

    private fun getViewToBitmap(height: Int, width: Int, root: View): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = root.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        root.draw(canvas)

        return bitmap
    }

    private fun checkForPermissions(context: Context) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                binding.refresh.isRefreshing = true
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        val lat = location.latitude
                        val long = location.longitude
                        Log.i(TAG, "lat $lat long $long")
                        fetchData(lat, long)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "turn on the location of your device",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Log.d(TAG, "ACCESS_COARSE_LOCATION : permission granted ")
            }

            shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                Log.d(TAG, "rationale dialog")
                showRationalDialog()
            }

            else -> {
                locationRequestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchData(lat: Double, long: Double) {
        val supervisorJob = SupervisorJob()
        lifecycleScope.launch(Dispatchers.IO + supervisorJob) {
            val defferedApiResult: Deferred<ApiResult<Weather>> = async {
                withContext(Dispatchers.Main) {
                    toggleRefresh(true)
                }
                startCoroutineToFetchData(lat, long, Keys.weatherApiKey)
            }
//            weather = weatherInfo.await()
//            if(weather == null){
//                Log.d(TAG,"null")
//            }

            val apiResult = defferedApiResult.await()


            when (apiResult.status) {
                ApiStatus.SUCCESS -> {
                    weather = apiResult.data
                    setWeatherData(apiResult.data)
                    Log.d(TAG, "${apiResult.data}")
                }

                ApiStatus.INTERNET_TURNED_OFF -> {
                    Log.e(TAG, "No internet Connection")
                    Log.e(TAG, "${apiResult.message}")
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT)
                        .show()

                }

                ApiStatus.INTERNET_NOT_WORKING -> {
                    Log.e(TAG, "Something wrong with internet connection")
                    Log.e(TAG, "${apiResult.message}")
                    Toast.makeText(requireContext(), "Internet not working", Toast.LENGTH_SHORT)
                        .show()

                }

                ApiStatus.HTTP_CODE_ERROR -> {
                    Log.e(TAG, "Exception for an unexpected, non-2xx HTTP response")
                    Log.e(TAG, "${apiResult.message}")
                    Toast.makeText(requireContext(), "non-2xx error", Toast.LENGTH_SHORT).show()

                }

                ApiStatus.ERROR -> {
                    Log.e(TAG, "${apiResult.message}")
                    Toast.makeText(
                        requireContext(),
                        "Something unexpected happened,please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            withContext(Dispatchers.Main) {
                toggleRefresh(false)
            }


        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun startCoroutineToFetchData(
        lat: Double,
        long: Double,
        apiKey: String
    ): ApiResult<Weather> {
        var weather: Weather? = null

        val apiResult = try {
            val data = WeatherApiInstance.api.getWeather(lat, long, apiKey)
            ApiResult<Weather>(
                status = ApiStatus.SUCCESS,
                data = data.body()
            )
        } catch (e: IOException) {
            Log.e(TAG, "No internet Connection")
            ApiResult<Weather>(
                status = ApiStatus.INTERNET_TURNED_OFF,
                message = e.message
            )
        } catch (e: SocketException) {
            Log.e(TAG, "Something wrong with internet connection")
            ApiResult<Weather>(
                status = ApiStatus.INTERNET_NOT_WORKING,
                message = e.message
            )
        } catch (e: HttpException) {
            Log.e(TAG, "Exception for an unexpected, non-2xx HTTP response")
            ApiResult<Weather>(
                status = ApiStatus.HTTP_CODE_ERROR,
                message = e.message
            )
        } catch (e: Exception) {
            ApiResult<Weather>(
                status = ApiStatus.ERROR,
                message = e.message
            )
        }
        return apiResult
    }

    private fun toggleRefresh(toggle: Boolean) {
        binding.refresh.isRefreshing = toggle
        binding.llContainer.isVisible = !toggle
        binding.tvToolbar.isVisible = !toggle
        binding.cbBookmark.isVisible = !toggle
    }


    private suspend fun setWeatherData(weather: Weather?) {
        withContext(Dispatchers.Main) {
            if (weather != null) {
                setData(weather)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData(weather: Weather) {
        binding.tvToolbar.text = weather.name.toString()
        binding.tvTemperature.text = "${weather.main.temp.toString().toCelsius()}\u00B0"
        binding.tvWeatherConditions.text = weather.weather[0].weatherCondition.toString()
        Log.d(TAG, "Clouds percent : ${weather.clouds.cloudPercent}")
        binding.tvCloudPercent.text = when (weather.clouds.cloudPercent) {
            in 0 until 10 -> "Clear Sky"
            in 10 until 20 -> "Mostly Clear"
            in 20 until 50 -> "Partly Cloudy"
            in 50 until 80 -> "Mostly Cloudy"
            else -> "Overcast"
        }

        binding.tvHumidityPercent.text = "${weather.main.humidity.toString()}%"
        binding.tvVisibility.text = "${weather.visibility.toDouble() / 1000} km"
        binding.tvWindSpeed.text = "${weather.wind.speed} m/sec"
        binding.tvPressure.text = "${weather.main.pressure} hPa"
        if (weather.rain?.volumeForOneHour != null) {
            binding.tvRain.text = "${weather.rain.volumeForOneHour}"
        } else {
            binding.rainContainer.isVisible = false
        }

        if (weather.snow?.volumeForOneHour != null) {
            binding.tvSnow.text = "${weather.snow.volumeForOneHour}"
        } else {
            binding.snowContainer.isVisible = false
        }

//                val value = weather.sys.sunrise.toString().toDate().formatTo("HH:mm a")


        val sunriseTime = weather.sys.sunrise.convertUnixUTCTimeToString()
        val sunsetTime = weather.sys.sunset.convertUnixUTCTimeToString()
        binding.tvSunrise.text = sunriseTime.toString()
        binding.tvSunset.text = sunsetTime.toString()

        binding.tvFeelsLike.text =
            "Feels Like ${weather.main.feelsLike.toString().toCelsius()}\u00B0"

//                val isoDateFormat = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(weather.sys.sunrise.toLong()))

//                val inputFormatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.ENGLISH)
//                val outputFormatter : DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm a",Locale.ENGLISH)
//                val date : LocalDateTime = LocalDateTime.parse(isoDateFormat.toString(),inputFormatter)
//                val formattedDate : String  = outputFormatter.format(date)
    }

    private fun String.toCelsius(): String {
        val celsius = (this.toDouble() - 273.15)
        return celsius.toLong().toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun String.convertUnixUTCTimeToString(): String {
        val instant = Instant.ofEpochSecond(this.toLong())
        val zone: ZoneId = ZoneId.of("Asia/Kolkata")
        val zdt: ZonedDateTime = instant.atZone(zone)
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

    fun String.toDate(
        dateFormat: String = "HH:mm a",
        timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    ): Date {
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
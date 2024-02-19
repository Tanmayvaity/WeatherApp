package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.fragments.LocationFragment
import com.example.weatherapp.fragments.WeatherFragment
import com.example.weatherapp.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    companion object{
        const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavView.setOnItemSelectedListener {
            fragmentNavigationLogic(it)
        }
        binding.bottomNavView.selectedItemId = mainActivityViewModel.selectedFragment



    }

    override fun onStart() {
        super.onStart()
//        hasNetworkConnection()
    }

//    private fun hasNetworkConnection() : Boolean{
//        val connectivityManager : ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    }

    private fun fragmentNavigationLogic(item: MenuItem):Boolean{
        val fragmentManager = supportFragmentManager
        var currentFragmentResourceId = R.id.location_screen
        lateinit var  fragment : Fragment
        when(item.itemId){
            R.id.location_screen ->{
                fragment = LocationFragment.newInstance()
                currentFragmentResourceId = R.id.location_screen
            }
            R.id.weather_screen ->{
                Log.d(TAG,"weather")
                fragment = WeatherFragment.newInstance()
                currentFragmentResourceId = R.id.weather_screen

            }
        }
        fragmentManager.commit {
            replace(R.id.fragment_container,fragment)
            mainActivityViewModel.selectedFragment = currentFragmentResourceId
        }
        return true
    }
}
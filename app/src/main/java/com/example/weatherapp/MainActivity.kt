package com.example.weatherapp


import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.fragments.LocationFragment
import com.example.weatherapp.fragments.WeatherFragment
import com.example.weatherapp.viewmodels.MainActivityViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private lateinit var networkRequest: NetworkRequest
    private lateinit var networkCallback: NetworkCallback
    private var lastValidityState = false

    companion object {
        const val TAG = "MainActivity"
        const val CONFIG_CHANGE = "ConfigChange"
        const val NETWORK_STATE = "NetworkState"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkRequest = getNetworkRequest()
        networkCallback = getNetworkCallBack()
        binding.bottomNavView.setOnItemSelectedListener {
            fragmentNavigationLogic(it, savedInstanceState)
        }
        binding.bottomNavView.selectedItemId = mainActivityViewModel.selectedFragment


        Log.d(TAG, "MainActivity:onCreate")
    }

    override fun onStart() {
        super.onStart()
//        getConnectivityManager().registerNetworkCallback(networkRequest, networkCallback)
        getConnectivityManager().registerDefaultNetworkCallback(networkCallback)

    }

    override fun onPause() {
        super.onPause()
        getConnectivityManager().unregisterNetworkCallback(networkCallback)
    }

    private fun getConnectivityManager(): ConnectivityManager {
        return this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private fun getNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }
// TODO
    private fun getNetworkCallBack(): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                Log.d(NETWORK_STATE, "Internet available")
            }

            override fun onUnavailable() {
                super.onUnavailable()
                Log.d(NETWORK_STATE, "Internet unavailable")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d(NETWORK_STATE, "Internet Lost")
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val networkValidity =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                if (networkValidity && networkValidity != lastValidityState) {
                    Log.d(NETWORK_STATE, "Internet is working: $networkValidity")
                }
                lastValidityState = networkValidity

            }
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "ORIENTATION_LANDSCAPE")
        }

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "ORIENTATION_PORTRAIT")
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean(CONFIG_CHANGE, true)
    }

    private fun fragmentNavigationLogic(item: MenuItem, savedInstanceState: Bundle?): Boolean {
        val fragmentManager = supportFragmentManager
        var currentFragmentResourceId = R.id.location_screen
        lateinit var fragment: Fragment

        when (item.itemId) {
            R.id.location_screen -> {

                fragment = LocationFragment.newInstance()
                currentFragmentResourceId = R.id.location_screen
            }

            R.id.weather_screen -> {
                fragment = WeatherFragment.newInstance()
                currentFragmentResourceId = R.id.weather_screen

            }
        }
        fragmentManager.commit {
            replace(R.id.fragment_container, fragment, fragment.toString())

            mainActivityViewModel.selectedFragment = currentFragmentResourceId
        }
        return true
    }


}
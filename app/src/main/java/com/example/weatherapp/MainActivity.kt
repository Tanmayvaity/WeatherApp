package com.example.weatherapp


import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
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
        const val CONFIG_CHANGE="ConfigChange"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavView.setOnItemSelectedListener {
            fragmentNavigationLogic(it,savedInstanceState)
        }
        binding.bottomNavView.selectedItemId = mainActivityViewModel.selectedFragment


        Log.d(TAG,"MainActivity:onCreate")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.d(TAG,"ORIENTATION_LANDSCAPE")
        }

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d(TAG,"ORIENTATION_PORTRAIT")
        }
    }
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean(CONFIG_CHANGE,true)
    }
    private fun fragmentNavigationLogic(item: MenuItem,savedInstanceState: Bundle?):Boolean{
        val fragmentManager = supportFragmentManager
        var currentFragmentResourceId = R.id.location_screen
        lateinit var  fragment : Fragment

        when(item.itemId){
            R.id.location_screen ->{

                fragment = LocationFragment.newInstance()
                currentFragmentResourceId = R.id.location_screen
            }
            R.id.weather_screen ->{
                fragment = WeatherFragment.newInstance()
                currentFragmentResourceId = R.id.weather_screen

            }
        }
        fragmentManager.commit {
            replace(R.id.fragment_container,fragment,fragment.toString())

            mainActivityViewModel.selectedFragment = currentFragmentResourceId
        }
        return true
    }

}
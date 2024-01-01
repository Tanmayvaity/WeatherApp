package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.fragments.LocationFragment
import com.example.weatherapp.fragments.SettingsFragment
import com.example.weatherapp.fragments.WeatherFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.bottomNavView.setOnItemSelectedListener {
            fragmentNavigationLogic(it)
        }
        binding.bottomNavView.selectedItemId = R.id.location_screen

    }

    private fun fragmentNavigationLogic(item: MenuItem):Boolean{
        val fragmentManager = supportFragmentManager
        var fragment : Fragment? = null
        when(item.itemId){
            R.id.location_screen ->{
                fragment = LocationFragment.newInstance()
            }
            R.id.weather_screen ->{
                fragment = WeatherFragment.newInstance()
            }
            R.id.settings_screen ->{
                fragment = SettingsFragment.newInstance()
            }
        }

        fragmentManager.beginTransaction().replace(R.id.fragment_container,fragment!!).commit()
        return true
    }
}
package com.example.weatherapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivitySettingsBinding
import com.example.weatherapp.fragments.SettingsScreen

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inflateFragment()
    }

    private fun inflateFragment(){
        val fm = supportFragmentManager
        fm.commit {
            replace(R.id.settings_container,SettingsScreen())
        }
    }
}
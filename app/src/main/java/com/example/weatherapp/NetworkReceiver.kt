package com.example.weatherapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.Settings
import android.util.Log

class NetworkReceiver: BroadcastReceiver() {

    companion object{
        const val  TAG = "NetworkReceiver"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == ConnectivityManager.CONNECTIVITY_ACTION){


        }
    }
}
@file:Suppress("DEPRECATION")

package com.minjem.dumi.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData


class InternetConnection(private val context: Context): LiveData<Boolean>() {

    private var connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private lateinit var  networkCallBack: ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()
        updateConnection()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->{
                connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())
            }
            else -> {
                context.registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            }
        }

    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())
            }
        } else {
            context.unregisterReceiver(networkReceiver)
        }

    }

    private fun  connectivityManagerCallback(): ConnectivityManager.NetworkCallback{
        networkCallBack = object : ConnectivityManager.NetworkCallback(){
            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(false)
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(true)
            }
        }
        return networkCallBack
    }

    private val networkReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()
        }
    }

    private fun updateConnection(){
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }



    /*fun onReceive(context: Context?, intent: Intent?) {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected){
        } else {
            Log.d("Internet ConnectionHelper", "onReceive: $isConnected")
            val i = Intent(context, AlertDialogActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("con", isConnected)
            context.startActivity(i)
        }
        Toast.makeText(context, if (isConnected) "Internet" else "No connection", Toast.LENGTH_LONG).show()
    }*/
}
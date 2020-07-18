package com.minjem.dumi.util

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.annotation.RequiresApi
import com.minjem.dumi.R
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object ConnectionHelper{

    fun showAlert(context: Context){
        val ctw = ContextThemeWrapper( context, R.style.Theme_AppCompat_Dialog_Alert)
        val alertDialog = AlertDialog.Builder(ctw)
        alertDialog.setTitle("Info")
        alertDialog.setMessage("Koneksi terputus, mohon cek koneksi data/wifi anda.")
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Tutup") { _, _ ->

        }
        alertDialog.show()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun hasNetworkAvailable(context: Context): Boolean {
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(service) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        Log.d(context.toString(), "hasNetworkAvailable: ${(network != null)}")
        return (network != null)
    }

    fun hasServerConnected(context: Context): Boolean {
        if (hasNetworkAvailable(context)) {
            try {
                val connection = URL("https://api.minjem.biz.id/api/").openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "Test")
                connection.setRequestProperty("ConnectionHelper", "close")
                connection.connectTimeout = 1000 //configurable
                connection.connect()
                Log.d(context.toString(), "hasServerConnected: ${(connection.responseCode == 200)}")
                return (connection.responseCode == 200)
            } catch (e: IOException) {
                Log.e(context.toString(), "Error checking server connection", e)
            }
        } else {
            Log.w(context.toString(), "Server is unavailable!")
        }
        Log.d(context.toString(), "hasServerConnected: false")
        return false
    }

}



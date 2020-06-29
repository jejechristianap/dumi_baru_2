package com.minjem.dumi.response

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.ContextThemeWrapper
import com.minjem.dumi.R


fun showAlert(context: Context){
    val ctw = ContextThemeWrapper( context, R.style.Theme_AppCompat_Dialog_Alert);
    val alertDialog = AlertDialog.Builder(ctw)
    alertDialog.setTitle("Info")
    alertDialog.setMessage("Internet tidak tersedia, mohon cek koneksi internet anda.");
    alertDialog.setIcon(android.R.drawable.ic_dialog_alert)
    alertDialog.setCancelable(false)
    alertDialog.setPositiveButton("Tutup") { dialog, _ ->
    }
    alertDialog.show()
}

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

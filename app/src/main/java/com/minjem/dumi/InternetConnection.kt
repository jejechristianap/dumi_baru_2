package com.minjem.dumi

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity


class InternetConnection: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected){
        } else {
            Log.d("Internet Connection", "onReceive: $isConnected")
            val i = Intent(context, AlertDialogActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("con", isConnected)
            context.startActivity(i)
        }
//        Toast.makeText(context, if (isConnected) "Internet" else "No connection", Toast.LENGTH_LONG).show()
    }
}
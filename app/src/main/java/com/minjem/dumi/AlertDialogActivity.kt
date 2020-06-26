package com.minjem.dumi

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import kotlin.system.exitProcess


class AlertDialogActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Log.d("Internet", "onCreate: ${intent.getBooleanExtra("con", false)}")
            val ctw = ContextThemeWrapper( this, R.style.Theme_AppCompat_Dialog_Alert);
            val alertDialog = AlertDialog.Builder(ctw)
            alertDialog.setTitle("Info")
            alertDialog.setMessage("Internet tidak tersedia, mohon cek koneksi internet anda.");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert)
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Tutup") { dialog, _ ->
                finish()
                startActivity(Intent(this, HalamanDepanActivity::class.java))
            }
            alertDialog.show()
        } catch (e: Exception) {
            Log.d("Internet Connection", "Show Dialog: " + e.message)
        }
    }
}
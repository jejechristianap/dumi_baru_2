package com.minjem.dumi

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.minjem.dumi.ecommerce.Helper.mToast
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.util.ConnectionHelper
import com.minjem.dumi.util.InternetConnection
import kotlinx.android.synthetic.main.activity_halaman_depan.*


class HalamanDepanActivity : AppCompatActivity() {
    private var daftarButton: Button? = null
    private var masukButton: Button? = null
    private val masukTv: TextView? = null
    lateinit var editor: SharedPreferences.Editor
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_halaman_depan)
//        conn()
//        checkCon()
        /*val i = Intent(this, InternetConnection::class.java)
        startService(i)*/
        Log.d("Update app", "onCreate: ${intent.getStringExtra("appVersion")}")
        if (intent.getStringExtra("appVersion") == "update"){

            forceUpdate()
        }
        if (checkPermssion()){
            Log.d("TAG", "onCreate: ${checkPermssion()}")
        } else {
            requestPermission()
        }
        val network = InternetConnection(applicationContext)
        network.observe(this, Observer { isConnected ->
            if (isConnected){
//                mToast(this, "Online")
//                checkUpdate()
                Log.d("Network", "onCreate: $isConnected")
            } else {
//                mToast(this, "Offline")
                ConnectionHelper.showAlert(this)
            }

        })

        SharedPrefManager.getInstance(applicationContext).logout()
        daftarButton = findViewById(R.id.asn_akftif_button_daftar)
        asn_akftif_button_daftar.setOnClickListener {

            startActivity(Intent(this@HalamanDepanActivity, DaftarActivity::class.java))
        }
        masukButton = findViewById(R.id.masuk_button)
        masuk_button.setOnClickListener { startActivity(Intent(this@HalamanDepanActivity, MasukActivity::class.java)) }
    }

    private fun forceUpdate(){
        val dialog = AlertDialog.Builder(this)
                .setTitle("New version available")
                .setCancelable(false)
                .setMessage("Please, update app to new version to continue reposting.")
                .setPositiveButton("Update") { _, _ ->
                    val appPackageName = packageName // getPackageName() from Context or Activity object
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                    } catch (anfe: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                    }
                }.setNegativeButton("No, thanks") { _, _ ->
                    finish()
                    moveTaskToBack(true)
                }.create()
        dialog.show()
    }

    private fun checkPermssion(): Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            return false
        }
        return true
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "onRequestPermissionsResult: $grantResults")
            } else {
                Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
            }
        }

    }

    private fun checkCon(){
        val loader = Thread{
            when{
                ConnectionHelper.hasServerConnected(this) -> runOnUiThread{
                    Log.d("HDA", "Loading api.minjem.biz.id")
                }
                else -> runOnUiThread {
                    mToast(this, "No Internet")
                    ConnectionHelper.showAlert(this)
                }
            }
        }
        loader.start()
    }


   /* private fun conn(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            val rec = InternetConnection()
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")

            registerReceiver(rec, intentFilter)
        }
    }*/

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().signOut()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        moveTaskToBack(true)
    }
}
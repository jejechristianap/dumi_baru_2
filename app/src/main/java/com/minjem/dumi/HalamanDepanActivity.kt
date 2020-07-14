package com.minjem.dumi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
        asn_akftif_button_daftar.setOnClickListener { startActivity(Intent(this@HalamanDepanActivity, DaftarActivity::class.java)) }
        masukButton = findViewById(R.id.masuk_button)
        masuk_button.setOnClickListener { startActivity(Intent(this@HalamanDepanActivity, MasukActivity::class.java)) }
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
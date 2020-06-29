package com.minjem.dumi

import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.response.InternetConnection
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

        SharedPrefManager.getInstance(applicationContext).logout()
        daftarButton = findViewById(R.id.asn_akftif_button_daftar)
        asn_akftif_button_daftar.setOnClickListener { startActivity(Intent(this@HalamanDepanActivity, DaftarActivity::class.java)) }
        masukButton = findViewById(R.id.masuk_button)
        masuk_button.setOnClickListener { startActivity(Intent(this@HalamanDepanActivity, MasukActivity::class.java)) }
    }

    private fun conn(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            val rec = InternetConnection()
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")

            registerReceiver(rec, intentFilter)
        }
    }

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
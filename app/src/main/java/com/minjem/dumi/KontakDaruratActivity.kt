package com.minjem.dumi

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.minjem.dumi.ecommerce.Helper.KOLOM
import kotlinx.android.synthetic.main.activity_kontak_darurat.*

class KontakDaruratActivity : AppCompatActivity() {
    private val statusHubungan = arrayOf("AYAH", "IBU", "KAKAK", "ADIK", "KERABAT")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kontak_darurat)
        initSpinner()

        backKontakDarurat.setOnClickListener { finish() }
        bLanjutKontakDarurat.setOnClickListener {
            if (etNamaKontakDarurat.text.toString().isEmpty()){
                etNamaKontakDarurat.error = KOLOM
                etNamaKontakDarurat.requestFocus()
                return@setOnClickListener
            }
            if (etNoTelpDarurat.text.toString().isEmpty()){
                etNoTelpDarurat.error = KOLOM
                etNoTelpDarurat.requestFocus()
                return@setOnClickListener
            }
            moveTo()
        }
    }

    private fun initSpinner(){
        val saHubungan = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, statusHubungan)
        sHubungan.adapter = saHubungan
    }

    private fun moveTo(){
        val sp = getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("kdHubungan", sHubungan.selectedItem.toString())
        editor.putString("kdNama", etNamaKontakDarurat.text.toString())
        editor.putString("kdTelp", etNoTelpDarurat.text.toString())
        editor.apply()
        startActivity(Intent(this, RekeningBankActivity::class.java))
    }
}
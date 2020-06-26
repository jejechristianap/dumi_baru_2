package com.minjem.dumi.ecommerce

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.minjem.dumi.R

class ECommerceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecommerce)

        if(savedInstanceState == null){
            val mFragment = intent.getStringExtra("fragment")
//            val token = intent.getStringExtra("token")

            when (mFragment) {
                "pulsa" -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_content,PulsaFragment()).commit()
                }
                "pln" -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_content, PlnFragment()).commit()
                }
                "flight" -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_content, FlightFragment()).commit()
                }
                "air" -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_content, PDAMFragment()).commit()
                }
            }

        }
    }
}
/*
        val pulsa = listOf("5K","10K","12K","20K","25K","30K","50K","80K","100K")
        val hargaIndosat = listOf("6400","11500","12500","20000","24800","29850","49000","78000","98000")
        val hargaTelkomsel = listOf("6500","11800","12700","20000","24900","29900","49500","78500","98000")
        val hargaXl = listOf("6200","11400","12600","20000","24700","29600","48500","78300","98000")
        val hargaThree = listOf("6200","11300","12500","20000","24500","29700","48700","78000","98000")

        pulsa.forEachIndexed { i, it ->
            val data = G_Pulsa()
            data.harga = hargaIndosat[i]
            data.operator = "Indosat"
            data.nominal = it
            data.kodeoperator = "1"
            list.add(data)
        }

        pulsa.forEachIndexed { i, it ->
            val data = G_Pulsa()
            data.harga = hargaTelkomsel[i]
            data.operator = "Telkomsel"
            data.kodeoperator = "2"
            data.nominal = it
            list.add(data)
        }

        pulsa.forEachIndexed { i, it ->
            val data = G_Pulsa()
            data.harga = hargaXl[i]
            data.operator = "XL"
            data.kodeoperator = "3"
            data.nominal = it
            list.add(data)
        }

        pulsa.forEachIndexed { i, it ->
            val data = G_Pulsa()
            data.harga = hargaThree[i]
            data.operator = "Three"
            data.kodeoperator = "4"
            data.nominal = it
            list.add(data)
        }*/
//adapterProduk.filterList(hasil)
//                        RV.adapter = adapterProduk
//                        adapterProduk.notifyDataSetChanged()
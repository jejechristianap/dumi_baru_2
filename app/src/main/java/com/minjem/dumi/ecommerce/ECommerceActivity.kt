package com.minjem.dumi.ecommerce

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.minjem.dumi.R
import com.minjem.dumi.akun.FullImageFragment

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
                "profile" ->{
                    supportFragmentManager.beginTransaction().replace(R.id.fl_content, FullImageFragment()).commit()
                }
            }

        }
    }
}

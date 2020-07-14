package com.minjem.dumi.ecommerce

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.minjem.dumi.R
import com.minjem.dumi.akun.FullImageFragment
import com.minjem.dumi.fragment.DigiSign

class ECommerceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecommerce)

        if(savedInstanceState == null){
            //            val token = intent.getStringExtra("token")

            when (intent.getStringExtra("fragment")) {
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
                "profile" -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fl_content, FullImageFragment()).commit()
                }
                "digisign" -> {
                    if (intent.getStringExtra("activity") ==  "kilat"){
                        Log.d("Ecommerce digisign", "onCreate: ${intent.getStringExtra("activity")}")
                        val bundle = Bundle()
                        bundle.putString("activity", "kilat")
                        val fragobj = DigiSign()
                        fragobj.arguments = bundle;
                        supportFragmentManager.beginTransaction().replace(R.id.fl_content, fragobj).commit()
                    } else {
                        Log.d("Ecommerce digisign", "onCreate: ${intent.getStringExtra("activity")}")
                        val bundle = Bundle()
                        bundle.putString("activity", "regular")
                        val fragobj = DigiSign()
                        fragobj.arguments = bundle;
                        supportFragmentManager.beginTransaction().replace(R.id.fl_content, fragobj).commit()
                    }

                }
            }

        }
    }
}

package com.minjem.dumi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.minjem.dumi.ecommerce.ECommerceActivity
import com.minjem.dumi.ecommerce.transaction.RiwayatView
import com.minjem.dumi.jenispinjaman.PinjamanKilatActivity
import com.minjem.dumi.jenispinjaman.PinjamanRegularActivity
import kotlinx.android.synthetic.main.activity_lihat_semua.*
import kotlinx.android.synthetic.main.activity_pinjmana_reguler.*

class SemuaEcommerceActivity : AppCompatActivity() {
    private var back: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_semua)
        toolbarSemua.title = ""
        toolbarSemua.setNavigationIcon(R.drawable.ic_back_white)
        toolbarSemua.setNavigationOnClickListener {
            finish()
        }

        /*back_semua_kategori.setOnClickListener {
            finish()
        }
*/
        initTouch()
    }

    private fun initTouch(){
        llIsiPulsa.setOnClickListener { goTo("pulsa") }
        llToken.setOnClickListener { goTo("pln") }
        llListrik.setOnClickListener { goTo("pln") }
        llInternet.setOnClickListener { goTo("na") }
        llOvo.setOnClickListener { goTo("na") }
        llGopay.setOnClickListener { goTo("na") }
        llEmoney.setOnClickListener { goTo("na") }
        llBrizi.setOnClickListener { goTo("na") }
        llTapcash.setOnClickListener { goTo("na") }
        llLinkaja.setOnClickListener { goTo("na") }
        llDana.setOnClickListener { goTo("na") }
        llGrabPay.setOnClickListener { goTo("na") }
        llVouchergame.setOnClickListener { goTo("na") }
        llPdam.setOnClickListener { goTo("air") }
        llBpjs.setOnClickListener { goTo("na") }
        llTelkom.setOnClickListener { goTo("na") }
        llGas.setOnClickListener { goTo("na") }
        llPascabayar.setOnClickListener { goTo("na") }
        llTvkabel.setOnClickListener { goTo("na") }
        llPesawat.setOnClickListener { goTo("na") }
        llKereta.setOnClickListener { goTo("na") }
        llHotel.setOnClickListener { goTo("na") }
        llPelni.setOnClickListener { goTo("na") }
        llFerry.setOnClickListener { goTo("na") }
        llBus.setOnClickListener { goTo("na") }
    }
    private fun goTo(item: String) {
        val intent: Intent
        when (item) {
            "pulsa" -> {
                intent = Intent(this, ECommerceActivity::class.java)
                intent.putExtra("fragment", "pulsa")
                startActivity(intent)
            }
            "pln" -> {
                intent = Intent(this, ECommerceActivity::class.java)
                intent.putExtra("fragment", "pln")
                startActivity(intent)
            }
            "air" -> {
                intent = Intent (this, ECommerceActivity::class.java)
                intent.putExtra("fragment", "air")
                startActivity(intent)
            }
            else -> Toast.makeText(this, "Tunggu update kami selanjutanya...", Toast.LENGTH_SHORT).show()
        }
    }
}
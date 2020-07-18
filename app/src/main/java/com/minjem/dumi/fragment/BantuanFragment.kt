package com.minjem.dumi.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.minjem.dumi.R
import kotlinx.android.synthetic.main.activity_pusat_bantuan.view.*

class BantuanFragment : Fragment() {
    lateinit var v: View
    lateinit var c: Context
    private var satu = false
    private var dua = false
    private var tiga = false
    private var empat = false
    private var lima = false
    private val TAG: String = BantuanFragment::class.java.simpleName
    private lateinit var rocketAnimation: AnimationDrawable


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.activity_pusat_bantuan, container, false)
        c = this.activity!!
        /*v.wvTtd.webViewClient = WebViewClient()
        v.wvTtd.settings.javaScriptEna-bled = true
        v.wvTtd.loadUrl("https://wv.tandatanganku.com/activationpage.html?act=FXvJGcLQWMzd1tNtAyoc4XRUT0h9EAsTqliRiBzel97uHbMRxXQLpIk7NCQNsoLMqp6yxKEYSAkFnlNsyOvUQUymmnb7mMg3E3SKUgufZTLHW%2BY1TYoqaJuTklB9o1TxkisxBe8RoYhjxg%2BtdQr5Sw%3D%3D")

*/
        initView()
        return v
    }

    private fun initView(){
        /*val rocketImage = v.findViewById<ImageView>(R.id.arrow1).apply {
            setBackgroundResource(R.drawable.animation)
            rocketAnimation = background as AnimationDrawable
        }*/

        Glide.with(c).load(R.drawable.ic_email).override(200, 200).into(v.ivEmail)
        Glide.with(c).load(R.drawable.ic_phone).override(200, 200).into(v.ivTelepon)
        Glide.with(c).load(R.drawable.ic_wa).override(200, 200).into(v.ivWa)
        Glide.with(c).load(R.drawable.ic_clock).override(200, 200).into(v.ivJam)
        Glide.with(c).load(R.drawable.ic_placeholder).override(200, 200).into(v.ivLokasi)

        v.pilihan_satu_ll.setOnClickListener {
            Log.d(TAG, "initView: pilihan satu: $satu")


            if (satu){
                v.deskripsi_satu_ll.visibility = View.GONE
                v.arrow1.setImageResource(R.drawable.ic_arrow_down24dp)
                satu = false
            } else {
                v.arrow1.setImageResource(R.drawable.ic_keyboard_arrow_up)
                v.deskripsi_satu_ll.visibility = View.VISIBLE
                satu = true
            }

        }
        v.pilihan_dua_ll.setOnClickListener {
            Log.d(TAG, "initView: pilihan dua: $dua")
            if (dua){
                v.deskripsi_dua_ll.visibility = View.GONE
                v.arrow2.setImageResource(R.drawable.ic_arrow_down24dp)
                dua = false
            } else {
                v.arrow2.setImageResource(R.drawable.ic_keyboard_arrow_up)
                v.deskripsi_dua_ll.visibility = View.VISIBLE
                dua = true
            }
        }
        v.pilihan_tiga_ll.setOnClickListener {
            Log.d(TAG, "initView: pilihan tiga: $tiga")
            if (tiga){
                v.deskripsi_tiga_ll.visibility = View.GONE
                v.arrow3.setImageResource(R.drawable.ic_arrow_down24dp)
                tiga = false
            } else {
                v.arrow3.setImageResource(R.drawable.ic_keyboard_arrow_up)
                v.deskripsi_tiga_ll.visibility = View.VISIBLE
                tiga = true
            }
        }

        v.pilihan_empat_ll.setOnClickListener {
            Log.d(TAG, "initView: pilihan empat: $empat")
            if (empat){
                v.deskripsi_empat_ll.visibility = View.GONE
                v.arrow4.setImageResource(R.drawable.ic_arrow_down24dp)
                empat = false
            } else {
                v.arrow4.setImageResource(R.drawable.ic_keyboard_arrow_up)
                v.deskripsi_empat_ll.visibility = View.VISIBLE
                empat = true
            }
        }
        v.pilihan_lima_ll.setOnClickListener {
            Log.d(TAG, "initView: pilihan lima: $lima")
            if (lima){
                v.deskripsi_lima_ll.visibility = View.GONE
                v.arrow5.setImageResource(R.drawable.ic_arrow_down24dp)
                lima = false
            } else {
                v.deskripsi_lima_ll.visibility = View.VISIBLE
                v.arrow5.setImageResource(R.drawable.ic_keyboard_arrow_up)
                lima = true
            }
        }
    }
}
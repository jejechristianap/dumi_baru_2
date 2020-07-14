package com.minjem.dumi.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.minjem.dumi.R
import kotlinx.android.synthetic.main.fragment_bantuan.view.*

class BantuanFragment : Fragment() {
    lateinit var v: View
    lateinit var c: Context
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.activity_pusat_bantuan, container, false)
        c = this.activity!!
        /*v.wvTtd.webViewClient = WebViewClient()
        v.wvTtd.settings.javaScriptEna-bled = true
        v.wvTtd.loadUrl("https://wv.tandatanganku.com/activationpage.html?act=FXvJGcLQWMzd1tNtAyoc4XRUT0h9EAsTqliRiBzel97uHbMRxXQLpIk7NCQNsoLMqp6yxKEYSAkFnlNsyOvUQUymmnb7mMg3E3SKUgufZTLHW%2BY1TYoqaJuTklB9o1TxkisxBe8RoYhjxg%2BtdQr5Sw%3D%3D")
*/
        return v
    }
}
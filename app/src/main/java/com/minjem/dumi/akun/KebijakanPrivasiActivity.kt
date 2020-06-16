package com.minjem.dumi.akun

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.minjem.dumi.R
import kotlinx.android.synthetic.main.activity_kebijakan_privasi.*

class KebijakanPrivasiActivity : AppCompatActivity() {
    private var backButton: ImageView? = null
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kebijakan_privasi)
        backButton = findViewById(R.id.back_syarat)
        back_syarat.setOnClickListener{ finish() }
        wvSyarat.webViewClient = WebViewClient()
        wvSyarat.settings.javaScriptEnabled = true
        wvSyarat.loadUrl("http://minjem.com/id/syarat-ketentuan/")
    }
}
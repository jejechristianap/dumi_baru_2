package com.minjem.dumi.akun

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.minjem.dumi.R
import com.minjem.dumi.model.SharedPrefManager
import kotlinx.android.synthetic.main.fragment_full_image.view.*
import java.io.File

class FullImageFragment : Fragment() {
    lateinit var v : View
    lateinit var c : Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = layoutInflater.inflate(R.layout.fragment_full_image,container,false)
        c = this.context!!

        Glide.with(c)
                .load(SharedPrefManager.getInstance(c).user.imageProfile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(v.ivProfile)
        return v
    }
}
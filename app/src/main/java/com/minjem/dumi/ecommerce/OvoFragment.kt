package com.minjem.dumi.ecommerce

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minjem.dumi.R

class OvoFragment : Fragment() {
    lateinit var mView : View
    lateinit var mContext : Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_ovo, container, false)
        mContext = this.context!!

        return mView
    }

}
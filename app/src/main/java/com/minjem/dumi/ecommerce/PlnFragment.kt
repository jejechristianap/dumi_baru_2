package com.minjem.dumi.ecommerce

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.minjem.dumi.R

class PlnFragment: Fragment() {
    lateinit var mView : View
    lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = layoutInflater.inflate(R.layout.fragment_pln,container,false)
        mContext = this.context!!

        return mView
    }
}
package com.minjem.dumi.ecommerce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minjem.dumi.R

class BottomSheetFragment() : BottomSheetDialogFragment() {
    private var fragmentView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.dialog_bottom_sheet_layout, container, false)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        /*textViewFacebook.setOnClickListener {
            Toast.makeText(context, "Facebook", Toast.LENGTH_SHORT).show()
        }
        textViewTwitter.setOnClickListener {
            Toast.makeText(context, "Twitter", Toast.LENGTH_SHORT).show()
        }
        textViewInstagram.setOnClickListener {
            Toast.makeText(context, "Instagram", Toast.LENGTH_SHORT).show()
        }
        textViewLinkedin.setOnClickListener {
            Toast.makeText(context, "Linkedin", Toast.LENGTH_SHORT).show()
        }*/
    }
}
package com.minjem.dumi.ecommerce.Helper

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import com.minjem.dumi.R
import java.text.DecimalFormat

const val USERNAME = "api_mmbc_fidac18"
const val PASSWORD = "Fi918_ahBmpl"

fun mDF(text : String) : String {

    val df = DecimalFormat("#,###")
    return df.format(text.toInt()).toString()
}

fun mProgress(progress : Dialog) : Dialog {
    progress.setContentView(R.layout.progress)
    progress.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
    progress.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    progress.setCancelable(false)
    progress.show()

    return progress
}

fun mToast(mContext : Context, text : String, duration : Int = Toast.LENGTH_SHORT) = Toast.makeText(mContext,text,duration).show()

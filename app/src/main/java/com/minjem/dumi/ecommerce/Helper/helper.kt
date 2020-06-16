package com.minjem.dumi.ecommerce.Helper

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.NumberFormat
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.minjem.dumi.R
import java.text.DecimalFormat
import java.util.*

const val USERNAME = "f!dac"
const val PASSWORD = "f!dac2020"
const val KOLOM = "Kolom ini tidak boleh kosong!"

@RequiresApi(Build.VERSION_CODES.N)
fun mDF(text : String) : String {

//    val local = Locale("in", "ID")
//    val formatUang = NumberFormat.getCurrencyInstance(local)

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

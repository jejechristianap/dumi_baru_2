package com.minjem.dumi

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.minjem.dumi.ecommerce.Helper.KOLOM
import kotlinx.android.synthetic.main.activity_data_keluarga.*
import java.text.SimpleDateFormat
import java.util.*

class DataKeluargaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_keluarga)
        backDataKeluarga.setOnClickListener { finish() }

        val myCalendar = Calendar.getInstance()
        val date = OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd-MM-yyyy" //In which you need put here
            val localID = Locale("in", "ID")
            val sdf = SimpleDateFormat(myFormat, localID)
            tglLahirPasanganTv.text = sdf.format(myCalendar.time)
        }

        tglLahirPasanganTv.setOnClickListener {
            // TODO Auto-generated method stub
            DatePickerDialog(this@DataKeluargaActivity, date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]).show()
        }


        bLanjutDataKeluarga.setOnClickListener {
            if (etNamaIbu.text.toString().isEmpty()){
                etNamaIbu.error = KOLOM
                etNamaIbu.requestFocus()
                return@setOnClickListener
            }
            spf()
        }
    }

/*    private fun updateLabel() {
        val myFormat = "dd-MM-yyyy" //In which you need put here
        val localID = Locale("in", "ID")
        val sdf = SimpleDateFormat(myFormat, localID)
        tglLahirPasanganTv.text = sdf.format(myCalendar.getTime())
    }*/

    private fun spf(){
        val sp = getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("namaIbu", etNamaIbu.text.toString())
        editor.putString("namaPasangan", etNamaPasangan.text.toString())
        editor.putString("noKtpPasangan", etNoKtpPasangan.text.toString())
        editor.putString("tglLahirPasangan", tglLahirPasanganTv.text.toString())
        editor.apply()
        startActivity(Intent(this, KontakDaruratActivity::class.java))
    }


}
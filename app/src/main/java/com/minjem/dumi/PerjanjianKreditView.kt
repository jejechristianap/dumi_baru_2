package com.minjem.dumi

import android.annotation.SuppressLint
import android.graphics.Typeface.BOLD
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.minjem.dumi.model.SharedPrefManager
import kotlinx.android.synthetic.main.activity_perjanjian_kredit_view.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class PerjanjianKreditView : AppCompatActivity() {
    lateinit var hariTgl: String
    lateinit var nama: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perjanjian_kredit_view)

        backPkIv.setOnClickListener{
            finish()
        }
         // mention the format you need
//        val sdf = SimpleDateFormat(myFormat, local).parse(hari)
        hariTgl = intent.getStringExtra("tanggal")!!.toString()

        nama = SharedPrefManager.getInstance(this).user.namaLengkap



        initTextView()
    }

    @SuppressLint("SetTextI18n")
    private fun initTextView(){
        var tanggal = hariTgl.substringBefore(",")
        var date = tanggal.replace("/", "-")

        Log.d("Hari", "onCreate: $tanggal")
        val stringNama = "2. <b>$nama</b>, Warga Negara Indonesia, beralamat di ${SharedPrefManager.getInstance(this).user.alamat}, " +
                "${SharedPrefManager.getInstance(this).user.noKtp} sebagai penerima fasilitas pinjaman konvensional yang " +
                "diberikan oleh Pemberi Pinjaman yang diwakili oleh Penyelenggara (untuk selanjutnya disebut sebagai <b>\"Penerima Pinjaman\"</b>)"

        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        val bunga = intent.getDoubleExtra("bunga", 0.0);
        val angsuran = intent.getDoubleExtra("angsuran", 0.0)

        pkHariTglTv.text = "Perjanjian Pinjaman Dumi PNS Sejahtera (\"Perjanjian\") " +
                "ini dibuat dan ditandatangani pada tanggal $date, yang dilaksanakan oleh dan antara:"
        pkNamaTv.text = Html.fromHtml(stringNama)
        pkJumlahPinjamanTv.text = "1. Jumlah Pinjaman: ${intent.getStringExtra("pinjaman")}"
        pkAngsuranTv.text = "3. Angsuran Pokok dan Bunga: ${format.format(angsuran-bunga)} dengan angsuran bunga per bulan ${format.format(bunga)}"
        pkTotalAngsuranTv.text = "4. Total Angsuran: ${format.format(angsuran)}"
        pkTujuanTv.text = "5. Tujuan Pinjaman: ${intent.getStringExtra("tujuan")}"
        pkJangkaWaktuTv.text = "6. Jangka Waktu: ${intent.getStringExtra("tenor")}"
        namaPenerimaTv.text = Html.fromHtml("<b>$nama</b>")


    }


}
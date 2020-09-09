package com.minjem.dumi.jenispinjaman

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import com.minjem.dumi.SuratPelengkapActivity
import com.minjem.dumi.R
import com.minjem.dumi.api.GetBungaInterface
import com.minjem.dumi.api.StatusPinjamanInterface
import com.minjem.dumi.ecommerce.Helper.mToast
import com.minjem.dumi.jenispinjaman.PinjamanKilatActivity.Companion.BIAYA_TRANSFER
import com.minjem.dumi.jenispinjaman.PinjamanKilatActivity.Companion.JUMLAH_BULAN_1_TAHUN
import com.minjem.dumi.jenispinjaman.PinjamanKilatActivity.Companion.TUJUAN_PINJAMAN
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.retrofit.RetrofitClient
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_pinjaman_ultimate.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PinjamanUltimateActivity : AppCompatActivity() {

    lateinit var localID: Locale
    lateinit var formatRp: NumberFormat
    private val baseStepPinjaman = 5000000
    private val basePinjamanUltimate = 50000000
    private var np = 1
    private var tujuan = ""
    private var getBunga = 0f
    private var bunga = 0f
    private var admin = 0f
    private var getAdmin = 0f
    private var getAsur12 = 0f
    private var getAsur24 = 0f
    private var getAsur36 = 0f
    private var angsuran = 0f
    private var asuransi = 0f
    private var sisa = 0f
    private var plafond = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pinjaman_ultimate)

        initView()
        getBungaValue()
        initTouch()
    }

    private fun initView(){
        toolbarUltimate.title = ""
        toolbarUltimate.setNavigationIcon(R.drawable.ic_back_white)

        etCurrencyUltimate.setDecimals(false)
        etCurrencyUltimate.setSeparator(".")
        etCurrencyUltimate.setText("51000000")

        npBulanUltimate.minValue = 1
        npBulanUltimate.maxValue = 60

        npTujuanUltimate.minValue = 0
        npTujuanUltimate.maxValue = TUJUAN_PINJAMAN.size - 1
        npTujuanUltimate.displayedValues = TUJUAN_PINJAMAN

        localID = Locale("in", "ID")
        formatRp = NumberFormat.getCurrencyInstance(localID)

        sbUltimate.max = 10
        numberPicker(np)
    }

    private fun initTouch(){
        toolbarUltimate.setNavigationOnClickListener {
            finish()
        }

        npBulanUltimate.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("NumberPicker", "initTouch: $picker, $oldVal, $newVal")
            np = newVal
            numberPicker(np)
        }

        npTujuanUltimate.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("NumberPicker Tujuan", "initTouch: $picker, $oldVal, ${TUJUAN_PINJAMAN[newVal]}")
            tujuan = TUJUAN_PINJAMAN[newVal]
        }

        etCurrencyUltimate.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("jumlah", "onTextChanged: $s, $start, $before, $count, ${etCurrencyUltimate.cleanIntValue}")
                numberPicker(np)
            }

        })

        sbUltimate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var progressValue = 0
            var x = 0
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progressValue = progress
                Log.d("Progress Value", "onProgressChanged: $baseStepPinjaman")
                Log.d("Progress Value", "onProgressChanged: $progressValue")
                x = if(progressValue > 0){
                    (progressValue * baseStepPinjaman) + basePinjamanUltimate
                } else {
                    basePinjamanUltimate + 1000000
                }
                Log.d("Progress Value", "onProgressChanged: $x")
                etCurrencyUltimate.setText(x.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                numberPicker(np)
                x = if(progressValue > 0){
                    (progressValue * baseStepPinjaman) + basePinjamanUltimate
                } else {
                    basePinjamanUltimate + 1000000
                }
                etCurrencyUltimate.setText(x.toString())
            }
        })

        bLanjutUltimate.setOnClickListener {
            val angs = tvAngsuranPerbulanUltimate.text.toString()
            if (TextUtils.isEmpty(angs)) {
                Toast.makeText(this, "Tentukan Lama Pembayaran", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            when {
                etCurrencyUltimate.cleanIntValue < 51000000 -> {
                    mToast(this, getString(R.string.minimum_pinjaman_rp_1_000_000))
                    Toasty.warning(this, "Pinjaman Ultimate minimal Rp51.000.0000", Toast.LENGTH_LONG, true)
                    return@setOnClickListener
                }
                etCurrencyUltimate.cleanIntValue > 100000000 -> {
                    Toasty.warning(this, "Pinjaman Ultimate maksimal Rp100.000.0000", Toast.LENGTH_LONG, true)
                    return@setOnClickListener
                }
            }
            Log.d("Jumlah pinjaman", "initTouch: ${etCurrencyUltimate.cleanDoubleValue}")
            pinjaman
        }

    }

    private fun numberPicker(number: Int){
        plafond = number
        val pokok = etCurrencyUltimate.cleanDoubleValue.toFloat() / plafond.toFloat()
        bunga = etCurrencyUltimate.cleanDoubleValue.toFloat() * getBunga / JUMLAH_BULAN_1_TAHUN
        angsuran = pokok + bunga
        admin = etCurrencyUltimate.cleanDoubleValue.toFloat() * getAdmin
        asuransi = etCurrencyUltimate.cleanDoubleValue.toFloat() * getAsur12
        val totalPengurangan = admin + asuransi + BIAYA_TRANSFER
        sisa = etCurrencyUltimate.cleanDoubleValue.toFloat() - totalPengurangan
        Log.d("Asuransi", "numberPicker: $getAsur12")

        tvAngsuranPerbulanUltimate.text = formatRp.format(angsuran.toDouble())
        tvAdminUltimate.text = formatRp.format(admin.toDouble())
        tvAsuransiUltimate.text = formatRp.format(asuransi.toDouble())
        tvTrfUltimate.text = formatRp.format(BIAYA_TRANSFER.toDouble())
        tvJumlahTerimaUltimate.text = formatRp.format(sisa.toDouble())
    }

    private fun getBungaValue(){
        RetrofitClient.client.create(GetBungaInterface::class.java)
                .bunga.enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("Get Bunga", "onFailure: ${t.message.toString()}")
                        mToast(this@PinjamanUltimateActivity, "Koneksi server terputus!")
                        getBungaValue()
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful){
                            val jsonObject = JSONObject(response.body()!!.string())
                            if (jsonObject.getBoolean("status")){
                                val jsonArray = JSONArray(jsonObject.getString("data"))
                                for (i in 0 until jsonArray.length()){
                                    if (jsonArray.getJSONObject(i).getInt("id_bunga") == 1 &&
                                            SharedPrefManager.getInstance(this@PinjamanUltimateActivity).user.inskerKerja.contains("Badan Kepegawaian Negara")){
                                        admin_asn_ultimate.text = "Biaya Administrasi 1%"
                                        asuransi_asn_ultimate.text = "Biaya Asuransi 1%"
                                        getBunga = jsonArray.getJSONObject(i).getDouble("bunga").toFloat()
                                        getAdmin = jsonArray.getJSONObject(i).getDouble("biaya_admin").toFloat()
                                        getAsur12 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_12").toFloat()
                                        getAsur24 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_24").toFloat()
                                        getAsur36 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_36").toFloat()
                                    } else if (jsonArray.getJSONObject(i).getInt("id_bunga") == 2 && !SharedPrefManager.getInstance(this@PinjamanUltimateActivity).user.inskerKerja.contains("Badan Kepegawaian Negara")){
                                        admin_asn_ultimate.text = "Biaya Administrasi 2%"
                                        asuransi_asn_ultimate.text = "Biaya Asuransi 2%"
                                        getBunga = jsonArray.getJSONObject(i).getDouble("bunga").toFloat()
                                        getAdmin = jsonArray.getJSONObject(i).getDouble("biaya_admin").toFloat()
                                        getAsur12 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_12").toFloat()
                                        getAsur24 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_24").toFloat()
                                        getAsur36 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_36").toFloat()
                                    }
                                }

                            } else {
                                mToast(this@PinjamanUltimateActivity, "Gagal mengambil data!")
                            }

                        } else {
                            mToast(this@PinjamanUltimateActivity, response.message().toString())
                        }
                    }

                })
    }

    private val pinjaman: Unit
    get(){
        val status = RetrofitClient.client.create(StatusPinjamanInterface::class.java)
                .getPinjaman(SharedPrefManager.getInstance(this).user.nip)
                .enqueue(object : Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("Get Bunga", "onFailure: ${t.message.toString()}")
                        mToast(this@PinjamanUltimateActivity, "Koneksi server terputus!")
                        pinjaman
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful){
                            val jsonObject = JSONObject(response.body()!!.string())
                            if (jsonObject.getBoolean("status")){
                                val jsonArray = JSONArray(jsonObject.getString("data"))
                                val statusPinjaman = ArrayList<Int>()
                                if (!jsonArray.isNull(0)){
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
                                        statusPinjaman.add(jsonObject.getInt("status"))
                                    }
                                    when {
                                        statusPinjaman.contains(1) -> {
                                            Toast.makeText(this@PinjamanUltimateActivity, "Anda sudah mengajukan pinjaman. Mohon menunggu info dari kami.", Toast.LENGTH_SHORT).show()
                                        }
                                        statusPinjaman.contains(2) -> {
                                            Toast.makeText(this@PinjamanUltimateActivity, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT).show()
                                        }
                                        statusPinjaman.contains(4) -> {
                                            Toast.makeText(this@PinjamanUltimateActivity, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT).show()
                                        }
                                        statusPinjaman.contains(5) -> {
                                            Toast.makeText(this@PinjamanUltimateActivity, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT).show()
                                        }
                                        else -> {
                                            ajukanPinjaman()
                                        }
                                    }
                                } else {
                                    ajukanPinjaman()
                                }
                            } else {
                                mToast(this@PinjamanUltimateActivity, "Gagal mengambil data")
                            }
                        } else {
                            mToast(this@PinjamanUltimateActivity, "Koneksi server terputus")
                        }
                    }

                })

    }

    @SuppressLint("SimpleDateFormat")
    private fun ajukanPinjaman(){
        val pref = applicationContext.getSharedPreferences("ajukanPinjaman", 0) // 0 - for private mode
        val editor = pref.edit()
        val dateFormat = SimpleDateFormat(PinjamanKilatActivity.DATE_FORMAT_2)
        val c = Calendar.getInstance()
        val today = c.time
        val tglPinjam = dateFormat.format(today)
        c.add(Calendar.MONTH, plafond)
        val dueDate = c.time
        val tglAkhirPinjam = dateFormat.format(dueDate)
        editor.putString("nip", SharedPrefManager.getInstance(this).user.nip)
        editor.putFloat("pinjaman", etCurrencyUltimate.cleanIntValue.toFloat())
        editor.putInt("lamaPinjaman", plafond)
        editor.putFloat("bunga", bunga)
        editor.putFloat("admin", admin)
        editor.putFloat("angsuran", angsuran)
        editor.putFloat("diterima", sisa)
        editor.putString("tujuan", tujuan)
        editor.putString("tglMulai", tglPinjam)
        editor.putString("tglAkhir", tglAkhirPinjam)
        editor.putFloat("asuransi", asuransi)
        editor.putString("activity", "reguler")
        editor.apply()
        Log.d("AJUKAN", "ajukanPinjaman: ${etCurrencyUltimate.cleanIntValue.toFloat()}")
        /*digisignPrestImp.data(SharedPrefManager.getInstance(this).user.nip
                ,SharedPrefManager.getInstance(this).user.email)*/
        val i = Intent(this, SuratPelengkapActivity::class.java)
        i.putExtra("activity", "reguler")
        startActivity(i)
    }

}
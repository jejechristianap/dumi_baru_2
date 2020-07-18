package com.minjem.dumi.jenispinjaman

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.minjem.dumi.MainActivity
import com.minjem.dumi.PelengkapanRegularActivity
import com.minjem.dumi.PersetujuanActivity
import com.minjem.dumi.R
import com.minjem.dumi.api.GetBungaInterface
import com.minjem.dumi.api.StatusPinjamanInterface
import com.minjem.dumi.ecommerce.ECommerceActivity
import com.minjem.dumi.ecommerce.Helper.mToast
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.presenter.DigisignPrestImp
import com.minjem.dumi.response.RDigisign
import com.minjem.dumi.retrofit.RetrofitClient
import com.minjem.dumi.view.DigisignView
import kotlinx.android.synthetic.main.activity_pinjaman_kilat.*
import kotlinx.android.synthetic.main.activity_pinjmana_reguler.*
import me.abhinay.input.CurrencyEditText
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PinjamanRegularActivity : AppCompatActivity(), DigisignView {
    lateinit var backRegular: ImageView
    lateinit var jumlahRegular: CurrencyEditText
    lateinit var seekbarRegular: SeekBar
    private var pinjamanUang = 0
    lateinit var bulan12: Button
    lateinit var bulan18: Button
    lateinit var bulan24: Button
    lateinit var bulan30: Button
    lateinit var bulan36: Button
    lateinit var angsuranRegularTv: TextView
    lateinit var biayaAdminRegularTv: TextView
    lateinit var asuransiDiatas12: TextView
    lateinit var biayaAsuransiRegularTv: TextView
    lateinit var biayaTransferRegularTv: TextView
    lateinit var jumlahTerimaRegularTv: TextView
    private val PEMBAYARAN_12_BULAN = 12
    private val PEMBAYARAN_18_BULAN = 18
    private val PEMBAYARAN_24_BULAN = 24
    private val PEMBAYARAN_30_BULAN = 30
    private val PEMBAYARAN_36_BULAN = 36
    private val JUMLAH_BULAN_1_TAHUN = 12
    private val JUMLAH_PINJAMAN_DEFAULT = 16000000
    private val BIAYA_ADMIN = 0.01f
    private val BIAYA_ASURANSI = 0.01f
    private val BIAYA_ASURANSI_SD_24 = 0.016f
    private val BIAYA_ASURANSI_SD_36 = 0.021f
    private val BIAYA_TRANSFER = 6500f
    private val BUNGA_PERBULAN = 0.099f
    private var getBunga = 0f
    private var getAdmin = 0f
    private var getAsur12 = 0f
    private var getAsur24 = 0f
    private var getAsur36 = 0f
    lateinit var localID: Locale
    lateinit var formatRp: NumberFormat
    lateinit var tujuanSpinner: Spinner
    lateinit var tujuanAdapter: ArrayAdapter<CharSequence>
    private val tujuanPinjaman = arrayOf("Renovasi Rumah", "Biaya Pendidikan", "Biaya Rumah Sakit",
            "Jalan-jalan", "Biaya Pernikahan", "Modal Usaha", "Lainnya")
    lateinit var lanjutButton: Button
    var bunga = 0f
    var admin = 0f
    var angsuran = 0f
    var asuransi = 0f
    var sisa = 0f
    var plafond = 0
    lateinit var editor: SharedPreferences.Editor
    lateinit var pref: SharedPreferences
    lateinit var adminTv: TextView
    lateinit var asurTv: TextView
    private var getAsuransi = ""
    private var getAdministrasi = ""
    private var bkn = true
    private var rb = ""
    lateinit var digisignPrestImp : DigisignPrestImp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pinjmana_reguler)

        initView()
        getBunga()
        initTouch()
    }

    private fun initView(){
        /*SharedPref ajukanPinjaman*/
        digisignPrestImp = DigisignPrestImp(this)
        pref = applicationContext.getSharedPreferences("ajukanPinjaman", 0) // 0 - for private mode
        editor = pref.edit()
        plafond = 0
        bunga = 0f
        admin = 0f
        angsuran = 0f
        asuransi = 0f
        sisa = 0f

        getBunga = 0f
        getAdmin = 0f
        getAsur12 = 0f
        getAsur24 = 0f
        getAsur36 = 0f
        backRegular = findViewById(R.id.back_regular)
        /*Tujuan Pinjaman*/
        tujuanSpinner = findViewById(R.id.tujuan_pinjaman_reguler_spinner)
        tujuanAdapter = ArrayAdapter(this@PinjamanRegularActivity, R.layout.spinner_text, tujuanPinjaman)
        tujuanAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
        tujuanSpinner.adapter = tujuanAdapter
        localID = Locale("in", "ID")
        formatRp = NumberFormat.getCurrencyInstance(localID)
        angsuranRegularTv = findViewById(R.id.angsuran_perbulan_regular)
        biayaAdminRegularTv = findViewById(R.id.biaya_administrasi_regular)
        biayaAsuransiRegularTv = findViewById(R.id.biaya_asuransi_regular)
        biayaTransferRegularTv = findViewById(R.id.biaya_transfer_antar_bank_regular)
        jumlahTerimaRegularTv = findViewById(R.id.jumlah_yang_diterima_regular)
        adminTv = findViewById(R.id.tvAdminReg)
        asurTv = findViewById(R.id.tvAsurReg)

        /*Jumlah yang akan dipinjam*/
        pinjamanUang = JUMLAH_PINJAMAN_DEFAULT
        jumlahRegular = findViewById(R.id.etCurrencyRegular)
        jumlahRegular.setDecimals(false)
        jumlahRegular.setSeparator(".")
        jumlahRegular.setText("16000000")

        seekbarRegular = findViewById(R.id.seekbar_regular)
        lanjutButton = findViewById(R.id.lanjut_button_reguler)
    }

    private fun initTouch(){
        backRegular.setOnClickListener{
            val intent = Intent(this@PinjamanRegularActivity, MainActivity::class.java)
            startActivity(intent)
        }

        jumlahRegular.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Pinjaman Regular", "onTextChanged: $count")
                Log.d("Pinjaman Regular", "onTextChanged: ${jumlahRegular.cleanIntValue}")
                when {
                    jumlahRegular.cleanIntValue < 16000000 -> {
                        tvMinReg.visibility = View.VISIBLE
                        tvMaxReg.visibility = View.GONE
                    }
                    jumlahRegular.cleanIntValue > 50000000 -> {
                        tvMinReg.visibility = View.GONE
                        tvMaxReg.visibility = View.VISIBLE
                    }
                    else -> {
                        tvMinReg.visibility = View.GONE
                        tvMaxReg.visibility = View.GONE
                    }
                }
                onCheckRadio()
            }
        })

        seekbarRegular.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressValue = progress
                angsuranRegularTv.text = ""
                biayaAdminRegularTv.text = ""
                biayaAsuransiRegularTv.text = ""
                biayaTransferRegularTv.text = ""
                when (progressValue) {
                    0 -> {
                        pinjamanUang = JUMLAH_PINJAMAN_DEFAULT
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    1 -> {
                        pinjamanUang = 18000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    2 -> {
                        pinjamanUang = 20000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    3 -> {
                        pinjamanUang = 22000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    4 -> {
                        pinjamanUang = 24000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    5 -> {
                        pinjamanUang = 26000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    6 -> {
                        pinjamanUang = 28000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    7 -> {
                        pinjamanUang = 30000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    8 -> {
                        pinjamanUang = 32000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    9 -> {
                        pinjamanUang = 34000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    10 -> {
                        pinjamanUang = 36000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    11 -> {
                        pinjamanUang = 38000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    12 -> {
                        pinjamanUang = 40000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    13 -> {
                        pinjamanUang = 42000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    14 -> {
                        pinjamanUang = 44000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    15 -> {
                        pinjamanUang = 46000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    16 -> {
                        pinjamanUang = 48000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    17 -> {
                        pinjamanUang = 50000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                onCheckRadio()
                when (progressValue) {
                    0 -> {
                        pinjamanUang = 16000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    1 -> {
                        pinjamanUang = 18000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    2 -> {
                        pinjamanUang = 20000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    3 -> {
                        pinjamanUang = 22000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    4 -> {
                        pinjamanUang = 24000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    5 -> {
                        pinjamanUang = 26000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    6 -> {
                        pinjamanUang = 28000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    7 -> {
                        pinjamanUang = 30000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    8 -> {
                        pinjamanUang = 32000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    9 -> {
                        pinjamanUang = 34000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    10 -> {
                        pinjamanUang = 36000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    11 -> {
                        pinjamanUang = 38000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    12 -> {
                        pinjamanUang = 40000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    13 -> {
                        pinjamanUang = 42000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    14 -> {
                        pinjamanUang = 44000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    15 -> {
                        pinjamanUang = 46000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    16 -> {
                        pinjamanUang = 48000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                    17 -> {
                        pinjamanUang = 50000000
                        jumlahRegular.setText(pinjamanUang.toString())
                    }
                }
            }
        })

        rgRegular.setOnCheckedChangeListener{ _, checkId ->
            val radio: RadioButton = findViewById(checkId)
            rb = radio.text.toString()
//            Log.d("RadioButton", if(rbRegular12.isChecked) "radio 3" else "radio lain")
            onCheckRadio()
        }

        lanjutButton.setOnClickListener{
            val angs = angsuranRegularTv.text.toString()
            if (TextUtils.isEmpty(angs)) {
                Toast.makeText(this, "Tentukan Lama Pembayaran", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when {
                jumlahRegular.cleanIntValue < 16000000 -> {
                    mToast(this, getString(R.string.minimum_pinjaman_rp_16_000_000))
                    return@setOnClickListener
                }
                jumlahRegular.cleanIntValue > 50000000 -> {
                    mToast(this, getString(R.string.maximum_pinjaman_rp_50_000_000))
                    return@setOnClickListener
                }
                else -> {
                    tvMinReg.visibility = View.GONE
                    tvMaxReg.visibility = View.GONE
                }
            }

            pinjaman
        }
    }

    private fun onCheckRadio(){
        when(rb){
            "12" -> {
                val adm = "Biaya Administrasi ${getAdmin*100}%"
                val asr = "Biaya Asuransi ${getAsur12*100}%"
                adminTv.text = adm
                asurTv.text = asr
                /*Loan Calculation*/plafond = PEMBAYARAN_12_BULAN
                val pokok = etCurrencyRegular.cleanIntValue.toFloat() / plafond.toFloat()
                bunga = etCurrencyRegular.cleanIntValue.toFloat() * getBunga / JUMLAH_BULAN_1_TAHUN
                angsuran = pokok + bunga
                admin = etCurrencyRegular.cleanIntValue.toFloat() * getAdmin
                asuransi = etCurrencyRegular.cleanIntValue.toFloat() * getAsur12
                val totalPengurangan = admin + asuransi + BIAYA_TRANSFER
                sisa = etCurrencyRegular.cleanIntValue.toFloat() - totalPengurangan
                angsuranRegularTv.text = formatRp.format(angsuran.toDouble())
                biayaAdminRegularTv.text = formatRp.format(admin.toDouble())
                biayaAsuransiRegularTv.text = formatRp.format(asuransi.toDouble())
                biayaTransferRegularTv.text = formatRp.format(BIAYA_TRANSFER.toDouble())
                jumlahTerimaRegularTv.text = formatRp.format(sisa.toDouble())
            }
            "18" -> {
                val adm = "Biaya Administrasi ${getAdmin*100}%"
                val asr = "Biaya Asuransi ${getAsur24*100}%"
                adminTv.text = adm
                asurTv.text = asr

                /*Loan Calculation*/
                plafond = PEMBAYARAN_18_BULAN
                val pokok = etCurrencyRegular.cleanIntValue.toFloat() / plafond.toFloat()
                bunga = etCurrencyRegular.cleanIntValue.toFloat() * getBunga / JUMLAH_BULAN_1_TAHUN
                angsuran = pokok + bunga
                admin = etCurrencyRegular.cleanIntValue.toFloat() * getAdmin
                asuransi = etCurrencyRegular.cleanIntValue.toFloat() * getAsur24
                val totalPengurangan = admin + asuransi + BIAYA_TRANSFER
                sisa = etCurrencyRegular.cleanIntValue.toFloat() - totalPengurangan
                angsuranRegularTv.text = formatRp.format(angsuran.toDouble())
                biayaAdminRegularTv.text = formatRp.format(admin.toDouble())
                biayaAsuransiRegularTv.text = formatRp.format(asuransi.toDouble())
                biayaTransferRegularTv.text = formatRp.format(BIAYA_TRANSFER.toDouble())
                jumlahTerimaRegularTv.text = formatRp.format(sisa.toDouble())
            }
            "24" -> {
                val adm = "Biaya Administrasi ${getAdmin*100}%"
                val asr = "Biaya Asuransi ${getAsur24*100}%"
                adminTv.text = adm
                asurTv.text = asr

                /*Loan Calculation 24*/plafond = PEMBAYARAN_24_BULAN
                val pokok = etCurrencyRegular.cleanIntValue.toFloat() / plafond.toFloat()
                bunga = etCurrencyRegular.cleanIntValue.toFloat() * getBunga / JUMLAH_BULAN_1_TAHUN
                angsuran = pokok + bunga
                admin = etCurrencyRegular.cleanIntValue.toFloat() * getAdmin
                asuransi = etCurrencyRegular.cleanIntValue.toFloat() * getAsur24
                val totalPengurangan = admin + asuransi + BIAYA_TRANSFER
                sisa = etCurrencyRegular.cleanIntValue.toFloat() - totalPengurangan
                angsuranRegularTv.text = formatRp.format(angsuran.toDouble())
                biayaAdminRegularTv.text = formatRp.format(admin.toDouble())
                biayaAsuransiRegularTv.text = formatRp.format(asuransi.toDouble())
                biayaTransferRegularTv.text = formatRp.format(BIAYA_TRANSFER.toDouble())
                jumlahTerimaRegularTv.text = formatRp.format(sisa.toDouble())
            }
            "30" -> {
                val adm = "Biaya Administrasi ${getAdmin*100}%"
                val asr = "Biaya Asuransi ${getAsur36*100}%"
                adminTv.text = adm
                asurTv.text = asr


                /*Loan Calculation 30*/
                plafond = PEMBAYARAN_30_BULAN
                val pokok = etCurrencyRegular.cleanIntValue.toFloat() / plafond.toFloat()
                bunga = etCurrencyRegular.cleanIntValue.toFloat() * getBunga / JUMLAH_BULAN_1_TAHUN
                angsuran = pokok + bunga
                admin = etCurrencyRegular.cleanIntValue.toFloat() * getAdmin
                asuransi = etCurrencyRegular.cleanIntValue.toFloat() * getAsur36
                val totalPengurangan = admin + asuransi + BIAYA_TRANSFER
                sisa = etCurrencyRegular.cleanIntValue.toFloat() - totalPengurangan
                angsuranRegularTv.text = formatRp.format(angsuran.toDouble())
                biayaAdminRegularTv.text = formatRp.format(admin.toDouble())
                biayaAsuransiRegularTv.text = formatRp.format(asuransi.toDouble())
                biayaTransferRegularTv.text = formatRp.format(BIAYA_TRANSFER.toDouble())
                jumlahTerimaRegularTv.text = formatRp.format(sisa.toDouble())
            }
            "36" -> {
                val adm = "Biaya Administrasi ${getAdmin*100}%"
                val asr = "Biaya Asuransi ${getAsur36*100}%"
                adminTv.text = adm
                asurTv.text = asr


                /*Loan Calculation 36*/
                plafond = PEMBAYARAN_36_BULAN
                val pokok = etCurrencyRegular.cleanIntValue.toFloat() / plafond.toFloat()
                bunga = etCurrencyRegular.cleanIntValue.toFloat() * getBunga / JUMLAH_BULAN_1_TAHUN
                angsuran = pokok + bunga
                admin = etCurrencyRegular.cleanIntValue.toFloat() * getAdmin
                asuransi = etCurrencyRegular.cleanIntValue.toFloat() * getAsur36
                val totalPengurangan = admin + asuransi + BIAYA_TRANSFER
                sisa = etCurrencyRegular.cleanIntValue.toFloat() - totalPengurangan
                angsuranRegularTv.text = formatRp.format(angsuran.toDouble())
                biayaAdminRegularTv.text = formatRp.format(admin.toDouble())
                biayaAsuransiRegularTv.text = formatRp.format(asuransi.toDouble())
                biayaTransferRegularTv.text = formatRp.format(BIAYA_TRANSFER.toDouble())
                jumlahTerimaRegularTv.text = formatRp.format(sisa.toDouble())
            }
        }
    }


    private fun getBunga() {
        val pDialog = ProgressDialog(this)
        pDialog.setMessage("Memuat Data...")
        pDialog.show()
        val pref = SharedPrefManager.getInstance(applicationContext).user
        val insker = pref.inskerKerja
        val bunga = RetrofitClient.getClient().create(GetBungaInterface::class.java)
        val call = bunga.bunga
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                pDialog.dismiss()
                try {
                    val obj = JSONObject(response.body()!!.string())
                    val status = obj.getBoolean("status")
                    if (status) {
                        val data = obj.getString("data")
                        val bungaObj = JSONArray(data)
                        for (i in 0 until bungaObj.length()) {
                            val bung = bungaObj.getJSONObject(i)
                            val id = bung.getInt("id_bunga")
                            if (id == 1 && insker.contains("Badan Kepegawaian Negara")) {
                                bkn = true

                                val bunga = bung.getDouble("bunga")
                                val biayaAdmin = bung.getDouble("biaya_admin")
                                val biayaAsuransi12 = bung.getDouble("biaya_asuransi_12")
                                val biayaAsuransi24 = bung.getDouble("biaya_asuransi_24")
                                val biayaAsuransi36 = bung.getDouble("biaya_asuransi_36")
                                getBunga = bunga.toFloat()
                                getAdmin = biayaAdmin.toFloat()
                                getAsur12 = biayaAsuransi12.toFloat()
                                getAsur24 = biayaAsuransi24.toFloat()
                                getAsur36 = biayaAsuransi36.toFloat()

                                Log.d("Biaya", """
                                        onResponse:
                                        Bunga: $getBunga
                                        Admin: $getAdmin
                                        Asur12: $getAsur12
                                        Asur24: $getAsur24
                                        Asur36: $getAsur36
                                        """.trimIndent())
                            } else if (id == 2 && !insker.contains("Badan Kepegawaian Negara")) {
//                                Toast.makeText(PinjamanKilatActivity.this, "Non BKN", Toast.LENGTH_SHORT).show();
                                bkn = false
                                val bunga = bung.getDouble("bunga")
                                val biayaAdmin = bung.getDouble("biaya_admin")
                                val biayaAsuransi12 = bung.getDouble("biaya_asuransi_12")
                                val biayaAsuransi24 = bung.getDouble("biaya_asuransi_24")
                                val biayaAsuransi36 = bung.getDouble("biaya_asuransi_36")
                                getBunga = bunga.toFloat()
                                getAdmin = biayaAdmin.toFloat()
                                getAsur12 = biayaAsuransi12.toFloat()
                                getAsur24 = biayaAsuransi24.toFloat()
                                getAsur36 = biayaAsuransi36.toFloat()
                            }
                        }

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                pDialog.dismiss()
            }
        })
    }

    private val pinjaman: Unit
        get() {
            val pref = SharedPrefManager.getInstance(applicationContext).user
            val nip = pref.nip
            val status = RetrofitClient.getClient().create(StatusPinjamanInterface::class.java)
            val call = status.getPinjaman(nip)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    try {
                        val obj = JSONObject(response.body()!!.string())
                        val cek = obj.getBoolean("status")
                        if (cek) {
                            val data = obj.getString("data")
                            val jsonArray = JSONArray(data)
                            val statusPinjaman = ArrayList<Int>()
                            if (!jsonArray.isNull(0)) {
                                for (i in 0 until jsonArray.length()) {
                                    val jsonObject = jsonArray.getJSONObject(i)
                                    statusPinjaman.add(jsonObject.getInt("status"))
                                }
                                when {
                                    statusPinjaman.contains(1) -> {
                                        Toast.makeText(this@PinjamanRegularActivity, "Anda sudah mengajukan pinjaman. Mohon menunggu info dari kami.", Toast.LENGTH_SHORT).show()
                                        return
                                    }
                                    statusPinjaman.contains(2) -> {
                                        Toast.makeText(this@PinjamanRegularActivity, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT).show()
                                        return
                                    }
                                    statusPinjaman.contains(4) -> {
                                        Toast.makeText(this@PinjamanRegularActivity, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT).show()
                                        return
                                    }
                                    statusPinjaman.contains(5) -> {
                                        Toast.makeText(this@PinjamanRegularActivity, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT).show()
                                        return
                                    }
                                    else -> {
                                        ajukanPinjaman()
                                    }
                                }
                            } else {
                                ajukanPinjaman()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
            })
        }

    private fun ajukanPinjaman() {
        val user = SharedPrefManager.getInstance(applicationContext).user
        val nip = user.nip
        val tujuan = tujuanSpinner!!.selectedItem.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val c = Calendar.getInstance()
        val today = c.time
        val tglPinjam = dateFormat.format(today)
        c.add(Calendar.MONTH, plafond)
        val dueDate = c.time
        val tglAkhirPinjam = dateFormat.format(dueDate)
        Log.d("Pinjaman",
                """
                    nip: $nip
                    Pinjaman: $pinjamanUang
                    Plafond: $plafond
                    bunga: $BUNGA_PERBULAN
                    Bunga: $bunga
                    Admin: $admin
                    Angsuran: $angsuran
                    TrfBank: $BIAYA_TRANSFER
                    Tujuan: $tujuan
                    TglPinjam: $tglPinjam
                    AkhirTgl: $tglAkhirPinjam
                    Sisa: $sisa
                    asurans: $asuransi
                    """.trimIndent())
        editor.putString("nip", nip)
        editor.putFloat("pinjaman", pinjamanUang.toFloat())
        editor.putInt("lamaPinjaman", plafond)
        editor.putFloat("bunga", bunga)
        editor.putFloat("admin", admin)
        editor.putFloat("angsuran", angsuran)
        editor.putFloat("diterima", sisa)
        editor.putString("tujuan", tujuan)
        editor.putString("tglMulai", tglPinjam)
        editor.putString("tglAkhir", tglAkhirPinjam)
        editor.putFloat("asuransi", asuransi)
        editor.apply()
        startActivity(Intent(this@PinjamanRegularActivity, PelengkapanRegularActivity::class.java))
       /* digisignPrestImp.data(SharedPrefManager.getInstance(this).user.nip
                ,SharedPrefManager.getInstance(this).user.email)*/

        /*PinjamanKilatInterface pinjam = RetrofitClient.getClient().create(PinjamanKilatInterface.class);
        Call<ResponseBody> call = pinjam.ajukanPinjaman(nip, pinjamanUang, plafond, 0, BUNGA_PERBULAN,
                bunga, admin, angsuran, BIAYA_TRANSFER, tujuan, tglPinjam, tglAkhirPinjam, "", sisa, asuransi, );*/
    }

    override fun digiResponse(response: RDigisign) {
        Log.d("Masuk Handler SUV >>>>"," ----------------------------------------- >>>>> RESPONSE")
        if (response.data!!.isNotEmpty()){
            Log.d("Digisign", "digiResponse: Selamat Akun Anda Sudah Teraktivasi")
            val i = Intent(this@PinjamanRegularActivity, PersetujuanActivity::class.java)
            i.putExtra("activity", "kilat")
            startActivity(i)
        } else {
//            mToast(this,"Belum Teraktivasi")
            Log.d("Digisign", "Belum Teraktivasi")

            val i = Intent(this, ECommerceActivity::class.java)
            i.putExtra("fragment", "digisign")
            i.putExtra("activity", "regular")
            startActivity(i)
        }

    }

    override fun digiError(error: String) {
        Log.e("Error",error)
    }
}
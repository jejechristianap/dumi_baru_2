package com.minjem.dumi.jenispinjaman

import android.annotation.SuppressLint
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
import com.minjem.dumi.PersetujuanActivity
import com.minjem.dumi.R
import com.minjem.dumi.api.GetBungaInterface
import com.minjem.dumi.api.StatusPinjamanInterface
import com.minjem.dumi.ecommerce.ECommerceActivity
import com.minjem.dumi.ecommerce.Helper.mToast
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.model.User
import com.minjem.dumi.presenter.DigisignPrestImp
import com.minjem.dumi.response.RDigisign
import com.minjem.dumi.retrofit.RetrofitClient
import com.minjem.dumi.view.DigisignView
import kotlinx.android.synthetic.main.activity_pinjaman_kilat.*
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

class PinjamanKilatActivity : AppCompatActivity(), DigisignView {
    lateinit var seekBar: SeekBar
    lateinit var jumlah: CurrencyEditText
    lateinit var back: ImageView
    private var pinjamanUang = 1000000
    lateinit var angsuranTv: TextView
    lateinit var biayaAdminTv: TextView
    lateinit var biayaAsuransiTv: TextView
    lateinit var biayaTransferTv: TextView
    lateinit var jumlahTerimaTv: TextView
    lateinit var bulan3: Button
    lateinit var bulan6: Button
    lateinit var bulan12: Button
    lateinit var ajukanButton: Button
    lateinit var tujuanSpinner: Spinner
    lateinit var tujuanAdapter: ArrayAdapter<CharSequence>
    private val tujuanPinjaman = arrayOf("Renovasi Rumah", "Biaya Pendidikan", "Biaya Rumah Sakit",
            "Jalan-jalan", "Biaya Pernikahan", "Modal Usaha", "Lainnya")
    private val PEMBAYARAN_3_BULAN = 3
    private val PEMBAYARAN_6_BULAN = 6
    private val PEMBAYARAN_12_BULAN = 12
    private val JUMLAH_BULAN_1_TAHUN = 12
    private val JUMLAH_PINJAMAN_DEFAULT = 1000000
    private val BIAYA_ADMIN = 0.01.toFloat()
    private val BIAYA_ASURANSI = 0.01.toFloat()
    private val BIAYA_TRANSFER = 6500f
    private val BUNGA_PERBULAN = 0.099.toFloat()
    private var bunga = 0f
    private var admin = 0f
    private var angsuran = 0f
    private var asuransi = 0f
    private var sisa = 0f
    private var plafond = 0
    private var getBunga = 0f
    private var getAdmin = 0f
    private var getAsur12 = 0f
    private var getAsur24 = 0f
    private var getAsur36 = 0f
    lateinit var localID: Locale
    lateinit var formatRp: NumberFormat
    lateinit var editor: SharedPreferences.Editor
    lateinit var pref: SharedPreferences
    lateinit var prefManager: User
    lateinit var adminTv: TextView
    lateinit var asuransiTv: TextView
    private var np = 1
    lateinit var digisignPrestImp : DigisignPrestImp
    private var tujuan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pinjaman_kilat)
        initView()
        getBunga()
        numberPicker(np)
        initTouch()

    }

    private fun initView(){

        digisignPrestImp = DigisignPrestImp(this)

        prefManager = SharedPrefManager.getInstance(applicationContext).user
        pref = applicationContext.getSharedPreferences("ajukanPinjaman", 0) // 0 - for private mode
        editor = pref.edit()

        /*tujuanSpinner = findViewById(R.id.tujuan_pinjaman_spinner)
        tujuanAdapter = ArrayAdapter(this@PinjamanKilatActivity, R.layout.spinner_text, tujuanPinjaman)
        tujuanAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
        tujuan_pinjaman_spinner.adapter = tujuanAdapter*/

        angsuranTv = findViewById(R.id.angsuran_perbulan_kilat)
        biayaAdminTv = findViewById(R.id.biaya_administrasi_kilat)
        biayaAsuransiTv = findViewById(R.id.biaya_asuransi_kilat)
        biayaTransferTv = findViewById(R.id.biaya_transfer_antar_bank_kilat)
        jumlahTerimaTv = findViewById(R.id.jumlah_yang_diterima_kilat)
        jumlah = findViewById(R.id.etCurrency)

        jumlah.setDecimals(false)
        jumlah.setSeparator(".")
        jumlah.setText("0")
        jumlah.requestFocus()

        npBulan.minValue = 1
        npBulan.maxValue = 12

        npTujuan.minValue = 0
        npTujuan.maxValue = tujuanPinjaman.size - 1
        npTujuan.displayedValues = tujuanPinjaman


        adminTv = findViewById(R.id.admin_asn)
        asuransiTv = findViewById(R.id.asuransi_asn)
        /*plafond = 0
        bunga = 0f
        admin = 0f
        angsuran = 0f
        asuransi = 0f
        sisa = 0f*/
        localID = Locale("in", "ID")
        formatRp = NumberFormat.getCurrencyInstance(localID)
        seekBar = findViewById(R.id.seekbar_kilat)
        seekBar.max = 15
    }

    private fun initTouch(){

        npBulan.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("NumberPicker", "initTouch: $picker, $oldVal, $newVal")
            np = newVal
            numberPicker(np)
        }

        npTujuan.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("NumberPicker Tujuan", "initTouch: $picker, $oldVal, ${tujuanPinjaman[newVal]}")
            tujuan = tujuanPinjaman[newVal]
        }


        jumlah.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("jumlah", "onTextChanged: $s, $start, $before, $count, ${jumlah.cleanIntValue}")

                /*in 0..999999 -> {
                        seekBar.progress = 1
                        seekBar.refreshDrawableState()
                    }
                    in 1000000..1999999 -> {
                        seekBar.progress = 2
                        seekBar.refreshDrawableState()
                    }
                    in 2000000..2999999 -> {
                        seekBar.progress = 3
                        seekBar.refreshDrawableState()
                    }
                    in 3000000..3999999 -> {
                        seekBar.progress = 4
                        seekBar.refreshDrawableState()
                    }
                    in 4000000..4999999 -> {
                        seekBar.progress = 5
                        seekBar.refreshDrawableState()
                    }
                    in 500000..5999999 -> {
                        seekBar.progress = 6
                        seekBar.refreshDrawableState()
                    }
                    in 7000000..7999999 -> {
                        seekBar.progress = 7
                        seekBar.refreshDrawableState()
                    }
                    in 8000000..8999999 -> {
                        seekBar.progress = 8
                        seekBar.refreshDrawableState()
                    }
                    in 9000000..9999999 -> {
                        seekBar.progress = 9
                        seekBar.refreshDrawableState()
                    }
                    in 10000000..10999999 -> {
                        seekBar.progress = 10
                        seekBar.refreshDrawableState()
                    }
                    in 11000000..11999999 -> {
                        seekBar.progress = 11
                        seekBar.refreshDrawableState()
                    }
                    in 12000000..12999999 -> {
                        seekBar.progress = 12
                        seekBar.refreshDrawableState()
                    }
                    in 13000000..13999999 -> {
                        seekBar.progress = 13
                        seekBar.refreshDrawableState()
                    }
                    in 14000000..14999999 -> {
                        seekBar.progress = 14
                        seekBar.refreshDrawableState()
                    }
                    15000000 ->{
                        seekBar.progress = 15
                        seekBar.refreshDrawableState()
                    }*/
                when (jumlah.cleanIntValue) {
                    !in 1000000..15000000 -> {
                        tvMinKil.visibility = View.VISIBLE
                        tvMaksKil.visibility = View.GONE
                    }
                    in 1000000..15000000 -> {
                        tvMinKil.visibility = View.GONE
                    }
                }
                numberPicker(np)
            }
        })
//        back = findViewById(R.id.back_kilat)
        back_kilat.setOnClickListener{
            val intent = Intent(this@PinjamanKilatActivity, MainActivity::class.java)
            startActivity(intent)
        }


        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressValue = 0
            var x = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressValue = progress
                Log.d("Progress Value", "onProgressChanged: $pinjamanUang")
                Log.d("Progress Value", "onProgressChanged: $progressValue")
                x = progressValue * pinjamanUang
                Log.d("Progress Value", "onProgressChanged: $progressValue")
                /*angsuranTv.text = ""
                biayaAdminTv.text = ""
                biayaAsuransiTv.text = ""
                biayaTransferTv.text = ""
                jumlahTerimaTv.text = ""*/
                jumlah.setText(x.toString())

                /*when (progressValue) {
                    0 -> {
                        pinjamanUang = 1000000
                        jumlah.setText(pinjamanUang.toString())
                    }
                    1 -> {
                        pinjamanUang = 1500000
                        jumlah.setText(pinjamanUang.toString())
                    }
                    2 -> {
                        pinjamanUang = 2000000
                        jumlah.setText(pinjamanUang.toString())
                    }
                    3 -> {
                        pinjamanUang = 2500000
                        jumlah.setText(pinjamanUang.toString())
                    }
                    4 -> {
                        pinjamanUang = 3000000
                        jumlah.setText(pinjamanUang.toString())
                    }
                    5 -> {
                        pinjamanUang = 3500000
                        jumlah.setText(pinjamanUang.toString())
                    }
                    6 -> {
                        pinjamanUang = 4000000
                        jumlah.setText(pinjamanUang.toString())
                    }
                    7 -> {
                        pinjamanUang = 4500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    8 -> {
                        pinjamanUang = 5000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    9 -> {
                        pinjamanUang = 5500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    10 -> {
                        pinjamanUang = 6000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    11 -> {
                        pinjamanUang = 6500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    12 -> {
                        pinjamanUang = 7000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    13 -> {
                        pinjamanUang = 7500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    14 -> {
                        pinjamanUang = 8000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    15 -> {
                        pinjamanUang = 8500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    16 -> {
                        pinjamanUang = 9000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    17 -> {
                        pinjamanUang = 9500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    18 -> {
                        pinjamanUang = 10000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    19 -> {
                        pinjamanUang = 10500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    20 -> {
                        pinjamanUang = 11000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    21 -> {
                        pinjamanUang = 11500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    22 -> {
                        pinjamanUang = 12000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    23 -> {
                        pinjamanUang = 12500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    24 -> {
                        pinjamanUang = 13000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    25 -> {
                        pinjamanUang = 13500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    26 -> {
                        pinjamanUang = 14000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    27 -> {
                        pinjamanUang = 14500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    28 -> {
                        pinjamanUang = 15000000
                        jumlah.setText(pinjamanUang.toString())
                    }
                }*/
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                numberPicker(np)
                x = progressValue * pinjamanUang
                jumlah.setText(x.toString())
                /*when (progressValue) {
                    0 -> {
                        pinjamanUang = 1000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    1 -> {
                        pinjamanUang = 1500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    2 -> {
                        pinjamanUang = 2000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    3 -> {
                        pinjamanUang = 2500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    4 -> {
                        pinjamanUang = 3000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    5 -> {
                        pinjamanUang = 3500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    6 -> {
                        pinjamanUang = 4000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    7 -> {
                        pinjamanUang = 4500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    8 -> {
                        pinjamanUang = 5000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    9 -> {
                        pinjamanUang = 5500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    10 -> {
                        pinjamanUang = 6000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    11 -> {
                        pinjamanUang = 6500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    12 -> {
                        pinjamanUang = 7000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    13 -> {
                        pinjamanUang = 7500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    14 -> {
                        pinjamanUang = 8000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    15 -> {
                        pinjamanUang = 8500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    16 -> {
                        pinjamanUang = 9000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    17 -> {
                        pinjamanUang = 9500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    18 -> {
                        pinjamanUang = 10000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    19 -> {
                        pinjamanUang = 10500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    20 -> {
                        pinjamanUang = 11000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    21 -> {
                        pinjamanUang = 11500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    22 -> {
                        pinjamanUang = 12000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    23 -> {
                        pinjamanUang = 12500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    24 -> {
                        pinjamanUang = 13000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    25 -> {
                        pinjamanUang = 13500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    26 -> {
                        pinjamanUang = 14000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    27 -> {
                        pinjamanUang = 14500000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                    28 -> {
                        pinjamanUang = 15000000
                        jumlah.setText(pinjamanUang.toString())
                        return
                    }
                }*/
            }
        })

        /*rgKilat.setOnCheckedChangeListener{ _, checkId ->
            val radio: RadioButton = findViewById(checkId)
            rb = radio.text.toString()
            Log.d("RadioButton", if(rbKilat3.isChecked) "radio 3" else "radio lain")
            onCheckRadio()
        }*/




        ajukanButton = findViewById(R.id.lanjut_button_kilat)
        ajukanButton.setOnClickListener{
            val angs = angsuranTv.text.toString()
            if (TextUtils.isEmpty(angs)) {
                Toast.makeText(this, "Tentukan Lama Pembayaran", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            when {
                jumlah.cleanIntValue < 1000000 -> {
                    mToast(this, getString(R.string.minimum_pinjaman_rp_1_000_000))
                    return@setOnClickListener
                }
                jumlah.cleanIntValue > 15000000 -> {
                    mToast(this, getString(R.string.maksimum_pinjaman_rp_15_000_000))
                    return@setOnClickListener
                }
            }

            pinjaman
        }
    }

    private fun numberPicker(number: Int){
        plafond = number
        val pokok = jumlah.cleanDoubleValue.toFloat() / plafond.toFloat()
        bunga = jumlah.cleanDoubleValue.toFloat() * getBunga / JUMLAH_BULAN_1_TAHUN
        angsuran = pokok + bunga
        admin = jumlah.cleanDoubleValue.toFloat() * getAdmin
        asuransi = jumlah.cleanDoubleValue.toFloat() * getAsur12
        val totalPengurangan = admin + asuransi + BIAYA_TRANSFER
        sisa = jumlah.cleanDoubleValue.toFloat() - totalPengurangan
        angsuranTv.text = formatRp.format(angsuran.toDouble())
        biayaAdminTv.text = formatRp.format(admin.toDouble())
        biayaAsuransiTv.text = formatRp.format(asuransi.toDouble())
        biayaTransferTv.text = formatRp.format(BIAYA_TRANSFER.toDouble())
        jumlahTerimaTv.text = formatRp.format(sisa.toDouble())
        /*when(rb){
               "3" -> {
                   plafond = PEMBAYARAN_3_BULAN
                   val pokok = jumlah.cleanDoubleValue.toFloat() / plafond.toFloat()
                   bunga = jumlah.cleanDoubleValue.toFloat() * getBunga / JUMLAH_BULAN_1_TAHUN
                   angsuran = pokok + bunga
                   admin = jumlah.cleanDoubleValue.toFloat() * getAdmin
                   asuransi = jumlah.cleanDoubleValue.toFloat() * getAsur12
                   val totalPengurangan = admin + asuransi + BIAYA_TRANSFER
                   sisa = jumlah.cleanDoubleValue.toFloat() - totalPengurangan
                   angsuranTv.text = formatRp.format(angsuran.toDouble())
                   biayaAdminTv.text = formatRp.format(admin.toDouble())
                   biayaAsuransiTv.text = formatRp.format(asuransi.toDouble())
                   biayaTransferTv.text = formatRp.format(BIAYA_TRANSFER.toDouble())
                   jumlahTerimaTv.text = formatRp.format(sisa.toDouble())
               }
               "6" -> {
                   plafond = PEMBAYARAN_6_BULAN
                   val pokok = jumlah.cleanDoubleValue.toFloat() / plafond.toFloat()
                   bunga = jumlah.cleanDoubleValue.toFloat() * getBunga / JUMLAH_BULAN_1_TAHUN
                   angsuran = pokok + bunga
                   admin = jumlah.cleanDoubleValue.toFloat() * getAdmin
                   asuransi = jumlah.cleanDoubleValue.toFloat() * getAsur12
                   val totalPengurangan = admin + asuransi + BIAYA_TRANSFER
                   sisa = jumlah.cleanDoubleValue.toFloat() - totalPengurangan
                   angsuranTv.text = formatRp.format(angsuran.toDouble())
                   biayaAdminTv.text = formatRp.format(admin.toDouble())
                   biayaAsuransiTv.text = formatRp.format(asuransi.toDouble())
                   biayaTransferTv.text = formatRp.format(BIAYA_TRANSFER.toDouble())
                   jumlahTerimaTv.text = formatRp.format(sisa.toDouble())
               }
               "12" -> {
                   plafond = PEMBAYARAN_12_BULAN
                   val pokok = jumlah.cleanDoubleValue.toFloat() / plafond.toFloat()
                   bunga = jumlah.cleanDoubleValue.toFloat() * getBunga / JUMLAH_BULAN_1_TAHUN
                   angsuran = pokok + bunga
                   admin = jumlah.cleanDoubleValue.toFloat() * getAdmin
                   asuransi = jumlah.cleanDoubleValue.toFloat() * getAsur12
                   val totalPengurangan = admin + asuransi + BIAYA_TRANSFER
                   sisa = jumlah.cleanDoubleValue.toFloat() - totalPengurangan

                   angsuranTv.text = formatRp.format(angsuran.toDouble())
                   biayaAdminTv.text = formatRp.format(admin.toDouble())
                   biayaAsuransiTv.text = formatRp.format(asuransi.toDouble())
                   biayaTransferTv.text = formatRp.format(BIAYA_TRANSFER.toDouble())
                   jumlahTerimaTv.text = formatRp.format(sisa.toDouble())
               }
           }*/
    }

    //        Toast.makeText(this, "NIp: " + nip, Toast.LENGTH_SHORT).show();
    private val pinjaman: Unit
        get() {
            val nip = prefManager.nip
            //        Toast.makeText(this, "NIp: " + nip, Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(this@PinjamanKilatActivity, "Anda sudah mengajukan pinjaman. Mohon menunggu info dari kami.", Toast.LENGTH_SHORT).show()
                                    }
                                    statusPinjaman.contains(2) -> {
                                        Toast.makeText(this@PinjamanKilatActivity, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT).show()
                                    }
                                    statusPinjaman.contains(4) -> {
                                        Toast.makeText(this@PinjamanKilatActivity, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT).show()
                                    }
                                    statusPinjaman.contains(5) -> {
                                        Toast.makeText(this@PinjamanKilatActivity, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT).show()
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
                    //                    Toast.makeText(PinjamanKilatActivity.this, "status: " + status, Toast.LENGTH_SHORT).show();
                    if (status) {
                        val data = obj.getString("data")
                        val bungaObj = JSONArray(data)
                        for (i in 0 until bungaObj.length()) {
                            val bung = bungaObj.getJSONObject(i)
                            val id = bung.getInt("id_bunga")
                            //                            Toast.makeText(PinjamanKilatActivity.this, "instansi: " + id, Toast.LENGTH_SHORT).show();
                            if (id == 1 && insker.contains("Badan Kepegawaian Negara")) {
                                adminTv.text = "Biaya Administrasi 1%"
                                asuransiTv.text = "Biaya Asuransi 1%"
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
                                adminTv.text = "Biaya Administrasi 2%"
                                asuransiTv.text = "Biaya Asuransi 2%"
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
                    pDialog.dismiss()
                    Log.d("Get Bunga", "onFailure: ${e.message.toString()}")
                } catch (e: IOException) {
                    e.printStackTrace()
                    pDialog.dismiss()
                    Log.d("Get Bunga", "onFailure: ${e.message.toString()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                pDialog.dismiss()
                Log.d("Get Bunga", "onFailure: ${t.message.toString()}")
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun ajukanPinjaman() {
        val user = SharedPrefManager.getInstance(applicationContext).user
        val nip = user.nip
//        val tujuan = tujuanSpinner.selectedItem.toString()
        val dateFormat = SimpleDateFormat(DATE_FORMAT_2)
        val c = Calendar.getInstance()
        val today = c.time
        val tglPinjam = dateFormat.format(today)
        c.add(Calendar.MONTH, plafond)
        val dueDate = c.time
        val tglAkhirPinjam = dateFormat.format(dueDate)
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
        editor.putString("activity", "kilat")
        editor.apply()
        Log.d("AJUKAN", "ajukanPinjaman: $editor")
        digisignPrestImp.data(SharedPrefManager.getInstance(this).user.nip
                ,SharedPrefManager.getInstance(this).user.email)
        /*val i = Intent(this@PinjamanKilatActivity, PersetujuanActivity::class.java)
        i.putExtra("activity", "kilat")
        startActivity(i)*/
    }

    companion object {
        const val DATE_FORMAT_2 = "yyyy-MM-dd"
    }

    override fun digiResponse(response: RDigisign) {
        Log.d("Masuk Handler SUV >>>>"," ----------------------------------------- >>>>> RESPONSE")
        if (response.data!!.isNotEmpty()){
            Log.d("Digisign", "digiResponse: Selamat Akun Anda Sudah Teraktivasi")
            val i = Intent(this@PinjamanKilatActivity, PersetujuanActivity::class.java)
            i.putExtra("activity", "kilat")
            startActivity(i)
        } else {
//            mToast(this,"Belum Teraktivasi")
            Log.d("Digisign", "Belum Teraktivasi")

            val i = Intent(this, ECommerceActivity::class.java)
            i.putExtra("fragment", "digisign")
            i.putExtra("activity", "kilat")
            startActivity(i)
        }

    }

    override fun digiError(error: String) {
        Log.e("Error",error)
    }
}
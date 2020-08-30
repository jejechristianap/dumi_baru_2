package com.minjem.dumi.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mdi.stockin.ApiHelper.RecyclerItemClickListener
import com.minjem.dumi.HalamanDepanActivity
import com.minjem.dumi.R
import com.minjem.dumi.adapter.EcommerceMenuAdapter
import com.minjem.dumi.api.GetBungaInterface
import com.minjem.dumi.api.StatusPinjamanInterface
import com.minjem.dumi.dataclass.DataEcommerce
import com.minjem.dumi.ecommerce.Helper.sBar
import com.minjem.dumi.jenispinjaman.PinjamanKilatActivity
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.retrofit.RetrofitClient
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_bottom_hitung.view.*
import kotlinx.android.synthetic.main.dialog_bottom_sheet_layout.view.*
import kotlinx.android.synthetic.main.fragment_beranda.*
import kotlinx.android.synthetic.main.fragment_beranda.admin_asn_ultimate
import kotlinx.android.synthetic.main.fragment_beranda.asuransi_asn_ultimate
import kotlinx.android.synthetic.main.fragment_beranda.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class BerandaFragment : Fragment() /*, Toolbar.OnMenuItemClickListener*/{
    private var saldoUser = 0
    lateinit var mView : View
    lateinit var mContext : Context
    lateinit var dView : View
    lateinit var eAdapter: EcommerceMenuAdapter
    private var maksimalAngsuran = 0
    private var cicilanPertahun = 0
    private var maksimalPinjaman = 0
    private var maksimalTenor = 0
    private var plafond1 = 0
    private var plafond2 = 0
    private var plafond3 = 0
    private var plafond4 = 0
    private var plafond5 = 0
    private var plafond6 = 0
    private var plafond7 = 0
    private var plafond8 = 0
    private var plafond9= 0
    private var plafond10 = 0
    private var plafond11 = 0
    private var plafond12 = 0
    private var plafond13 = 0
    private var minPinjaman = 1000000
    private var getBunga = 0f
    private var bunga = 0f
    private var admin = 0f
    private var sisa = 0f
    private var asuransi = 0f
    private var angsuran = 0f
    private var getAdmin = 0f
    private var getAsur12 = 0f
    private var getAsur24 = 0f
    private var getAsur36 = 0f
    private var npTenor = 0
    private var npTujuan = ""
    lateinit var localID: Locale
    lateinit var formatRp: NumberFormat


    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_beranda, container, false)
        mContext = this.context!!


        initView()
        getBunga()
        initOnTouch()
        /*val toolbar = activity!!.findViewById<View>(R.id.toolbarMain) as Toolbar
        toolbar.inflateMenu(R.menu.menu_toolbar_main)
        toolbar.setOnMenuItemClickListener(this)*/
        /*eAdapter = EcommerceMenuAdapter(mContext, generateMenu())
        mView.gvMenuUtama.layoutManager = GridLayoutManager(mContext, 4)
        mView.gvMenuUtama.adapter = eAdapter
        rvClick()
        initOnTouch()
        saldo()
        val network = InternetConnection(mContext)
        network.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (isConnected){
                Log.d("Network", "onCreateView: $isConnected")
            } else {
                dialogTkb("connection")
            }
        })*/
        return mView
    }

    private fun initView(){
        mView.cetJumlahPinjaman.setDecimals(false)
        mView.cetJumlahPinjaman.setSeparator(".")
        mView.cetJumlahPinjaman.setText("0")


        localID = Locale("in", "ID")
        formatRp = NumberFormat.getCurrencyInstance(localID)

        when(SharedPrefManager.getInstance(mContext).user.nama_bank){
            "MANTAP" -> {
                mView.npTenorPinjaman.minValue = 1
                mView.npTenorPinjaman.maxValue = 180
            }
            "BNI" -> {
                mView.npTenorPinjaman.minValue = 1
                mView.npTenorPinjaman.maxValue = SharedPrefManager.getInstance(mContext).user.maksimal_tenor * 12
            }
        }

        mView.npTujuanPinjaman.minValue = 0
        mView.npTujuanPinjaman.maxValue = PinjamanKilatActivity.TUJUAN_PINJAMAN.size - 1
        mView.npTujuanPinjaman.displayedValues = PinjamanKilatActivity.TUJUAN_PINJAMAN

        mView.npTenorPinjaman.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("NumberPicker", "initTouch: $picker, $oldVal, $newVal")
            npTenor = newVal
        }

        mView.npTujuanPinjaman.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("NumberPicker", "initTouch: $picker, $oldVal, ${PinjamanKilatActivity.TUJUAN_PINJAMAN[newVal]}")
            npTujuan = PinjamanKilatActivity.TUJUAN_PINJAMAN[newVal]
        }

        mView.sbPinjaman.max = 50


    }

    private fun generateMenu(): List<DataEcommerce> {
        return listOf(DataEcommerce("PULSA", R.drawable.ic_pulsa),
                DataEcommerce("PLN", R.drawable.ic_token_pln),
                DataEcommerce("GOPAY", R.drawable.ic_gopay),
                DataEcommerce("OVO", R.drawable.ic_ovo),
                DataEcommerce("HOTEL", R.drawable.ic_hotel),
                DataEcommerce("PESAWAT", R.drawable.ic_pesawat),
                DataEcommerce("KERETA", R.drawable.ic_kereta),
                DataEcommerce("SEMUA", R.drawable.ic_all))
    }

    private fun rvClick(){
        mView.gvMenuUtama.addOnItemTouchListener(RecyclerItemClickListener(mContext, object: RecyclerItemClickListener.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                sBar(mView, eAdapter.list[position].title.toString())
            }
        }))
    }

    private fun initOnTouch() {
        mView.bHitungPinjaman.setOnClickListener {
            if (cetJumlahPinjaman.cleanIntValue == 0){
                mView.cetJumlahPinjaman.error = "Silakan tentukan jumlah pinjaman"
                return@setOnClickListener
            } else {
                mView.cetJumlahPinjaman.error = null
            }
            hitungPinjaman()
        }

        mView.sbPinjaman.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var progressValue = 0
            val base = 2000000
            var x = 0

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progressValue = progress
                x = progressValue * base
                mView.cetJumlahPinjaman.setText(x.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                x = progressValue * base
                mView.cetJumlahPinjaman.setText(x.toString())
            }

        })

        /*mView.cardKilat.setOnClickListener { goTo("kilat") }
        mView.kilatButton.setOnClickListener { goTo("kilat") }
        mView.cardRegular.setOnClickListener { goTo("regular") }
        mView.regularButton.setOnClickListener { goTo("regular") }
        mView.cardUltimate.setOnClickListener { goTo("ultimate") }
        mView.bUltimate.setOnClickListener { goTo("ultimate") }
        mView.riwayatButton.setOnClickListener { goTo("riwayat") }
        mView.icPulsa.setOnClickListener { goTo("pulsa") }
        mView.textPulsa.setOnClickListener { goTo("pulsa") }
        mView.icPln.setOnClickListener { goTo("pln") }
        mView.textPln.setOnClickListener { goTo("pln") }
        mView.icGopay.setOnClickListener { goTo("na") }
        mView.textGopay.setOnClickListener { goTo("na") }
        mView.icOvo.setOnClickListener { goTo("na") }
        mView.textOvo.setOnClickListener { goTo("na") }
        mView.icHotel.setOnClickListener { goTo("na") }
        mView.textHotel.setOnClickListener { goTo("na") }
        mView.icPesawat.setOnClickListener { goTo("na") }
        mView.textPesawat.setOnClickListener { goTo("na") }
        mView.icKereta.setOnClickListener { goTo("na") }
        mView.textKereta.setOnClickListener { goTo("na") }
        mView.icSemua.setOnClickListener { goTo("semua") }
        mView.textSemua.setOnClickListener { goTo("semua") }
        mView.tkb.setOnClickListener {
            dialogTkb("tkb")
        }*/


    }


    lateinit var v: View
    private fun hitungDialog(angsuran: Double, admin: Double, asuransi: Double, trf: Double, sisa: Double){
        dView = layoutInflater.inflate(R.layout.dialog_bottom_hitung, null)
        val dialog = BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme)
        dialog.setContentView(dView)
        dialog.show()

        dView.tvAngsuranDialog.text = formatRp.format(angsuran)
        dView.tvAdminDialog.text = formatRp.format(admin)
        dView.tvAsuransiDialog.text = formatRp.format(asuransi)
        dView.tvTrfBankDialog.text = formatRp.format(trf)
        dView.tvJumlahTerimaDialog.text = formatRp.format(sisa)

        v = dView

        dView.bAjukanSekarang.setOnClickListener {
            pinjaman
        }
        dView.bBatal.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun getBunga(){
        RetrofitClient.client.create(GetBungaInterface::class.java)
                .bunga.enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("Get Bunga", "onFailure: ${t.message.toString()}")
                        sBar(mView, "Koneksi server terputus!")
                        getBunga()
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful){
                            val jsonObject = JSONObject(response.body()!!.string())
                            if (jsonObject.getBoolean("status")){
                                val jsonArray = JSONArray(jsonObject.getString("data"))
                                for (i in 0 until jsonArray.length()){
                                    if (jsonArray.getJSONObject(i).getInt("id_bunga") == 1 &&
                                            SharedPrefManager.getInstance(mContext).user.inskerKerja.contains("Badan Kepegawaian Negara")){
                                        admin_asn_ultimate.text = "Biaya Administrasi 1%"
                                        asuransi_asn_ultimate.text = "Biaya Asuransi 1%"
                                        getBunga = jsonArray.getJSONObject(i).getDouble("bunga").toFloat()
                                        getAdmin = jsonArray.getJSONObject(i).getDouble("biaya_admin").toFloat()
                                        getAsur12 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_12").toFloat()
                                        getAsur24 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_24").toFloat()
                                        getAsur36 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_36").toFloat()
                                    } else if (jsonArray.getJSONObject(i).getInt("id_bunga") == 2 && !SharedPrefManager.getInstance(mContext).user.inskerKerja.contains("Badan Kepegawaian Negara")){
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
                                sBar(mView, "Gagal mengambil data!")
                            }

                        } else {
                            sBar(mView, response.message().toString())
                        }
                    }

                })
    }

    private fun hitungPinjaman(){
        val pokok = mView.cetJumlahPinjaman.cleanDoubleValue.toFloat() / npTenor
        bunga = mView.cetJumlahPinjaman.cleanDoubleValue.toFloat() * getBunga / PinjamanKilatActivity.JUMLAH_BULAN_1_TAHUN
        angsuran = pokok + bunga
        admin = mView.cetJumlahPinjaman.cleanDoubleValue.toFloat() * getAdmin
        asuransi = mView.cetJumlahPinjaman.cleanDoubleValue.toFloat() * getAsur12
        val totalPengurangan = admin + asuransi + PinjamanKilatActivity.BIAYA_TRANSFER
        sisa = mView.cetJumlahPinjaman.cleanDoubleValue.toFloat() - totalPengurangan
        Log.d("Asuransi", "numberPicker: $getAsur12")

        Log.d("Hitung Pinjaman", "Angsuran: $angsuran\n" +
                "Admin: $admin\n Asuransi: $asuransi\n Jumlah Terima: $sisa")

        hitungDialog(angsuran.toDouble(), admin.toDouble(), asuransi.toDouble(), PinjamanKilatActivity.BIAYA_TRANSFER.toDouble(), sisa.toDouble())

        /*tvAngsuranPerbulanUltimate.text = formatRp.format(angsuran.toDouble())
        tvAdminUltimate.text = formatRp.format(admin.toDouble())
        tvAsuransiUltimate.text = formatRp.format(asuransi.toDouble())
        tvTrfUltimate.text = formatRp.format(PinjamanKilatActivity.BIAYA_TRANSFER.toDouble())
        tvJumlahTerimaUltimate.text = formatRp.format(sisa.toDouble())*/
//        dialogTkb()
    }

    private val pinjaman: Unit
        get(){
            val status = RetrofitClient.client.create(StatusPinjamanInterface::class.java)
                    .getPinjaman(SharedPrefManager.getInstance(mContext).user.nip)
                    .enqueue(object : Callback<ResponseBody>{
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.d("Get Bunga", "onFailure: ${t.message.toString()}")
                            sBar(v, "Koneksi server terputus!")
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
//                                                sBar(v, "Anda sudah mengajukan pinjaman. Mohon menunggu info dari kami.")
                                                Toasty.warning(mContext, "Anda sudah mengajukan pinjaman. Mohon menunggu info dari kami.", Toast.LENGTH_LONG, true).show()
                                            }
                                            statusPinjaman.contains(2) -> {
//                                                sBar(v, "Masih ada tagihan yang belum selesai, terima kasih.")
                                                Toasty.warning(mContext, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_LONG, true).show()

                                            }
                                            statusPinjaman.contains(4) -> {
//                                                sBar(v, "Masih ada tagihan yang belum selesai, terima kasih.")
                                                Toasty.warning(mContext, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_LONG, true).show()
                                            }
                                            statusPinjaman.contains(5) -> {
//                                                sBar(v, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT)
                                                Toasty.warning(mContext, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_LONG, true).show()
                                            }
                                            else -> {
//                                                ajukanPinjaman()
//                                                sBar(v, "Bisa ajukan")
                                                Toasty.warning(mContext, "Bisa ajukan", Toast.LENGTH_LONG, true).show()
                                            }
                                        }
                                    } else {
                                        sBar(v, "Bisa ajukan")
//                                        ajukanPinjaman()
                                    }
                                } else {
                                    sBar(v, "Gagal mengambil data")
                                }
                            } else {
                                sBar(v, "Koneksi server terputus!")

                            }
                        }

                    })

        }



    private fun goTo(item: String) {
        /*val intent: Intent
        when (item) {
            "kilat" -> startActivity(Intent(activity, PinjamanKilatActivity::class.java))
            "regular" -> startActivity(Intent(activity, PinjamanRegularActivity::class.java))
            "ultimate" -> startActivity(Intent(activity, PinjamanUltimateActivity::class.java))
            "riwayat" -> startActivity(Intent(activity, RiwayatView::class.java))
            "pulsa" -> {
                intent = Intent(activity, ECommerceActivity::class.java)
                intent.putExtra("fragment", "pulsa")
                startActivity(intent)
            }
            "pln" -> {
                intent = Intent(activity, ECommerceActivity::class.java)
                intent.putExtra("fragment", "pln")
                startActivity(intent)
            }
            "flight" -> {
                intent = Intent(activity, ECommerceActivity::class.java)
                intent.putExtra("fragment", "flight")
                startActivity(intent)
            }
            "pinjaman" -> {
                intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("fragment", "pinjaman")
            }
            "semua" -> startActivity(Intent(activity, SemuaEcommerceActivity::class.java))
            else -> Toast.makeText(activity, "Tunggu update kami selanjutanya...", Toast.LENGTH_SHORT).show()
        }*/
    }

    /*override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.actionLogout -> {
//                getHistoryPinjaman()
                SharedPrefManager.getInstance(mContext).logout()
                activity!!.finish()
                startActivity(Intent(mContext, HalamanDepanActivity::class.java))
                return true
            }

        }
        return false
    }*/

    /*private fun showBottomDialog(){
           val bsf = BottomSheetFragment()
           bsf.show(fragmentManager!!, bsf.tag)
       }*/
    /*private fun saldo(){
            val idUser = SharedPrefManager.getInstance(activity).user.id
            val nipBaru : String? = SharedPrefManager.getInstance(activity).user.nip
            val api = RetrofitClient.client.create(BaseApiService::class.java)
            val call = api.getSaldo(idUser, nipBaru.toString(), USERNAME, PASSWORD)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            if (jsonObject.getBoolean("status")) {
                                val jsonArray = JSONArray(jsonObject.getString("data"))
                                for (i in 0 until jsonArray.length()) {
                                    saldoUser = jsonArray.getJSONObject(i).optInt("saldo", 0)
                                }
                                Log.d("SaldoUser", "Saldo: $saldoUser")
                                localID = Locale("in", "ID")
                                formatRp = NumberFormat.getCurrencyInstance(localID!!)
                                mView.saldoPayLater.text = formatRp.format(saldoUser)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    val base = "Rp0"
                    mView.saldoPayLater.text = base
                    dialogTkb("connection")
                }
            })
        }*/
}
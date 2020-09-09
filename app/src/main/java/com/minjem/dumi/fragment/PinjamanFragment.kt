package com.minjem.dumi.fragment

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mdi.stockin.ApiHelper.RecyclerItemClickListener
import com.minjem.dumi.R
import com.minjem.dumi.adapter.HistoryPinjamanAdapter
import com.minjem.dumi.api.StatusPinjamanInterface
import com.minjem.dumi.dataclass.HistoryData
import com.minjem.dumi.ecommerce.ECommerceActivity
import com.minjem.dumi.ecommerce.Helper.mProgress
import com.minjem.dumi.ecommerce.Helper.mToast
import com.minjem.dumi.ecommerce.Helper.sBar
import com.minjem.dumi.ecommerce.api.HttpRetrofitClient
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.model.User
import com.minjem.dumi.presenter.DigisignPrestImp
import com.minjem.dumi.response.RDigisign
import com.minjem.dumi.retrofit.RetrofitClient
import com.minjem.dumi.util.CustomProgressDialog
import com.minjem.dumi.view.DigisignView
import kotlinx.android.synthetic.main.d_webview.*
import kotlinx.android.synthetic.main.dialog_history_pinjaman.*
import kotlinx.android.synthetic.main.fragment_pinjaman.*
import kotlinx.android.synthetic.main.fragment_pinjaman.view.*
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
import kotlin.collections.ArrayList

class PinjamanFragment : Fragment(), DigisignView/*, Toolbar.OnMenuItemClickListener*/ {
    private var statusPinjamanTv: TextView? = null
    private var pinjamanTv: TextView? = null
    private var tenorPinjamanTv: TextView? = null
    private var bungaTv: TextView? = null
    private var angsuranPerbulanTv: TextView? = null
    private var asuransiTv: TextView? = null
    private var adminTv: TextView? = null
    private var transferBankTv: TextView? = null
    private var jumlahTerimaTv: TextView? = null
    private var tglPengajuanTv: TextView? = null
    private var tvGagal: TextView? = null
    private var prefManager: User? = null
    private var localID: Locale? = null
    private var formatRp: NumberFormat? = null
    private lateinit var mContext: Context
    private var svTagihan: ScrollView? = null
    private var rlGagal: RelativeLayout? = null
    lateinit var v: View
    private val progressDialog = CustomProgressDialog()
    var list : MutableList<HistoryData> = ArrayList()
    var listPinjaman : MutableList<HistoryData> = ArrayList()
    var listResult : MutableList<HistoryData> = ArrayList()
    private val TAG = PinjamanFragment::class.qualifiedName
    private val dataPinjaman = HistoryData()
    private val sign = HistoryData()
    lateinit var mDialog: Dialog
    lateinit var pinjamanAdapter: HistoryPinjamanAdapter
    lateinit var digisignPrestImp : DigisignPrestImp
    private lateinit var dWeb : Dialog
    lateinit var dProgress : Dialog
    private var idPinjaman = 0
    private var documentId = ""
    private var regis = false
    private var activation = false
    private var pdfSend = ""
    private var resultDocument = false
    private var touch = false
    var i = 0
    val handler = Handler()
    private var runnableCode: Runnable? = null
    private var runnableActivation: Runnable? = null
    private var rc = false
    private var ra = false
    private var notif = ""
    private var resultSign = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_pinjaman, container, false)
        mContext = this.activity!!
//        (activity as AppCompatActivity).setSupportActionBar(v.toolbarPinjaman)
//        v.ctlPinjaman.title = "Pinjaman"
        /*val toolbar = activity!!.findViewById<View>(R.id.toolbarMain) as Toolbar
        toolbar.inflateMenu(R.menu.menu_toolbar_pinjaman)
        toolbar.setOnMenuItemClickListener(this)*/

        mDialog = Dialog(mContext, R.style.DialogTheme)
        digisignPrestImp = DigisignPrestImp(this)
        dWeb = Dialog(mContext)
        dProgress = Dialog(mContext)
        initView()
        initOnTouch()
        pinjaman

        return v
    }



    private fun refreshPinjaman(){
        mDialog.srlHistoryPinjaman.isRefreshing = false
        getPinjaman
    }

    private fun refreshList(){
        srlTagihan.isRefreshing = false
        pinjaman
    }


    private fun initOnTouch(){
//        val view = activity.findViewById(R.id.mainActivity)

       v.bRegisAktifasiDigisign.setOnClickListener{
           touch = true
           if (activation){
               sBar(v, "Selamat akun Digisign anda sudah teraktivasi.")
               return@setOnClickListener
           }

           if (!regis){
               AlertDialog.Builder(mContext)
                       .setMessage("Kami bekerja sama dengan digisign untuk melakukan tandatangan digital, " +
                               "apakah anda setuju? Jika anda setuju maka data anda akan kami kirim ke pihak digisign.")
                       .setPositiveButton("Saya setuju") { _: DialogInterface?, _: Int ->
                           mProgress(dProgress)
                           digisignPrestImp.data(SharedPrefManager.getInstance(mContext).user.nip
                                   ,SharedPrefManager.getInstance(mContext).user.email, idPinjaman)
                       }
                       .setNegativeButton("Tidak") { _: DialogInterface?, _: Int -> }
                       .show()
           } else {
               mProgress(dProgress)
               /*digisignPrestImp.data(SharedPrefManager.getInstance(mContext).user.nip
                       ,SharedPrefManager.getInstance(mContext).user.email, idPinjaman)*/
               val i = Intent(mContext, ECommerceActivity::class.java)
               i.putExtra("fragment", "digisign")
               i.putExtra("idPinjaman", idPinjaman)
               i.putExtra("regis", "00")
               startActivity(i)
               dProgress.dismiss()
           }


            /*val i = Intent(activity, PerjanjianKreditView::class.java)
            i.putExtra("tanggal", dataPinjaman.tglPengajuan)
            i.putExtra("pinjaman", tvPinjaman.text.toString())
            i.putExtra("bunga", dataPinjaman.bungaRupiah)
            i.putExtra("angsuran", dataPinjaman.angsuranPerbulan)
            i.putExtra("tujuan", dataPinjaman.tujuanPinjaman)
            i.putExtra("tenor", tenor_pinjaman_bulan.text.toString())
            startActivity(i)*/
        }

        v.bTtd.setOnClickListener {
            if (!regis){
                Snackbar.make(v, "Silahkan melakukan registrasi Digisign terlebih dahulu.", Snackbar.LENGTH_LONG).show()
            } else {
                if (!activation){
                    Log.d(TAG, "initOnTouch: Aktivation digisign $activation")
                    Snackbar.make(v, "Silahkan melakukan aktivasi Digisign terlebih dahulu.", Snackbar.LENGTH_LONG).show()
                } else {
                    if (pdfSend == "OK"){
                        if (resultDocument){
                            mToast(mContext, "Selamat dokumen anda telah ditanda tangani.")
                        } else {
                            mProgress(dProgress)
                            runnableCode = object : Runnable {
                                override fun run() {
                                    try {
                                        rc = true
                                        Log.d(TAG, "Handler >>>>>>>>>>>>>>>>>>>>>>>>>> START : Activation($activation)")
                                        signPinjaman
                                        handler.postDelayed(this, 3000)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        rc = false
                                    }

                                }
                            }
                            handler.post(runnableCode!!)
                            Log.d(TAG, "PdfSend: $pdfSend Result: $resultDocument")
                            getSignDocument(SharedPrefManager.getInstance(mContext).user.email, documentId)
                        }
                    } else{
                        Snackbar.make(v, "Mohon menunggu, dokumen anda sedang kami proses.", Snackbar.LENGTH_LONG).show()
                    }
                }
            }

        }

        v.srlTagihan.setOnRefreshListener {
            srlTagihan.isRefreshing = true
            refreshList()
        }

//        v.ivHistoryPinjaman.setOnClickListener {
//            getHistoryPinjaman()
//        }
    }


    private fun getHistoryPinjaman(){
        listPinjaman.clear()
        mDialog.setContentView(R.layout.dialog_history_pinjaman)
        mDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mDialog.rvHistoryPinjaman.layoutManager = GridLayoutManager(mContext, 1)
        pinjamanAdapter = HistoryPinjamanAdapter(mContext, listPinjaman)
        mDialog.rvHistoryPinjaman.adapter = pinjamanAdapter
        pinjamanAdapter.notifyDataSetChanged()
        mDialog.show()
        getPinjaman
        rvClick()
        mDialog.srlHistoryPinjaman.setOnRefreshListener {
            mDialog.srlHistoryPinjaman.isRefreshing = true
            listPinjaman.clear()
            refreshPinjaman()
        }
        mDialog.toolbarRiwayatPinjaman.title = ""
        mDialog.toolbarRiwayatPinjaman.setNavigationIcon(R.drawable.ic_back_white)
        mDialog.toolbarRiwayatPinjaman.setNavigationOnClickListener {
            mDialog.dismiss()
        }
    }

    private fun rvClick(){
        mDialog.rvHistoryPinjaman.addOnItemTouchListener(RecyclerItemClickListener(mContext, object : RecyclerItemClickListener.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {

            }

        }))
    }

    private fun initView(){
        prefManager = SharedPrefManager.getInstance(mContext).user
        localID = Locale("in", "ID")
        formatRp = NumberFormat.getCurrencyInstance(localID!!)
        statusPinjamanTv = v.findViewById(R.id.status_pinjaman)
        pinjamanTv = v.findViewById(R.id.tvPinjaman)
        tenorPinjamanTv = v.findViewById(R.id.tenor_pinjaman_bulan)
        bungaTv = v.findViewById(R.id.bunga_pinjaman)
        angsuranPerbulanTv = v.findViewById(R.id.angsuran_pinjaman)
        asuransiTv = v.findViewById(R.id.asuransi_pinjaman)
        adminTv = v.findViewById(R.id.administrasi_pinjaman)
        transferBankTv = v.findViewById(R.id.transfer_bank_pinjaman)
        jumlahTerimaTv = v.findViewById(R.id.jumlah_terima_pinjaman)
        tglPengajuanTv = v.findViewById(R.id.tanggal_pengajuan_pinjaman)
        svTagihan = v.findViewById(R.id.svTagihan)

        rlGagal = v.findViewById(R.id.rlGagalTagihan)
        tvGagal = v.findViewById(R.id.tvGagalTagihan)
    }

    private val pinjaman: Unit
        get() {
//            progressDialog.show(mContext, "Loading...")
            mProgress(dProgress)
            val nip = prefManager!!.nip
            val status = RetrofitClient.client.create(StatusPinjamanInterface::class.java)
            val call = status.getPinjaman(nip)
            call.enqueue(object : Callback<ResponseBody> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        try {
                            dProgress.dismiss()
                            val obj = JSONObject(response.body()!!.string())
                            val cek = obj.getBoolean("status")
                            if (cek) {
                                val data = obj.getString("data")
                                val jsonArray = JSONArray(data)
                                if (jsonArray.length() == 0) {
                                    rlGagal!!.visibility = View.VISIBLE
                                    svTagihan!!.visibility = View.GONE
                                    tvGagal!!.text = "Mohon maaf, belum ada tagihan."
                                } else {
                                    svTagihan!!.visibility = View.VISIBLE
                                    rlGagal!!.visibility = View.GONE

                                    var max = -1
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)

                                        if (jsonObject.getInt("id") > max){  //Filter the highest id
                                            max = jsonObject.getInt("id")
                                            Log.d(TAG, "onResponse: max id $max")
                                            dataPinjaman.id = jsonObject.getInt("id")
                                            dataPinjaman.nipBaru = jsonObject.getString("nipBaru")
                                            dataPinjaman.pinjaman = jsonObject.getInt("pinjaman")
                                            dataPinjaman.lamaPinjaman = jsonObject.getInt("lamaPinjaman")
                                            dataPinjaman.bungaPertahun = jsonObject.getInt("bungaPertahun")
                                            dataPinjaman.bungaPersen = jsonObject.getDouble("bungaPersen")
                                            dataPinjaman.bungaRupiah = jsonObject.getDouble("bungaRupiah")
                                            dataPinjaman.administrasiRupiah = jsonObject.getDouble("administrasiRupiah")
                                            dataPinjaman.angsuranPerbulan = jsonObject.getDouble("angsuranPerbulan")
                                            dataPinjaman.asuransiRupiah = jsonObject.getDouble("asuransiRupiah")
                                            dataPinjaman.transferRupiah = jsonObject.getDouble("transferRupiah")
                                            dataPinjaman.diterimaRupiah = jsonObject.getDouble("diterimaRupiah")
                                            dataPinjaman.registrasi = jsonObject.getString("registrasi")
                                            dataPinjaman.notif_registrasi = jsonObject.getString("notif_registrasi")
                                            dataPinjaman.info_registrasi = jsonObject.getString("info_registrasi")
                                            dataPinjaman.activation = jsonObject.getString("activation")
                                            dataPinjaman.notif_activation = jsonObject.getString("notif_activation")
                                            dataPinjaman.pdf_send = jsonObject.getString("pdf_send")
                                            dataPinjaman.result_document = jsonObject.getString("result_document")
                                            dataPinjaman.sign_document = jsonObject.getString("sign_document")
                                            dataPinjaman.status_document = jsonObject.getString("status_document")
                                            idPinjaman = jsonObject.getInt("id")
                                            documentId = jsonObject.getString("document_id")
                                            pdfSend = jsonObject.getString("pdf_send")

                                            when (jsonObject.getInt("status")) {
                                                1 -> {
                                                    statusPinjamanTv!!.text = "Pengajuan"
                                                    v.bPkD.visibility = View.GONE
                                                }
                                                2 -> {
                                                    statusPinjamanTv!!.text = "Disetujui"
                                                    v.bPkD.visibility = View.VISIBLE
                                                    if (jsonObject.getString("registrasi") == "00"){
                                                        /*mToast(mContext, jsonObject.getString("notif_registrasi"))
                                                        webView(jsonObject.getString("info_registrasi"), true)*/

                                                        regis = true
                                                        if (jsonObject.getString("activation") == "00"){
                                                            activation = true

                                                            if (jsonObject.getString("result_document") == "00"){
                                                                resultDocument = true
                                                                v.bRegisAktifasiDigisign.setBackgroundResource(R.drawable.button_custom_design_grey)
                                                                v.bTtd.setBackgroundResource(R.drawable.button_custom_design_grey)

                                                            } else {
                                                                v.bRegisAktifasiDigisign.setBackgroundResource(R.drawable.button_custom_design_grey)
                                                                v.bTtd.setBackgroundResource(R.drawable.button_custom_design_darkblue)
                                                            }

                                                        } else {
                                                            activation = false
                                                            v.bRegisAktifasiDigisign.setBackgroundResource(R.drawable.button_custom_design_darkblue)
                                                            v.bTtd.setBackgroundResource(R.drawable.button_custom_design_grey)
                                                        }

                                                    } else {
                                                        regis = false
                                                        v.bRegisAktifasiDigisign.setBackgroundResource(R.drawable.button_custom_design_darkblue)
                                                        v.bTtd.setBackgroundResource(R.drawable.button_custom_design_grey)
                                                    }
                                                    Log.d(TAG, "onResponse Registrasi: ${jsonObject.getString("registrasi")}")
                                                    Log.d(TAG, "onResponse Activation: ${jsonObject.getString("activation")}")
                                                    Log.d(TAG, "onResponse result_document: ${jsonObject.getString("result_document")}")
                                                    Log.d(TAG, "onResponse Result: $resultDocument")
                                                }
                                                3 -> {
                                                    statusPinjamanTv!!.text = "Ditolak"
                                                    v.bPkD.visibility = View.GONE
                                                }
                                                4 -> {
                                                    statusPinjamanTv!!.text = "Telah ditransfer"
                                                    v.bPkD.visibility = View.GONE
                                                }
                                                5 -> {
                                                    statusPinjamanTv!!.text = "Kredit berjalan"
                                                    v.bPkD.visibility = View.GONE
                                                }
                                                6 -> {
                                                    statusPinjamanTv!!.text = "Kredit Lunas"
                                                    v.bPkD.visibility = View.GONE
                                                }
                                                else -> {
                                                    statusPinjamanTv!!.text = "!!Dalam Proses Pengembangan!!"
                                                    v.bPkD.visibility = View.GONE
                                                }
                                            }
                                            dataPinjaman.status = jsonObject.getInt("status")
                                            dataPinjaman.tglPengajuan = jsonObject.getString("tglPengajuan")
                                            dataPinjaman.tujuanPinjaman = jsonObject.getString("tujuanPinjaman")
                                            dataPinjaman.tgl_mulai_pinjaman = jsonObject.getString("tgl_mulai_pinjaman")
                                            dataPinjaman.tgl_akhir_pinjaman = jsonObject.getString("tgl_akhir_pinjaman")
                                            dataPinjaman.tgl_lunas = jsonObject.getString("tgl_lunas")
                                            dataPinjaman.nopk = jsonObject.getString("nopk")
                                        }
                                        Log.d(TAG, "onResponse: ${dataPinjaman.id}")

                                    }

                                    list.add(dataPinjaman)
                                    Log.d("Data Pinjaman", "onResponse: $list")

                                    pinjamanTv!!.text = formatRp!!.format(dataPinjaman.pinjaman)
                                    tenorPinjamanTv!!.text = "${dataPinjaman.lamaPinjaman} Bulan"
                                    bungaTv!!.text = formatRp!!.format(dataPinjaman.bungaRupiah)
                                    angsuranPerbulanTv!!.text = formatRp!!.format(dataPinjaman.angsuranPerbulan)
                                    asuransiTv!!.text = formatRp!!.format(dataPinjaman.asuransiRupiah)
                                    adminTv!!.text = formatRp!!.format(dataPinjaman.administrasiRupiah)
                                    jumlahTerimaTv!!.text = formatRp!!.format(dataPinjaman.diterimaRupiah)
                                    transferBankTv!!.text = formatRp!!.format(6500)
                                    tglPengajuanTv!!.text = dataPinjaman.tglPengajuan


                                }
                            }
                        } catch (e: JSONException) {
                            dProgress.dismiss()
                            rlGagal!!.visibility = View.VISIBLE
                            svTagihan!!.visibility = View.GONE
                            tvGagal!!.text = "Mohon maaf, koneksi gagal."
                            e.printStackTrace()
                        } catch (e: IOException) {
                            dProgress.dismiss()
                            rlGagal!!.visibility = View.VISIBLE
                            svTagihan!!.visibility = View.GONE
                            tvGagal!!.text = "Mohon maaf, koneksi gagal."
                            e.printStackTrace()
                        }
                    } else {
                        dProgress.dismiss()
                        rlGagal!!.visibility = View.VISIBLE
                        svTagihan!!.visibility = View.GONE
                        tvGagal!!.text = "Mohon maaf koneksi gagal, silahkan cek koneksi anda"
                        Toast.makeText(mContext, "Mohon maaf server tidak terjangkau", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    dProgress.dismiss()
                    rlGagal!!.visibility = View.VISIBLE
                    svTagihan!!.visibility = View.GONE
                    tvGagal!!.text = "Mohon maaf koneksi gagal, silahkan cek koneksi anda"
                    Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    private val getPinjaman: Unit
        get() {
            mProgress(dProgress)
            val nip = prefManager!!.nip
            val status = RetrofitClient.client.create(StatusPinjamanInterface::class.java)
            val call = status.getPinjaman(nip)
            call.enqueue(object : Callback<ResponseBody> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        try {
                            dProgress.dismiss()
                            val obj = JSONObject(response.body()!!.string())
                            val cek = obj.getBoolean("status")
                            if (cek) {
                                val data = obj.getString("data")
                                val jsonArray = JSONArray(data)
                                if (jsonArray.length() == 0) {

                                } else {
                                    var max = -1
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
                                        val dp = HistoryData()
                                        dp.id = jsonObject.getInt("id")
                                        dp.nipBaru = jsonObject.getString("nipBaru")
                                        dp.pinjaman = jsonObject.getInt("pinjaman")
                                        dp.lamaPinjaman = jsonObject.getInt("lamaPinjaman")
                                        dp.bungaPertahun = jsonObject.getInt("bungaPertahun")
                                        dp.bungaPersen = jsonObject.getDouble("bungaPersen")
                                        dp.bungaRupiah = jsonObject.getDouble("bungaRupiah")
                                        dp.administrasiRupiah = jsonObject.getDouble("administrasiRupiah")
                                        dp.angsuranPerbulan = jsonObject.getDouble("angsuranPerbulan")
                                        dp.asuransiRupiah = jsonObject.getDouble("asuransiRupiah")
                                        dp.transferRupiah = jsonObject.getDouble("transferRupiah")
                                        dp.diterimaRupiah = jsonObject.getDouble("diterimaRupiah")
                                        var status = ""
                                        when(jsonObject.getInt("status")){
                                            1 -> status = "Pengajuan"
                                            2 -> status = "Disetujui"
                                            3 -> status = "Ditolak"
                                            4 -> status = "Ditransfer"
                                            5 -> status = "Kredit berjalan"
                                            6 -> status = "Kredit Lunas"
                                            else -> "!!Dalam Proses Pengembangan!!"
                                        }
//                                        dp.status = status
                                        val local = Locale("in", "ID")
                                        val sdf = SimpleDateFormat("M/d/yyyy, HH:mm:ss a", local)
                                        val d = sdf.parse(jsonObject.getString("tglPengajuan"))
                                        sdf.applyPattern("d/MM/yyyy")
                                        dp.tglPengajuan = sdf.format(d!!)
                                        dp.tujuanPinjaman = jsonObject.getString("tujuanPinjaman")
                                        dp.tgl_mulai_pinjaman = jsonObject.getString("tgl_mulai_pinjaman")
                                        dp.tgl_akhir_pinjaman = jsonObject.getString("tgl_akhir_pinjaman")
                                        dp.tgl_lunas = jsonObject.getString("tgl_lunas")
                                        dp.nopk = jsonObject.getString("nopk")
                                        listPinjaman.add(dp)

                                    }
                                    pinjamanAdapter.filter(listPinjaman)
                                    mDialog.rvHistoryPinjaman.adapter = pinjamanAdapter
                                    pinjamanAdapter.notifyDataSetChanged()
                                    Log.d("List Pinjaman", "onResponse: $listPinjaman")

                                }
                            }
                        } catch (e: JSONException) {
                            dProgress.dismiss()

                            e.printStackTrace()
                        } catch (e: IOException) {
                            dProgress.dismiss()

                            e.printStackTrace()
                        }
                    } else {
                        dProgress.dismiss()

                        Toast.makeText(mContext, "Mohon maaf server tidak terjangkau", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    dProgress.dismiss()

                    Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

    private val signPinjaman: Unit
        get() {
            val nip = prefManager!!.nip
            val status = RetrofitClient.client.create(StatusPinjamanInterface::class.java)
            val call = status.getPinjaman(nip)
            call.enqueue(object : Callback<ResponseBody> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            val cek = obj.getBoolean("status")
                            if (cek) {
                                val data = obj.getString("data")
                                val jsonArray = JSONArray(data)
                                if (jsonArray.length() == 0) {
                                    mToast(mContext, obj.getString("message"))
                                } else {
                                    var max = -1
                                    var idResult = 0
                                    var resultSign = ""
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
                                        if (jsonObject.getInt("id") > max){
                                            max = jsonObject.getInt("id")
                                            Log.d(TAG, "onResponse: max id $max")
                                            sign.id = jsonObject.getInt("id")
                                            sign.registrasi = jsonObject.getString("registrasi")
                                            sign.notif_registrasi = jsonObject.getString("notif_registrasi")
                                            sign.info_registrasi = jsonObject.getString("info_registrasi")
                                            sign.activation = jsonObject.getString("activation")
                                            sign.notif_activation = jsonObject.getString("notif_activation")
                                            sign.pdf_send = jsonObject.getString("pdf_send")
                                            sign.result_document = jsonObject.getString("result_document")
                                            sign.sign_document = jsonObject.getString("sign_document")
                                            sign.status_document = jsonObject.getString("status_document")

                                            val result = jsonObject.getString("result_document") == "00"
                                            Log.d(TAG, "onResponse: result = $result >>>>> id ${jsonObject.getString("id")}")
                                        }
                                    }
                                }
                                listResult.add(sign)
                                Log.d("Data Pinjaman", "onResponse: $listResult")
                                if (sign.result_document == "00"){
                                    dWeb.dismiss()
                                    v.bTtd.setBackgroundResource(R.drawable.button_custom_design_grey)
                                    Log.d(TAG, "onResponse: >>>>>>>>>>>>>>>>>>>>>>> STOP HANDLER")
                                    handler.removeCallbacks(runnableCode!!)
                                } else {
                                    if (sign.activation == "00"){
                                        if(ra){
                                            ra = false
                                            handler.removeCallbacks(runnableActivation!!)
                                        }

                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            Log.e(TAG, "onResponse: ${e.message}")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            Log.e(TAG, "onResponse: ${e.message}")
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(mContext, "Mohon maaf server tidak terjangkau", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    /*private fun filter(new: MutableList<HistoryData>){
        list = new.sortedBy { it.id } as MutableList<HistoryData>
    }*/

    @SuppressLint("SetJavaScriptEnabled")
    private fun webView(link: String?, boolean : Boolean) {
        dWeb.setContentView(R.layout.d_webview)
        dWeb.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dWeb.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        dWeb.window!!.attributes.windowAnimations = R.style.Animation_Design_BottomSheetDialog
        dWeb.show()
        dWeb.toolbarWvDigisign.title = ""
        dWeb.toolbarWvDigisign.setNavigationIcon(R.drawable.ic_back_white)
        dWeb.toolbarWvDigisign.setNavigationOnClickListener {
            if (ra){
                pinjaman
                dWeb.dismiss()
                handler.removeCallbacks(runnableActivation!!)
                ra = false
            } else {
                pinjaman
                handler.removeCallbacks(runnableCode!!)
                dWeb.dismiss()
            }

        }

        if (boolean){
            val url = link
            Log.d("Activation WEB VIEW",url!!)
            try {
                val web = dWeb.id_webview_digisign
                web.webViewClient = WebViewClient()
                web.settings.loadWithOverviewMode = true
                web.settings.useWideViewPort = true
                web.settings.builtInZoomControls = true
                web.settings.javaScriptEnabled = true
                web.loadUrl(url)
            } catch (e : IOException){
                e.printStackTrace()
            } catch (e : JSONException){
                e.printStackTrace()
            }
        } else {
            val url = link
            Log.d("REGISTRATION WEB VIEW","$url")
            try {
                val web = dWeb.id_webview_digisign
                web.webViewClient = WebViewClient()
                web.settings.loadWithOverviewMode = true
                web.settings.useWideViewPort = true
                web.settings.builtInZoomControls = true
                web.settings.javaScriptEnabled = true
                web.loadUrl(url)
            }catch (e: IOException){
                e.printStackTrace()
            } catch (e : JSONException){
                e.printStackTrace()
            }
        }
    }

    override fun digiResponse(response: RDigisign) {
        if (response.data!!.isNotEmpty()){
            if (response.data[0].result == "00"){
                if (response.data[0].result_activation == "00"){
                    if (response.data[0].result_tandatangan == "00"){
                        dProgress.dismiss()
                        mToast(mContext,"Status Ttd: ${response.data[0].status_tandatangan}")
                    } else {
                        dProgress.dismiss()
                        Log.d(TAG, "digiResponse: $documentId")
                        getSignDocument(response.data[0].email.toString(), documentId)
                        /*digisignPrestImp.sign(response.data[0].email.toString()
                                ,response.data[0].document_id.toString())*/
                    }
                    dProgress.dismiss()

                } else {
                    dProgress.dismiss()
                    ra = true
                    runnableActivation = object : Runnable {
                        override fun run() {
                            try {
                                ra = true
                                Log.d(TAG, "looping run: >>>>>>>>>>>>>>>>>>>>>>>>>> $activation")
                                signPinjaman
                                handler.postDelayed(this, 3000)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                ra = false
                            }

                        }
                    }
                    handler.post(runnableActivation!!)
                    /*digisignPrestImp.data(SharedPrefManager.getInstance(mContext).user.nip
                            ,SharedPrefManager.getInstance(mContext).user.email, idPinjaman)*/

                    webView(response.data[0].info, true)
                   /* val i = Intent(mContext, ECommerceActivity::class.java)
                    i.putExtra("fragment", "digisign")
                    i.putExtra("activity", "kilat")
                    startActivity(i)*/
                }
                dProgress.dismiss()
            } else {
                dProgress.dismiss()
                Log.d("Digisign", "Belum Teraktivasi")
                val i = Intent(mContext, ECommerceActivity::class.java)
                i.putExtra("fragment", "digisign")
                i.putExtra("idPinjaman", idPinjaman)
                startActivity(i)
            }
        } else {
//            mToast(this,"Belum Teraktivasi")
            dProgress.dismiss()
            Log.d("Digisign", "Belum Teraktivasi")
            val i = Intent(mContext, ECommerceActivity::class.java)
            i.putExtra("fragment", "digisign")
            i.putExtra("idPinjaman", idPinjaman)
            startActivity(i)

        }
    }

    override fun digiError(error: String) {
        dProgress.dismiss()
        Log.e("Error",error)
    }

    private fun getSignDocument(email:String, documentId:String){
        val api = HttpRetrofitClient
        api.retrofit.signDocumentDigisign(email, documentId).enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                dProgress.dismiss()
                Log.e("Error", "${t.message}")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val jsonObject = JSONObject(response.body()!!.string())
                    if (jsonObject.getBoolean("status")){
                        dProgress.dismiss()
                        Log.d(TAG, "onResponse: ${jsonObject.getString("message")}")
                        val jsonData = JSONObject(jsonObject.getString("data"))
                        resultSign = jsonData.getString("result")
                        if (resultSign == "00"){
                            dProgress.dismiss()
                            webView(jsonData.getString("link"), true)
                        } else {
//                            handler.removeCallbacks(runnableCode!!)
                            dProgress.dismiss()
                            notif = jsonData.getString("notif")
                            sBar(v, "$notif : $resultSign")
                            if (resultSign == "05"){
                                handler.removeCallbacks(runnableCode!!)
                                Log.d(TAG, "onResponse: handler >>>>>>>>>>>>>>>>>>> STOP")
                            }
                        }
                    } else {
                        dProgress.dismiss()
                        Log.d(TAG, "onResponse: ${jsonObject.getString("message")}")
                        mToast(mContext, jsonObject.getString("message"))
                    }
                } else {
                    dProgress.dismiss()
                    mToast(mContext, response.message())
                    Log.d(TAG, "onResponse: ${response.message()}")
                }
            }

        })
    }

    /*override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.actionRiwayatPinjaman -> {
                getHistoryPinjaman()
                return true
            }

        }
        return false
    }*/
}
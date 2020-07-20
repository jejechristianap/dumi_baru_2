package com.minjem.dumi.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.mdi.stockin.ApiHelper.RecyclerItemClickListener
import com.minjem.dumi.PerjanjianKreditView
import com.minjem.dumi.R
import com.minjem.dumi.adapter.HistoryPinjamanAdapter
import com.minjem.dumi.api.StatusPinjamanInterface
import com.minjem.dumi.model.DataPinjaman
import com.minjem.dumi.model.RecyclerViewAdapter
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.model.User
import com.minjem.dumi.retrofit.RetrofitClient
import com.minjem.dumi.util.CustomProgressDialog
import kotlinx.android.synthetic.main.ecommerce_flight_cityairport.*
import kotlinx.android.synthetic.main.fragment_pinjaman.*
import kotlinx.android.synthetic.main.fragment_pinjaman.view.*
import kotlinx.android.synthetic.main.history_pinjaman.*
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

class PinjamanFragment : Fragment() {
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
    private var tglPengajuan = ""
    private var tujuan = ""
    private var bungaRupiah = 0.0
    private var angsuran = 0.0
    private lateinit var mContext: Context
    private var svTagihan: ScrollView? = null
    private var rlGagal: RelativeLayout? = null
    lateinit var v: View
    private val progressDialog = CustomProgressDialog()
    var list : MutableList<DataPinjaman> = ArrayList()
    var listPinjaman : MutableList<DataPinjaman> = ArrayList()
    private val TAG = PinjamanFragment::class.qualifiedName
    private val dataPinjaman = DataPinjaman()
    lateinit var mDialog: Dialog
    lateinit var pinjamanAdapter: HistoryPinjamanAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_pinjaman, container, false)
        mContext = this.activity!!
        mDialog = Dialog(mContext, R.style.DialogTheme)


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
       v.pkButton.setOnClickListener{
            val i = Intent(activity, PerjanjianKreditView::class.java)
            i.putExtra("tanggal", dataPinjaman.tglPengajuan)
            i.putExtra("pinjaman", tvPinjaman.text.toString())
            i.putExtra("bunga", dataPinjaman.bungaRupiah)
            i.putExtra("angsuran", dataPinjaman.angsuranPerbulan)
            i.putExtra("tujuan", dataPinjaman.tujuanPinjaman)
            i.putExtra("tenor", tenor_pinjaman_bulan.text.toString())
            startActivity(i)
        }

        v.srlTagihan.setOnRefreshListener {
            srlTagihan.isRefreshing = true
            refreshList()
        }

        v.ivHistoryPinjaman.setOnClickListener {
            getHistoryPinjaman()
        }
    }

    private fun getHistoryPinjaman(){
        listPinjaman.clear()
        mDialog.setContentView(R.layout.history_pinjaman)
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
            progressDialog.show(mContext!!, "Loading...")
            val nip = prefManager!!.nip
            val status = RetrofitClient.getClient().create(StatusPinjamanInterface::class.java)
            val call = status.getPinjaman(nip)
            call.enqueue(object : Callback<ResponseBody> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        try {
                            progressDialog.dialog.dismiss()
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
                                            dataPinjaman.bungaRupiah = jsonObject.getInt("bungaRupiah")
                                            dataPinjaman.administrasiRupiah = jsonObject.getInt("administrasiRupiah")
                                            dataPinjaman.angsuranPerbulan = jsonObject.getDouble("angsuranPerbulan")
                                            dataPinjaman.asuransiRupiah = jsonObject.getInt("asuransiRupiah")
                                            dataPinjaman.transferRupiah = jsonObject.getInt("transferRupiah")
                                            dataPinjaman.diterimaRupiah = jsonObject.getInt("diterimaRupiah")

                                            when (jsonObject.getInt("status")) {
                                                1 -> {
                                                    statusPinjamanTv!!.text = "Pengajuan"
                                                    v.pkButton.visibility = View.GONE
                                                }
                                                2 -> {
                                                    statusPinjamanTv!!.text = "Disetujui"
                                                    v.pkButton.visibility = View.VISIBLE
                                                }
                                                3 -> {
                                                    statusPinjamanTv!!.text = "Ditolak"
                                                    v.pkButton.visibility = View.GONE
                                                }
                                                4 -> {
                                                    statusPinjamanTv!!.text = "Telah ditransfer"
                                                    v.pkButton.visibility = View.GONE
                                                }
                                                5 -> {
                                                    statusPinjamanTv!!.text = "Kredit berjalan"
                                                    v.pkButton.visibility = View.GONE
                                                }
                                                6 -> {
                                                    statusPinjamanTv!!.text = "Kredit Lunas"
                                                    v.pkButton.visibility = View.GONE
                                                }
                                                else -> {
                                                    statusPinjamanTv!!.text = "!!Dalam Proses Pengembangan!!"
                                                    v.pkButton.visibility = View.GONE
                                                }
                                            }
                                            dataPinjaman.status = jsonObject.getInt("status").toString()
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
                            progressDialog.dialog.dismiss()
                            rlGagal!!.visibility = View.VISIBLE
                            svTagihan!!.visibility = View.GONE
                            tvGagal!!.text = "Mohon maaf, koneksi gagal."
                            e.printStackTrace()
                        } catch (e: IOException) {
                            progressDialog.dialog.dismiss()
                            rlGagal!!.visibility = View.VISIBLE
                            svTagihan!!.visibility = View.GONE
                            tvGagal!!.text = "Mohon maaf, koneksi gagal."
                            e.printStackTrace()
                        }
                    } else {
                        progressDialog.dialog.dismiss()
                        rlGagal!!.visibility = View.VISIBLE
                        svTagihan!!.visibility = View.GONE
                        tvGagal!!.text = "Mohon maaf koneksi gagal, silahkan cek koneksi anda"
                        Toast.makeText(mContext, "Mohon maaf server tidak terjangkau", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    progressDialog.dialog.dismiss()
                    rlGagal!!.visibility = View.VISIBLE
                    svTagihan!!.visibility = View.GONE
                    tvGagal!!.text = "Mohon maaf koneksi gagal, silahkan cek koneksi anda"
                    Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    private val getPinjaman: Unit
        get() {
            progressDialog.show(mContext, "Loading...")
            val nip = prefManager!!.nip
            val status = RetrofitClient.getClient().create(StatusPinjamanInterface::class.java)
            val call = status.getPinjaman(nip)
            call.enqueue(object : Callback<ResponseBody> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        try {
                            progressDialog.dialog.dismiss()
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
                                        val dp = DataPinjaman()
                                        dp.id = jsonObject.getInt("id")
                                        dp.nipBaru = jsonObject.getString("nipBaru")
                                        dp.pinjaman = jsonObject.getInt("pinjaman")
                                        dp.lamaPinjaman = jsonObject.getInt("lamaPinjaman")
                                        dp.bungaPertahun = jsonObject.getInt("bungaPertahun")
                                        dp.bungaPersen = jsonObject.getDouble("bungaPersen")
                                        dp.bungaRupiah = jsonObject.getInt("bungaRupiah")
                                        dp.administrasiRupiah = jsonObject.getInt("administrasiRupiah")
                                        dp.angsuranPerbulan = jsonObject.getDouble("angsuranPerbulan")
                                        dp.asuransiRupiah = jsonObject.getInt("asuransiRupiah")
                                        dp.transferRupiah = jsonObject.getInt("transferRupiah")
                                        dp.diterimaRupiah = jsonObject.getInt("diterimaRupiah")
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
                                        dp.status = status
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
                            progressDialog.dialog.dismiss()

                            e.printStackTrace()
                        } catch (e: IOException) {
                            progressDialog.dialog.dismiss()

                            e.printStackTrace()
                        }
                    } else {
                        progressDialog.dialog.dismiss()

                        Toast.makeText(mContext, "Mohon maaf server tidak terjangkau", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    progressDialog.dialog.dismiss()

                    Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    private fun filter(new: MutableList<DataPinjaman>){
        list = new.sortedBy { it.id } as MutableList<DataPinjaman>

    }
}
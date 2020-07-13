package com.minjem.dumi.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.minjem.dumi.PerjanjianKreditView
import com.minjem.dumi.R
import com.minjem.dumi.api.StatusPinjamanInterface
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.model.User
import com.minjem.dumi.retrofit.RetrofitClient
import com.minjem.dumi.util.CustomProgressDialog
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
import java.util.*

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
    private var mContext: Context? = null
    private var svTagihan: ScrollView? = null
    private var rlGagal: RelativeLayout? = null
    lateinit var v: View
    private val progressDialog = CustomProgressDialog()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_pinjaman, container, false)
        mContext = this.activity
        initView()
        initOnTouch()
        pinjaman

        return v
    }

    private fun refreshList(){
        srlTagihan.isRefreshing = false
        pinjaman
    }

    private fun initOnTouch(){
       v.pkButton.setOnClickListener{
            val i = Intent(activity, PerjanjianKreditView::class.java)
            i.putExtra("tanggal", tglPengajuan)
            i.putExtra("pinjaman", tvPinjaman.text.toString())
            i.putExtra("bunga", bungaRupiah)
            i.putExtra("angsuran", angsuran)
            i.putExtra("tujuan", tujuan)
            i.putExtra("tenor", tenor_pinjaman_bulan.text.toString())
            startActivity(i)
        }

        v.srlTagihan.setOnRefreshListener {
            srlTagihan.isRefreshing = true
            refreshList()
        }
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
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
                                        val statusId = jsonObject.getInt("status")
                                        val pinjaman = jsonObject.getDouble("pinjaman")
                                        var lamaPinjaman = jsonObject.getString("lamaPinjaman")
                                        bungaRupiah = jsonObject.getDouble("bungaRupiah")
                                        angsuran = jsonObject.getDouble("angsuranPerbulan")
                                        val asuransi = jsonObject.getDouble("asuransiRupiah")
                                        val adminRupiah = jsonObject.getDouble("administrasiRupiah")
                                        val diterima = jsonObject.getDouble("diterimaRupiah")
                                        tglPengajuan = jsonObject.getString("tglPengajuan")
                                        tujuan = jsonObject.getString("tujuanPinjaman")
                                        when (statusId) {
                                            1 -> {
                                                statusPinjamanTv!!.text = "Pengajuan"
                                                v.pkButton.visibility = View.GONE
                                            }
                                            2 -> {
                                                statusPinjamanTv!!.text = "Disetujui"
                                                v.pkButton.visibility = View.VISIBLE
                                            }
                                            3 -> {
                                                statusPinjamanTv!!.text = "Pengajuan ditolak"
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
                                        lamaPinjaman += " Bulan"
                                        pinjamanTv!!.text = formatRp!!.format(pinjaman)
                                        tenorPinjamanTv!!.text = lamaPinjaman
                                        bungaTv!!.text = formatRp!!.format(bungaRupiah)
                                        angsuranPerbulanTv!!.text = formatRp!!.format(angsuran)
                                        asuransiTv!!.text = formatRp!!.format(asuransi)
                                        adminTv!!.text = formatRp!!.format(adminRupiah)
                                        jumlahTerimaTv!!.text = formatRp!!.format(diterima)
                                        transferBankTv!!.text = formatRp!!.format(6500)
                                        tglPengajuanTv!!.text = tglPengajuan
                                    }
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
}
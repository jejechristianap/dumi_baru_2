package com.minjem.dumi.ecommerce.transaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.minjem.dumi.CustomProgressDialog
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.adapter.RiwayatECommAdapter
import com.minjem.dumi.ecommerce.Helper.PASSWORD
import com.minjem.dumi.ecommerce.Helper.USERNAME
import com.minjem.dumi.ecommerce.api.HttpRetrofitClient
import com.minjem.dumi.ecommerce.response.RiwayatPPOBData
import com.minjem.dumi.model.SharedPrefManager
import kotlinx.android.synthetic.main.activity_token_transaction.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatView : AppCompatActivity() {
    lateinit var riwayatAdapter: RiwayatECommAdapter
    private val list: MutableList<RiwayatPPOBData> = ArrayList()
    private val progressDialog = CustomProgressDialog()
    lateinit var api: HttpRetrofitClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token_transaction)
        api = HttpRetrofitClient
        initView()
        initOnClick()
        getRiwayat()
    }

    private fun initView(){
        noInvoiceTv.text = intent.getStringExtra("invoice")
        noMeterTv.text = intent.getStringExtra("meter")
        namaTv.text = intent.getStringExtra("nama")
        tarifDayaTv.text = intent.getStringExtra("tarifdaya")
        bayarTv.text = intent.getStringExtra("bayar")
        materaiTv.text = intent.getStringExtra("materai")
        ppnTv.text = intent.getStringExtra("ppn")
        ppjTv.text = intent.getStringExtra("ppj")
        angsuranTv.text = intent.getStringExtra("angsuran")
        noTokenTransactionTv.text = intent.getStringExtra("token")
        informasiTv.text = intent.getStringExtra("informasi")

        riwayatRv.layoutManager = LinearLayoutManager(this)
        riwayatAdapter = RiwayatECommAdapter(this, list)
        riwayatRv.adapter = riwayatAdapter
        riwayatAdapter.notifyDataSetChanged()
    }

    private fun initOnClick(){
        backTransaksiIv.setOnClickListener {
            finish()
        }
    }

    private fun getRiwayat(){
        progressDialog.show(this, "Memuat data...")
        val idNasabah = SharedPrefManager.getInstance(this).user.id
        api.retrofit.getRiwayat(USERNAME, PASSWORD, idNasabah)
                .enqueue(object: Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("Error", t.message!!)
                        progressDialog.dialog.dismiss()
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful){
                            val jsonObject = JSONObject(response.body()!!.string())
                            Log.d("Riwayat Status", jsonObject.getString("message"))

                            if (jsonObject.getBoolean("status")){
                                val jsonArray = JSONArray(jsonObject.getString("data"))
                                if (jsonArray.length() != 0){
                                    for (i in 0 until jsonArray.length()){
                                        val tipe = jsonArray.getJSONObject(i).getString("tipe")
                                        val operator = jsonArray.getJSONObject(i).getString("operator")
                                        val no_tujuan = jsonArray.getJSONObject(i).getString("no_tujuan")
                                        val harga = jsonArray.getJSONObject(i).getString("harga")
                                        val invoice = jsonArray.getJSONObject(i).getString("invoice")
                                        val ppob_namapelanggan = jsonArray.getJSONObject(i).getString("ppob_namapelanggan")
                                        val ppob_nomorpelanggan = jsonArray.getJSONObject(i).getString("ppob_nomorpelanggan")
                                        val ppob_stroomtoken = jsonArray.getJSONObject(i).getString("ppob_stroomtoken")
                                        val ppob_tarifdaya = jsonArray.getJSONObject(i).getString("ppob_tarifdaya")
                                        val ppob_totalbayar = jsonArray.getJSONObject(i).getString("ppob_totalbayar")
                                        val created_at = jsonArray.getJSONObject(i).getString("created_at")

                                        val riwayat = RiwayatPPOBData()
                                        riwayat.tipe = tipe
                                        riwayat.operator = operator
                                        riwayat.no_tujuan = no_tujuan
                                        riwayat.harga = harga
                                        riwayat.invoice = invoice
                                        riwayat.ppob_namapelanggan = ppob_namapelanggan
                                        riwayat.ppob_nomorpelanggan = ppob_nomorpelanggan
                                        riwayat.ppob_stroomtoken = ppob_stroomtoken
                                        riwayat.ppob_tarifdaya = ppob_tarifdaya
                                        riwayat.ppob_totalbayar = ppob_totalbayar
                                        riwayat.created_at = created_at
                                        list.add(riwayat)

                                        riwayatAdapter.filter(list)
                                        riwayatRv.adapter = riwayatAdapter
                                        riwayatAdapter.notifyDataSetChanged()
                                        progressDialog.dialog.dismiss()
                                    }
                                } else {
                                    Toast.makeText(this@RiwayatView, "Mohon maaf belum ada transaksi", Toast.LENGTH_LONG).show()
                                    progressDialog.dialog.dismiss()
                                }

                            } else {
                                progressDialog.dialog.dismiss()
                                Log.d("Status riwayat", jsonObject.getString("message"))
                            }
                        } else {
                            progressDialog.dialog.dismiss()
                        }
                    }

                })
    }

}

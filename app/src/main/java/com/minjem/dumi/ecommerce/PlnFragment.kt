package com.minjem.dumi.ecommerce

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.mdi.stockin.ApiHelper.HttpRetrofitClient
import com.mdi.stockin.ApiHelper.RecyclerItemClickListener
import com.minjem.dumi.CustomProgressDialog
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.Adapter.PlnAdapter
import com.minjem.dumi.ecommerce.Helper.PASSWORD
import com.minjem.dumi.ecommerce.Helper.USERNAME
import com.minjem.dumi.ecommerce.response.PlnData
import kotlinx.android.synthetic.main.ecommerce_konfirmasi_pembayaran.*
import kotlinx.android.synthetic.main.ecommerce_pln.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PlnFragment: Fragment() {
    lateinit var mView : View
    lateinit var mContext : Context
    lateinit var api : HttpRetrofitClient
    lateinit var plnAdapter : PlnAdapter
    lateinit var mDialog : Dialog
    private val progressDialog = CustomProgressDialog()
    private var nomorPelanggan : String? = null
    private var namaPelanggan : String? = null
    private var voucher : String? = null
    private var total : String? = null
    private var posItem : Int? = null
    val list : MutableList<PlnData> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = layoutInflater.inflate(R.layout.ecommerce_pln,container,false)
        mContext = this.context!!

        /*Recycler View PLN*/
        mView.rvPln.layoutManager = GridLayoutManager(mContext, 3)
        plnAdapter = PlnAdapter(mContext, list)
        mView.rvPln.adapter = plnAdapter
        plnAdapter.notifyDataSetChanged()
        api = HttpRetrofitClient

        /*Dialog*/
        mDialog = Dialog(mContext)

        getPln()
        rvClick()
        return mView
    }

    private fun getPln(){
        progressDialog.show(mContext, "Loading...")
        api.retrofit.getPln(USERNAME, PASSWORD).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", t.message!!)
                progressDialog.dialog.dismiss()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    try {
                        Log.d("Response", response.body().toString())
                        val jsonArray = JSONArray(response.body()!!.string())
                        Log.d("JsonArray", jsonArray.length().toString())

                        for (i in 0 until jsonArray.length()) {
                            val ppob_grup = jsonArray.getJSONObject(i).getString("ppob_grup").toString()
                            val ppob_produk = jsonArray.getJSONObject(i).getString("ppob_produk").toString()
                            val ppob_kodeproduk = jsonArray.getJSONObject(i).getString("ppob_kodeproduk").toString()
                            var ppob_nominal = jsonArray.getJSONObject(i).getString("ppob_nominal").toString()
                            val ppob_admin = jsonArray.getJSONObject(i).getString("ppob_admin").toString()
                            val ppob_debet = jsonArray.getJSONObject(i).getString("ppob_debet").toString()
                            val ppob_status = jsonArray.getJSONObject(i).getString("ppob_status").toString()
                            ppob_nominal = ppob_nominal.replace(".", "")

                            if (ppob_grup == "PLN Group" && ppob_produk == "PLN Pra Bayar" && ppob_status == "Ready") {
                                val find = list.find { it.ppob_kodeproduk == ppob_kodeproduk && it.ppob_nominal == ppob_nominal }
                                if (find != null) {
                                    if (find.ppob_nominal!!.toInt() < ppob_nominal.toInt()) {
//                                    Log.d("Harga PLN Prabayar", "")
                                        list.forEachIndexed { index, plnData ->
                                            if (plnData.ppob_produk == ppob_produk && plnData.ppob_nominal == ppob_nominal) {
                                                val pln = PlnData()
                                                pln.ppob_grup = ppob_grup
                                                pln.ppob_produk = ppob_produk
                                                pln.ppob_kodeproduk = ppob_kodeproduk
                                                pln.ppob_nominal = ppob_nominal
                                                pln.ppob_admin = ppob_admin
                                                pln.ppob_debet = ppob_debet
                                                pln.ppob_status = ppob_status
                                                list[index] = pln
                                            }
                                        }
                                    }
                                } else {
                                    val pln = PlnData()
                                    pln.ppob_grup = ppob_grup
                                    pln.ppob_produk = ppob_produk
                                    pln.ppob_kodeproduk = ppob_kodeproduk
                                    pln.ppob_nominal = ppob_nominal
                                    pln.ppob_admin = ppob_admin
                                    pln.ppob_debet = ppob_debet
                                    pln.ppob_status = ppob_status
//                                pln.ppob_nominal = pln.ppob_nominal?.replace(".","")
                                    list.add(pln)
                                }
                            }
                            plnAdapter.filter(list)
                            mView.rvPln.adapter = plnAdapter
                            plnAdapter.notifyDataSetChanged()
                            progressDialog.dialog.dismiss()
                            Log.d("Jumlah Filter", list.size.toString())
                        }
                        Log.d("Jumlah PLN", list.size.toString())
                        progressDialog.dialog.dismiss()
                    } catch (e: IOException){
                        e.printStackTrace()
                    } catch (e: JSONException){
                        e.printStackTrace()
                        progressDialog.dialog.dismiss()
                        mView.pln_reload.visibility = View.VISIBLE
                    }
                } else {
                    progressDialog.dialog.dismiss()
                    mView.pln_reload.visibility = View.VISIBLE
                    Log.e("Erorr", "Response gagal")
                }
            }
        })
    }

    private fun rvClick(){
        mView.rvPln.addOnItemTouchListener(RecyclerItemClickListener(mContext, object: RecyclerItemClickListener.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                if(mView.noMeter.text.toString().length > 10){
                    val kode = plnAdapter.list[position].ppob_kodeproduk.toString()
                    cekTagihan(kode)
                    posItem = position
                } else {
                    Toast.makeText(mContext, "Masukkan no meter!", Toast.LENGTH_LONG).show()
                    mView.noMeter.error = "Masukkan no meter!"
                }
            }
        }))
    }

    private fun cekTagihan(ppobKodeproduk: String?) {
        progressDialog.show(mContext, "Memuat...")
        val noPelanggan = mView.noMeter.text.toString()
        Log.d("Test", "Nomor: $noPelanggan, kode: $ppobKodeproduk")

        if (ppobKodeproduk != null) {
            api.retrofit.cekTagihanPln(USERNAME, PASSWORD, ppobKodeproduk, noPelanggan).enqueue(object : Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Error", t.message!!)
                    progressDialog.dialog.dismiss()
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful){
                        try {
                            progressDialog.dialog.dismiss()
//                            rincianTransaksi()
                            Log.d("Response", response.body().toString())
                            val json = JSONObject(response.body()!!.string())
                            namaPelanggan = json.getString("ppob_namapelanggan").toString()
                            nomorPelanggan = "${json.getString("ppob_nomorpelanggan")} | ${json.getString("ppob_tarifdaya")}"
                            voucher = json.getString("ppob_totaltagihan").toString()
                            total = json.getString("ppob_totalbayar").toString()
                            posItem?.let { rincianTransaksi(it) }
                        }catch (e: IOException){
                            e.printStackTrace()
                        } catch (e: JSONException){
                            e.printStackTrace()
                            progressDialog.dialog.dismiss()
                        }
                    } else {
                        progressDialog.dialog.dismiss()
                    }
                }

            })
        }




    }

    private fun rincianTransaksi(pos : Int){
        mDialog.setContentView(R.layout.ecommerce_konfirmasi_pembayaran)
        mDialog.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.setCancelable(false)
        mDialog.show()

        mDialog.id_btn_batalkan.setOnClickListener { mDialog.dismiss() }
        mDialog.id_provider_pulsa.text = namaPelanggan
        mDialog.id_nomor_pulsa.text = nomorPelanggan
        mDialog.id_nominal_pulsa.text = voucher
        mDialog.id_total_pembayaran.text = total
        mDialog.id_biaya_transaksi_pulsa.text = plnAdapter.list[pos].ppob_admin
    }

}
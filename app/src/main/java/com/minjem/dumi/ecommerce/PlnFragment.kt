package com.minjem.dumi.ecommerce

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.mdi.stockin.ApiHelper.HttpRetrofitClient
import com.minjem.dumi.CustomProgressDialog
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.Adapter.PlnAdapter
import com.minjem.dumi.ecommerce.Helper.PASSWORD
import com.minjem.dumi.ecommerce.Helper.USERNAME
import com.minjem.dumi.ecommerce.response.PlnData
import kotlinx.android.synthetic.main.ecommerce_pln.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlnFragment: Fragment() {
    lateinit var mView : View
    lateinit var mContext : Context
    lateinit var api : HttpRetrofitClient
    lateinit var plnAdapter : PlnAdapter
    lateinit var mDialog : Dialog
    private val progressDialog = CustomProgressDialog()
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
//        rvClick()
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
                    Log.d("Response", response.body().toString())
                    val jsonArray = JSONArray(response.body()!!.string())
                    Log.d("JsonArray", jsonArray.length().toString())

                    for (i in 0 until jsonArray.length()){
                        val ppob_grup = jsonArray.getJSONObject(i).getString("ppob_grup").toString()
                        val ppob_produk = jsonArray.getJSONObject(i).getString("ppob_produk").toString()
                        val ppob_kodeproduk = jsonArray.getJSONObject(i).getString("ppob_kodeproduk").toString()
                        var ppob_nominal = jsonArray.getJSONObject(i).getString("ppob_nominal").toString()
                        val ppob_admin = jsonArray.getJSONObject(i).getString("ppob_admin").toString()
                        val ppob_debet = jsonArray.getJSONObject(i).getString("ppob_debet").toString()
                        val ppob_status = jsonArray.getJSONObject(i).getString("ppob_status").toString()
                        ppob_nominal = ppob_nominal.replace(".","")

                        if (ppob_grup == "PLN Group" && ppob_produk == "PLN Pra Bayar" && ppob_status == "Ready"){
                            val find = list.find{ it.ppob_kodeproduk == ppob_kodeproduk && it.ppob_nominal == ppob_nominal}
                            if (find != null){
                                if(find.ppob_nominal!!.toInt() < ppob_nominal.toInt()){
//                                    Log.d("Harga PLN Prabayar", "")
                                    list.forEachIndexed { index, plnData ->
                                        if (plnData.ppob_produk == ppob_produk && plnData.ppob_nominal == ppob_nominal){
                                            val pln = PlnData()
                                            pln.ppob_grup = ppob_grup
                                            pln.ppob_produk = ppob_produk
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

                } else {
                    progressDialog.dialog.dismiss()
                    Log.e("Erorr", "Response gagal")
                }
            }

        })
    }

}
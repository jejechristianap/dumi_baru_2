package com.minjem.dumi.ecommerce

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mdi.stockin.ApiHelper.RecyclerItemClickListener
import com.minjem.dumi.util.CustomProgressDialog
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.Helper.KOLOM
import com.minjem.dumi.ecommerce.adapter.PDAMAdapter
import com.minjem.dumi.ecommerce.Helper.PASSWORD
import com.minjem.dumi.ecommerce.Helper.USERNAME
import com.minjem.dumi.ecommerce.api.HttpRetrofitClient
import com.minjem.dumi.ecommerce.response.PDAMData
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import kotlinx.android.synthetic.main.ecommerce_pdam.view.*
import kotlinx.android.synthetic.main.ecommerce_pdam.view.rvPdam
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PDAMFragment : Fragment() {
    lateinit var mView : View
    lateinit var dView : View
    lateinit var mContext : Context
    lateinit var api : HttpRetrofitClient
    lateinit var pdamAdapter: PDAMAdapter
    val list : MutableList<PDAMData> = ArrayList()
    private val progressDialog = CustomProgressDialog()
    private val TAG = "PDAM"
    private var isClick = false
    lateinit var nama : String
    lateinit var noPel : String
    lateinit var produk : String
    lateinit var jmlTagihan : String
    lateinit var blnTagihan : String
    lateinit var tunggakan : String
    lateinit var admin : String
    lateinit var totalTagihan : String
    lateinit var reason : String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.ecommerce_pdam, container, false)
        mContext = this.context!!

        initView()
        getPdam()
        findKota()
        rvClick()

        return mView
    }

    private fun initView(){
        mView.rvPdam.layoutManager = LinearLayoutManager(mContext)
        pdamAdapter = PDAMAdapter(mContext, list)
        mView.rvPdam.adapter = pdamAdapter
        pdamAdapter.notifyDataSetChanged()
        api = HttpRetrofitClient
        mView.rlListKota.visibility = View.VISIBLE
        mView.rlTagihanPdam.visibility = View.GONE


        mView.ivBackPdam.setOnClickListener {
            if (isClick){
                mView.etPdamIdPel.text.clear()
                mView.rlListKota.visibility = View.VISIBLE
                mView.rlTagihanPdam.visibility = View.GONE
                isClick = false
            } else {
                activity!!.finish()
            }

        }

        mView.ivClose.visibility = View.GONE
        mView.ivClose.setOnClickListener {
            mView.etNomorBill.text.clear()
        }
    }

    private fun rvClick(){
        mView.rvPdam.addOnItemTouchListener(RecyclerItemClickListener(mContext, object: RecyclerItemClickListener.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
//                mToast(mContext, "Touch $position")
                isClick = true
                mView.rlListKota.visibility = View.GONE
                mView.rlTagihanPdam.visibility = View.VISIBLE
                mView.ivDeleteText.visibility = View.GONE
                cekTagihan(position)
            }

        }))
    }

    private fun cekTagihan(position: Int){
        mView.tvKotaTagihan.text = pdamAdapter.list[position].ppob_produk
        mView.tvProdukTagihan.text = pdamAdapter.list[position].ppob_kodeproduk
        mView.ivDeleteText.setOnClickListener {
            mView.etPdamIdPel.text.clear()
            mView.etPdamIdPel.requestFocus()
        }


        mView.etPdamIdPel.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged: $s")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged: $s")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()){
                    mView.ivDeleteText.visibility = View.GONE

                } else {
                    mView.ivDeleteText.visibility = View.VISIBLE
                }
            }

        })

        mView.bGanti.setOnClickListener {
            mView.etPdamIdPel.text.clear()
            mView.rlListKota.visibility = View.VISIBLE
            mView.rlTagihanPdam.visibility = View.GONE
        }

        mView.bLanjutPdam.setOnClickListener {
            if (TextUtils.isEmpty(mView.etPdamIdPel.text.toString())){
                mView.etPdamIdPel.error = KOLOM
                mView.etPdamIdPel.requestFocus()
                return@setOnClickListener
            }
            getTagihan(position)
        }

    }

    private fun getTagihan(position: Int){
        progressDialog.show(mContext, "Loading...")
        api.retrofit.cekTagihanPPOB(USERNAME, PASSWORD,
                pdamAdapter.list[position].ppob_kodeproduk.toString(), mView.etPdamIdPel.text.toString())
                .enqueue(object : Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("Error", t.message!!)
                        dView.rlGagal.visibility = View.VISIBLE
                        dView.rlBerhasil.visibility = View.GONE
                        progressDialog.dialog.dismiss()
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful){
                            try {
                                val jsonObject = JSONObject(response.body()!!.string())
                                if (jsonObject.getBoolean("status")){
                                    Log.d(TAG, "onResponse: ${jsonObject.getString("message")}")
                                    val jsonData = JSONObject(jsonObject.getString("data"))
                                    when(jsonData.getString("result")){
                                        "no" -> {

                                            reason = jsonData.getString("reason")
                                            showBottomSheet(false)
                                            progressDialog.dialog.dismiss()
                                        }
                                        "ok" -> {

                                            progressDialog.dialog.dismiss()

                                            nama = jsonData.getString("ppob_namapelanggan")
                                            noPel = jsonData.getString("ppob_nomorpelanggan")
                                            produk = jsonData.getString("ppob_kodeproduk")
                                            jmlTagihan = "Rp${jsonData.getString("ppob_totaltagihan")}"
                                            blnTagihan = jsonData.getString("ppob_bulantagihan")
                                            tunggakan = jsonData.getString("ppob_jumlahtunggakan")
                                            admin = "Rp${jsonData.getString("ppob_admin")}"
                                            totalTagihan = "Rp${jsonData.getString("ppob_totalbayar")}"
                                            showBottomSheet(true)
                                        }

                                    }
                                }

                            }catch (e: IOException){
                                e.printStackTrace()
                            } catch (e: JSONException){
                                e.printStackTrace()
                                progressDialog.dialog.dismiss()
                                showBottomSheet(false)
                            }
                        } else {
                            progressDialog.dialog.dismiss()
                            showBottomSheet(false)
                        }
                    }
                })
    }

    @SuppressLint("SimpleDateFormat")
    private fun showBottomSheet(berhasil: Boolean){
        dView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        val dialog = BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme)
//        dialog.setCancelable(false)
        dialog.setContentView(dView)

        if (berhasil){
            if (tunggakan == ""){
                dView.tvTunggakan.text= "Rp0"
            } else {
                dView.tvTunggakan.text= tunggakan
            }

            /*to change pattern date from the string json
            * Ex. string you get 202009, the pattern will be like yyyyMM*/
            val local = Locale("in", "ID")
            val sdf = SimpleDateFormat("yyyyMM", local)
            val d = sdf.parse(blnTagihan)
            sdf.applyPattern("MMMM yyyy")

            dView.rlGagal.visibility = View.GONE
            dView.rlBerhasil.visibility = View.VISIBLE
            dView.tvNamaPel.text = nama
            dView.tvNomorPel.text = noPel
            dView.tvProdukDetail.text = produk
            dView.tvJmlTagihan.text = jmlTagihan
            dView.tvBlnTagihan.text = sdf.format(d!!)

            dView.tvAdmin.text = admin
            dView.tvTotal.text= totalTagihan

        } else {
            dView.rlGagal.visibility = View.VISIBLE
            dView.rlBerhasil.visibility = View.GONE
            dView.tvGagal.text = reason
        }


        dialog.show()
        dView.bTutup.setOnClickListener { dialog.dismiss() }
    }

    private fun findKota(){
        mView.etNomorBill.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged: $s")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged: $s")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val result = ArrayList<PDAMData>()
                Log.d(TAG, "onTextChanged: $s")
                if (s.isNullOrEmpty()){
                    mView.ivClose.visibility = View.GONE
                } else {
                    mView.ivClose.visibility = View.VISIBLE
                }
                for (i in list){
                    if(i.ppob_produk!!.toLowerCase(Locale.ROOT).contains(s.toString().toLowerCase(Locale.ROOT))) {
                        result.add(i)
                    }
                    pdamAdapter.filter(result)
                    mView.rvPdam.adapter = pdamAdapter
                    pdamAdapter.notifyDataSetChanged()
                }
            }

        })
    }

    private fun getPdam(){
        progressDialog.show(mContext,"Loading...")
        api.retrofit.getPPOB(USERNAME, PASSWORD).enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", t.message!!)
                progressDialog.dialog.dismiss()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    Log.d(TAG, "onResponse: ${response.body().toString()}")
                    val jsonObject = JSONObject(response.body()!!.string())
                    if (jsonObject.getBoolean("status")){
                        val jsonArray = JSONArray(jsonObject.getString("data"))
                        for (i in 0 until jsonArray.length()){
                            val ppob_grup = jsonArray.getJSONObject(i).getString("ppob_grup").toString()
                            val ppob_produk = jsonArray.getJSONObject(i).getString("ppob_produk").toString()
                            val ppob_kodeproduk = jsonArray.getJSONObject(i).getString("ppob_kodeproduk").toString()
                            var ppob_nominal = jsonArray.getJSONObject(i).getString("ppob_nominal").toString()
                            val ppob_admin = jsonArray.getJSONObject(i).getString("ppob_admin").toString()
                            val ppob_debet = jsonArray.getJSONObject(i).getString("ppob_debet").toString()
                            val ppob_status = jsonArray.getJSONObject(i).getString("ppob_status").toString()

                            if (ppob_grup == "PDAM" && ppob_status == "Ready"){
                                val pdam = PDAMData()
                                when (ppob_produk) {
                                    "PDAM Aetra" -> {
                                        pdam.ppob_produk = "DKI Jakarta"
                                    }
                                    "PDAM PALYJA" -> {
                                        pdam.ppob_produk = "DKI Jakarta"
                                    }
                                    else -> {
                                        pdam.ppob_produk = ppob_produk
                                    }
                                }

                                pdam.ppob_grup = ppob_grup
                                pdam.ppob_kodeproduk = ppob_kodeproduk
                                pdam.ppob_nominal = ppob_nominal
                                pdam.ppob_admin = ppob_admin
                                pdam.ppob_debet = ppob_debet
                                pdam.ppob_status = ppob_status
                                list.add(pdam)
                            }
                            pdamAdapter.filter(list)
                            mView.rvPdam.adapter = pdamAdapter
                            pdamAdapter.notifyDataSetChanged()
                            progressDialog.dialog.dismiss()
                        }
                        Log.d(TAG, list.size.toString())
                    }
                } else {
                    progressDialog.dialog.dismiss()
                    Log.e("Erorr", response.message().toString())
                }
            }

        })
    }

}
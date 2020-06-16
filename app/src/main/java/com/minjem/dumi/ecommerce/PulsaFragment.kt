package com.minjem.dumi.ecommerce

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hendi.pulsa.response.G_Pulsa
import com.mdi.stockin.ApiHelper.RecyclerItemClickListener
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.Adapter.A_pulsa
import com.minjem.dumi.ecommerce.Helper.PASSWORD
import com.minjem.dumi.ecommerce.Helper.USERNAME
import com.minjem.dumi.ecommerce.Helper.mDF
import com.minjem.dumi.ecommerce.Helper.mProgress
import com.minjem.dumi.ecommerce.api.HttpRetrofitClient
import com.minjem.dumi.model.SharedPrefManager
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.ecommerce_konfirmasi_pembayaran.*
import kotlinx.android.synthetic.main.ecommerce_pulsa.view.*
import kotlinx.android.synthetic.main.gagal.*
import kotlinx.android.synthetic.main.sukses.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class PulsaFragment : Fragment(){
    lateinit var v : View
    lateinit var mContext: Context
    lateinit var api : HttpRetrofitClient
    lateinit var adapter : A_pulsa
    val list : MutableList<G_Pulsa> = ArrayList()
    lateinit var popup : Dialog
    lateinit var progress : Dialog
    lateinit var dialogSukses : Dialog
    lateinit var dialogGagal : Dialog
    private var idUser : Int = 0
    private var nipBaru : String? = null
    private var saldoUser : Int = 0
    private var itrx : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = layoutInflater.inflate(R.layout.ecommerce_pulsa,container,false)
        mContext = this.context!!
        api = HttpRetrofitClient
        v.id_rv.layoutManager = GridLayoutManager(mContext,3)
        adapter  = A_pulsa(mContext, list)
        v.id_rv.adapter = adapter
        adapter.notifyDataSetChanged()
        progress = Dialog(mContext)
        dialogGagal = Dialog(mContext)
        dialogSukses = Dialog(mContext)
        popup = Dialog(mContext)

        getSaldo()
        getPulsa()
        rvClick()

        v.backPulsaIv.setOnClickListener {
            activity!!.finish()
        }
        v.id_btn_reload.setOnClickListener {
            getPulsa()
        }

        return v
    }

    private fun rvClick() {
        v.id_rv.addOnItemTouchListener(RecyclerItemClickListener(mContext,object : RecyclerItemClickListener.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                if (v.id_no_operator.text.toString().length > 8) {
                    popUp(position)
                } else {
                    v.id_no_operator.setError("No Kurang dari 9 Karakter !")
                    Toast.makeText(mContext,"No Kurang dari 9 Karakter !",Toast.LENGTH_SHORT).show()
                }
            }
        }))
    }

    @SuppressLint("SetTextI18n")
    private fun popUp(position: Int) {
        popup.setContentView(R.layout.ecommerce_konfirmasi_pembayaran)
        popup.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.WRAP_CONTENT)
        popup.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popup.setCancelable(false)
        popup.show()

        popup.id_btn_batalkan.setOnClickListener {
            popup.dismiss()
        }

        popup.id_nomor_pulsa.text = v.id_no_operator.text.toString()
        popup.id_provider_pulsa.text = v.id_nama_operator.text.toString()
        popup.id_nominal_pulsa.text = "Rp. " + adapter.list[position].nominal?.let { mDF(it) }
        popup.id_total_pembayaran.text = "Rp. " + adapter.list[position].harga?.let { mDF(it) }
        popup.id_metode_pulsa.text = "Rp. " + mDF(saldoUser.toString())

        when(popup.id_provider_pulsa.text.toString()){
            "INDOSAT" ->{
                popup.id_logo_pulsa.setBackgroundResource(R.drawable.indosat)}
            "TELKOMSEL" ->{
                popup.id_logo_pulsa.setBackgroundResource(R.drawable.telkomsel)}
            "XL" ->{
                popup.id_logo_pulsa.setBackgroundResource(R.drawable.xl)}
            "TRI" ->{
                popup.id_logo_pulsa.setBackgroundResource(R.drawable.three)}
            "SMARTFREN" ->{
                popup.id_logo_pulsa.setBackgroundResource(R.drawable.smartfren)}
            "AXIS" ->{
                popup.id_logo_pulsa.setBackgroundResource(R.drawable.axis)}
        }

        if (adapter.list[position].harga!!.toInt() > adapter.list[position].nominal!!.toInt()){
            popup.id_biaya_transaksi_pulsa.text = "Rp. " + mDF((adapter.list[position].harga!!.toInt() - adapter.list[position].nominal!!.toInt()).toString())
        } else {
            popup.id_biaya_transaksi_pulsa.text = "Rp. 0"
        }


        popup.id_btn_kirim_pulsa.setOnClickListener {
            mProgress(progress)
            api.retrofit.isiPulsa(idUser, saldoUser, itrx+1,
                    adapter.list[position].kodeoperator!!, popup.id_nomor_pulsa.text.toString(),
                    adapter.list[position].harga!!.toInt(), adapter.list[position].nominal!!.toInt(), adapter.list[position].tipe.toString(), USERNAME, PASSWORD)
                    .enqueue(object : Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("error isipulsa",t.message!!)
                    progress.dismiss()
                    mDialogGagal()
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    progress.dismiss()
                    if (response.isSuccessful){
                        Log.d("Isi Pulsa",response.body().toString())
                        try {
                            val json = JSONObject(response.body()!!.string())
                            if (json.getBoolean("status")){
                                Log.d("Status isi pulsa", json.getString("message"))
                                mDialogSukses()
                            } else {
                                mDialogGagal()
                            }
                        } catch (e: IOException){
                            e.printStackTrace()
                        } catch (e: JSONException){
                            e.printStackTrace()
                            mDialogGagal()
                        }

                    } else {
                        mDialogGagal()
                        Log.e("Error Pulsa",response.body().toString())
                    }
                }
            })
        }

    }

    private fun getSaldo(){
        val id = SharedPrefManager.getInstance(mContext).user.id
        val nipBaru = SharedPrefManager.getInstance(mContext).user.nip
        api.retrofit.getSaldo(id, nipBaru, USERNAME, PASSWORD).enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error",t.message!!)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val jsonObject = JSONObject(response.body()!!.string())
                    if (jsonObject.getBoolean("status")){
                        Log.d("Saldo", jsonObject.getString("message"))
                        val jsonArray = JSONArray(jsonObject.getString("data"))
                        for (i in 0 until jsonArray.length()){
                            idUser = jsonArray.getJSONObject(i).getInt("id")
                            saldoUser = jsonArray.getJSONObject(i).getInt("saldo")
                            itrx = jsonArray.getJSONObject(i).getInt("itrx")

                        }
                        Log.d("getSaldo", "ID: $idUser\nSaldo: $saldoUser\nITRX: $itrx")
                    }

                }
            }

        })
    }

    private fun getPulsa() {
        mProgress(progress)
        v.id_no_operator.setText("")
        v.id_btn_reload.visibility = View.GONE
        api.retrofit.getPulsa( USERNAME, PASSWORD).enqueue(object : Callback<ResponseBody>  {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error",t.message!!)
                progress.dismiss()
                v.id_btn_reload.visibility = View.VISIBLE

            }

            @RequiresApi(Build.VERSION_CODES.KITKAT)
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progress.dismiss()
                if (response.isSuccessful){
                    Log.d("Response",response.body().toString())
                    try {

                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getBoolean("status")){
                            val json = JSONArray(jsonObject.getString("data"))
                            Log.d("Json => ", json.length().toString())

                            for (i in 0 until json.length()){
                                val tipe = json.getJSONObject(i).getString("tipe").toString()
                                val operator = json.getJSONObject(i).getString("operator").toString()
                                val kodeoperator = json.getJSONObject(i).getString("kodeoperator").toString()
                                val keterangan = json.getJSONObject(i).getString("keterangan").toString()
                                val nominal = json.getJSONObject(i).getString("nominal").toString()
                                val harga = json.getJSONObject(i).getString("harga").toString()
                                val status = json.getJSONObject(i).getString("status").toString()


                                Log.d("Log Operator dan Tipe $i", "$operator - $tipe")

                                if (tipe == "PULSA" && status == "Ready"){
                                    val find = list.find { it.operator == operator && it.nominal == nominal }

                                    if (find != null){

                                        if (find.harga!!.toInt() < harga.toInt()){
                                            list.forEachIndexed { index, it ->
                                                if (it.operator == operator && it.nominal == nominal){
                                                    Log.d("Harga lebih besar", "Harga Sekarang " + it.harga + " < " + harga)
                                                    val pulsa = G_Pulsa()
                                                    pulsa.tipe = tipe
                                                    pulsa.operator = operator
                                                    pulsa.kodeoperator = kodeoperator
                                                    pulsa.keterangan = keterangan
                                                    pulsa.nominal = nominal
                                                    pulsa.harga = harga
                                                    pulsa.status = status
                                                    list[index] = pulsa
                                                }
                                            }
                                        }


                                    } else {
                                        val pulsa = G_Pulsa()
                                        pulsa.tipe = tipe
                                        pulsa.operator = operator
                                        pulsa.kodeoperator = kodeoperator
                                        pulsa.keterangan = keterangan
                                        pulsa.nominal = nominal
                                        pulsa.harga = harga
                                        pulsa.status = status
                                        list.add(pulsa)
                                    }
                                }
                            }

                            Log.d("Jumlah Pulsa",list.size.toString())

                            val awal = ArrayList<G_Pulsa>()
                            for (i in list){
                                if (i.operator!!.equals("TELKOMSEL") && i.status!!.contains("Ready")){
                                    Log.d("Filter Tel",i.operator + " - " + i.status + " - " + i.tipe)
                                    awal.add(i)
                                    v.id_nama_operator.text = i.operator
                                    v.id_image_operator.setBackgroundResource(R.drawable.telkomsel)
                                }
                                adapter.filter(awal)
                                v.id_rv.adapter = adapter
                                adapter.notifyDataSetChanged()
                                Log.d("Jumlah Filter",awal.size.toString())
                            }
                        }



                    } catch (e: IOException){
                        e.printStackTrace()
                    } catch (e: JSONException){
                        e.printStackTrace()
                        v.id_btn_reload.visibility = View.VISIBLE
                    }
                    //var list = respon
                } else {
                    v.id_btn_reload.visibility = View.VISIBLE
                    Log.e("Error","Response gagal")
                }
            }
        })


        var masuk = false
        v.id_no_operator.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Operator",s.toString())
                var result = ArrayList<G_Pulsa>()
                if (s.toString().contains("0814") || s.toString().contains("0815") || s.toString().contains("0816")
                        || s.toString().contains("0855") || s.toString().contains("0856") || s.toString().contains("0857")
                        || s.toString().contains("0858") ){
                    for (i in list){
                        if (i.operator!!.contains("INDOSAT")){
                            result.add(i)
                            v.id_nama_operator.text = i.operator
                            v.id_image_operator.setBackgroundResource(R.drawable.indosat)
                        }
                        masuk = true
                        adapter.filter(result)
                        v.id_rv.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                } else if (s.toString().contains("0811") || s.toString().contains("0812") || s.toString().contains("0813") || s.toString().contains("0852") || s.toString().contains("0853") || s.toString().contains("0821") || s.toString().contains("0822") || s.toString().contains("0823") ){
                    for (i in list){
                        if (i.operator!!.contains("TELKOMSEL")){
                            result.add(i)
                            v.id_nama_operator.text = i.operator
                            v.id_image_operator.setBackgroundResource(R.drawable.telkomsel)
                        }
                        masuk = true
                        adapter.filter(result)
                        v.id_rv.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                } else if (s.toString().contains("0817") || s.toString().contains("0818") || s.toString().contains("0819") || s.toString().contains("0859") || s.toString().contains("0877") || s.toString().contains("0878") ){
                    for (i in list){
                        if (i.operator!!.contains("XL")){
                            result.add(i)
                            v.id_nama_operator.text = i.operator
                            v.id_image_operator.setBackgroundResource(R.drawable.xl)
                        }
                        masuk = true
                        adapter.filter(result)
                        v.id_rv.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                } else if (s.toString().contains("0896") || s.toString().contains("0897") || s.toString().contains("0898") || s.toString().contains("0899") ){
                    for (i in list){
                        if (i.operator!!.contains("TRI")){
                            result.add(i)
                            v.id_nama_operator.text = i.operator
                            v.id_image_operator.setBackgroundResource(R.drawable.three)
                        }
                        masuk = true
                        adapter.filter(result)
                        v.id_rv.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                } else if (s.toString().contains("0889") || s.toString().contains("0881") || s.toString().contains("0882") || s.toString().contains("0883") || s.toString().contains("0886") || s.toString().contains("0887") || s.toString().contains("0888") || s.toString().contains("0884") || s.toString().contains("0885") ){
                    for (i in list){
                        if (i.operator!!.contains("SMARTFREN")){
                            result.add(i)
                            v.id_nama_operator.text = i.operator
                            v.id_image_operator.setBackgroundResource(R.drawable.smartfren)
                        }
                        masuk = true
                        adapter.filter(result)
                        v.id_rv.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }else if (s.toString().contains("0832") || s.toString().contains("0838") || s.toString().contains("0833") || s.toString().contains("0831") ){
                    for (i in list){
                        if (i.operator!!.contains("AXIS")){
                            result.add(i)
                            v.id_nama_operator.text = i.operator
                            v.id_image_operator.setBackgroundResource(R.drawable.axis)
                        }
                        masuk = true
                        adapter.filter(result)
                        v.id_rv.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }else {
                    if (s.toString().length > 3){
                        result = ArrayList()
                        for (i in list){
                            if (i.operator!!.contains("kuya")){
                                result.add(i)
                            }
                            adapter.filter(result)
                            v.id_rv.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    } else if (!masuk){
                        for (i in list){
                            if (i.operator!!.contains("TELKOMSEL")){
                                result.add(i)
                                v.id_nama_operator.text = i.operator
                                v.id_image_operator.setBackgroundResource(R.drawable.telkomsel)
                            }
                            adapter.filter(result)
                            v.id_rv.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

                //AXIS , SMARTFREN , BOLT
            }
        })
    }


    private fun mDialogSukses(){
        dialogSukses.setContentView(R.layout.sukses)
        dialogSukses.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        dialogSukses.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogSukses.setCancelable(false)
        dialogSukses.show()

        dialogSukses.id_btn_sukses.setOnClickListener {
//            val i = Intent()
            dialogSukses.dismiss()
            popup.dismiss()
        }
    }

    private fun mDialogGagal(){
        dialogGagal.setContentView(R.layout.gagal)
        dialogGagal.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        dialogGagal.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogGagal.setCancelable(false)
        dialogGagal.show()

        dialogGagal.id_btn_gagal.setOnClickListener {
            dialogGagal.dismiss()
        }

    }

}
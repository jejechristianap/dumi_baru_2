package com.minjem.dumi.ecommerce

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.mdi.stockin.ApiHelper.RecyclerItemClickListener
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.Helper.PASSWORD
import com.minjem.dumi.ecommerce.Helper.USERNAME
import com.minjem.dumi.ecommerce.Helper.mDF
import com.minjem.dumi.ecommerce.adapter.PlnAdapter
import com.minjem.dumi.ecommerce.api.HttpRetrofitClient
import com.minjem.dumi.ecommerce.response.PDAMData
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.util.CustomProgressDialog
import kotlinx.android.synthetic.main.ecommerce_konfirmasi_pembayaran.*
import kotlinx.android.synthetic.main.ecommerce_pln.view.*
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
import java.text.DecimalFormat

class PlnFragment: Fragment() {
    lateinit var mView : View
    lateinit var mContext : Context
    lateinit var api : HttpRetrofitClient
    lateinit var plnAdapter : PlnAdapter
    lateinit var mDialog : Dialog
    lateinit var dialogSukses : Dialog
    lateinit var dialogGagal : Dialog
    private val progressDialog = CustomProgressDialog()
    private var nomorPelanggan : String? = null
    private var namaPelanggan : String? = null
    private var tarifDaya : String? = null
    private var voucher : String? = null
    private var total : String? = null
    private var posItem : Int? = null
    private var idUser = 0;
    private var saldoUser = 0;
    private var itrx = 0;
    lateinit var pb : PDAMData
    private var noInvoice = ""
    private var noMeter = ""
    private var nama = ""
    private var tarifdaya = ""
    private var bayar = ""
    private var materai = ""
    private var ppn = ""
    private var ppj = ""
    private var angsuran = ""
    private var noToken = ""
    private var informasiListrik = ""
    lateinit var dF : DecimalFormat
    val list : MutableList<PDAMData> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = layoutInflater.inflate(R.layout.ecommerce_pln,container,false)
        mContext = this.context!!

        mView.toolbarPln.title = ""
        mView.toolbarPln.setNavigationIcon(R.drawable.ic_back_white)
        mView.toolbarPln.setNavigationOnClickListener {
            activity!!.finish()
        }
        /*Recycler View PLN*/
        mView.rvPln.layoutManager = GridLayoutManager(mContext, 3)
        plnAdapter = PlnAdapter(mContext, list)
        mView.rvPln.adapter = plnAdapter
        plnAdapter.notifyDataSetChanged()
        api = HttpRetrofitClient
        pb = PDAMData()

        /*Dialog*/
        mDialog = Dialog(mContext)
        dialogGagal = Dialog(mContext)
        dialogSukses = Dialog(mContext)

        initOnclick()
        getSaldo()
        getPln()
        rvClick()
        return mView
    }

    private fun initOnclick(){
        mView.rvPln.visibility = View.VISIBLE
        mView.bLanjutPln.visibility = View.GONE
        mView.rlGagalPln.visibility = View.GONE



        mView.rgPln.setOnCheckedChangeListener{ _, checkId ->
            val radio: RadioButton = mView.findViewById(checkId)
            if (radio.text == "Prabayar"){
                mView.rvPln.visibility = View.VISIBLE
                mView.rlGagalPln.visibility = View.GONE
                mView.bLanjutPln.visibility = View.GONE

            } else {
                mView.rvPln.visibility = View.GONE
                mView.bLanjutPln.visibility = View.VISIBLE
//                cekTagihan()
            }
        }

        mView.bLanjutPln.setOnClickListener {
            if (TextUtils.isEmpty(mView.noMeter.text.toString())){
                mView.noMeter.error = "Masukkan no meter!"
                return@setOnClickListener
            }
            cekTagihan()
        }

        /*mView.historyIv.setOnClickListener {


            val i = Intent(mContext, RiwayatView::class.java)
            i.putExtra("invoice", noInvoice)
            i.putExtra("meter", noMeter)
            i.putExtra("nama", nama)
            i.putExtra("tarifdaya", tarifdaya)
            i.putExtra("bayar", bayar)
            i.putExtra("materai", materai)
            i.putExtra("ppn", ppn)
            i.putExtra("ppj", ppj)
            i.putExtra("angsuran", angsuran)
            i.putExtra("token", noToken)
            i.putExtra("informasi", informasiListrik)
            startActivity(i)

        }*/
    }

    private fun getPln(){
        progressDialog.show(mContext, "Loading...")
        api.retrofit.getPPOB(USERNAME, PASSWORD).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", t.message!!)
                progressDialog.dialog.dismiss()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    try {
                        Log.d("Response", response.body().toString())
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getBoolean("status")){
                            val jsonArray = JSONArray(jsonObject.getString("data"))
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
                                                    val pln = PDAMData()
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
                                        val pln = PDAMData()
                                        pln.ppob_grup = ppob_grup
                                        pln.ppob_produk = ppob_produk
                                        pln.ppob_kodeproduk = ppob_kodeproduk
                                        pln.ppob_nominal = ppob_nominal
                                        pln.ppob_admin = ppob_admin
                                        pln.ppob_debet = ppob_debet
                                        pln.ppob_status = ppob_status
                                        list.add(pln)
                                    }
                                }

                                if (ppob_grup == "PLN Group" && ppob_produk == "PLN Bulanan" && ppob_status == "Ready"){
                                    pb.ppob_grup = ppob_grup
                                    pb.ppob_produk = ppob_produk
                                    pb.ppob_kodeproduk = ppob_kodeproduk
                                    pb.ppob_nominal = ppob_nominal
                                    pb.ppob_admin = ppob_admin
                                    pb.ppob_debet = ppob_debet
                                    pb.ppob_status = ppob_status
                                }

                                plnAdapter.filter(list)
                                mView.rvPln.adapter = plnAdapter
                                plnAdapter.notifyDataSetChanged()
                                progressDialog.dialog.dismiss()

                            }
                            Log.d("Jumlah Filter", list.size.toString())
                            Log.d("Jumlah PLN", list.size.toString())
                            progressDialog.dialog.dismiss()
                        }

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

    private fun cekTagihan(){
        progressDialog.show(mContext, "Memuat...")
        val TAG = "PLN Postpaid"

        api.retrofit.cekTagihanPPOB(USERNAME, PASSWORD, pb.ppob_kodeproduk.toString(), mView.noMeter.text.toString())
                .enqueue(object : Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("Error", t.message!!)
                        progressDialog.dialog.dismiss()
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful){
                            progressDialog.dialog.dismiss()
                            val jsonObject = JSONObject(response.body()!!.string())
                            if (jsonObject.getBoolean("status")){
                                Log.d(TAG, "onResponse: ${jsonObject.getString("message")}")
                                val jsonData = JSONObject(jsonObject.getString("data"))
                                if (jsonData.getString("result") == "no"){
                                    mView.rlGagalPln.visibility = View.VISIBLE
                                    mView.tvGagalPln.text = jsonData.getString("reason")
                                }

                            }
                        }
                    }

                })
    }

    private fun cekTagihan(ppobKodeproduk: String?) {
        progressDialog.show(mContext, "Memuat...")
        val noPelanggan = mView.noMeter.text.toString()
        Log.d("Test", "Nomor: $noPelanggan, kode: $ppobKodeproduk")

        if (ppobKodeproduk != null) {
            api.retrofit.cekTagihanPPOB(USERNAME, PASSWORD, ppobKodeproduk, noPelanggan).enqueue(object : Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Error", t.message!!)
                    progressDialog.dialog.dismiss()
                }

                @RequiresApi(Build.VERSION_CODES.N)
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful){
                        try {
//                            rincianTransaksi()
                            Log.d("Response", response.body().toString())
                            val json = JSONObject(response.body()!!.string())
                            val data = json.getString("data")
                            if (json.getBoolean("status")){
                                val request = JSONObject(data);
                                if (request.getString("result") == "ok"){
                                    namaPelanggan = request.getString("ppob_namapelanggan").toString()
                                    tarifDaya = request.getString("ppob_tarifdaya")
                                    nomorPelanggan = request.getString("ppob_nomorpelanggan")
                                    voucher = request.getString("ppob_voucher").toString()
                                    total = request.getString("ppob_totalbayar").toString()
                                    posItem?.let { rincianTransaksi(it) }
                                    progressDialog.dialog.dismiss()
                                } else {
                                    Toast.makeText(mContext, request.getString("reason"), Toast.LENGTH_LONG).show()
                                    progressDialog.dialog.dismiss()
                                }

                            }

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



    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private fun rincianTransaksi(pos : Int){
        mDialog.setContentView(R.layout.ecommerce_konfirmasi_pembayaran)
        mDialog.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.setCancelable(false)
        mDialog.show()

        val sisaSaldo = "Rp${mDF(saldoUser.toString())}"
        voucher = voucher!!.replace(".", "")
        total = total!!.replace(".", "")
        mDialog.id_btn_batalkan.setOnClickListener { mDialog.dismiss() }

        mDialog.id_provider_pulsa.text = namaPelanggan
        mDialog.id_nomor_pulsa.text = "$nomorPelanggan | $tarifDaya"
        mDialog.id_nominal_pulsa.text = "Rp${plnAdapter.list[pos].ppob_nominal}"
        mDialog.id_total_pembayaran.text = "Rp$total"
        mDialog.id_biaya_transaksi_pulsa.text = "Rp${plnAdapter.list[pos].ppob_admin}"
        mDialog.id_metode_pulsa.text = sisaSaldo

        mDialog.id_btn_kirim_pulsa.setOnClickListener {
            isiListrikToken(pos)
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

    private fun isiListrikToken(pos : Int){
        progressDialog.show(mContext,"Loading...")
        val idNasabah = SharedPrefManager.getInstance(mContext).user.id
        val nipBaru = SharedPrefManager.getInstance(mContext).user.nip
        api.retrofit.isiTokenListrik(USERNAME, PASSWORD, plnAdapter.list[pos].ppob_kodeproduk!!, nomorPelanggan!!, idNasabah, nipBaru, voucher!!)
                .enqueue(object : Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("Error",t.message!!)
                        progressDialog.dialog.dismiss()
                        mDialogGagal()
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful){
                            progressDialog.dialog.dismiss()
                            Log.d("Isi Token Listrik", response.body().toString())
                            val jsonObject = JSONObject(response.body()!!.string())
                            if (jsonObject.getBoolean("status")){
                                Log.d("Status token listrik", jsonObject.getString("message"))

                                mDialogSukses()
                                /*for (i in 0 until jsonArray.length()){
                                    noInvoice = jsonArray.getJSONObject(i).getString("ppob_invoice")
                                    noMeter = jsonArray.getJSONObject(i).getString("ppob_nomorpelanggan")
                                    nama = jsonArray.getJSONObject(i).getString("ppob_namapelanggan")
                                    tarifdaya = jsonArray.getJSONObject(i).getString("ppob_tarifdaya")
                                    bayar = jsonArray.getJSONObject(i).getString("ppob_totalbayar")
                                    materai = jsonArray.getJSONObject(i).getString("ppob_materai")
                                    ppn = jsonArray.getJSONObject(i).getString("ppob_ppn")
                                    ppj = jsonArray.getJSONObject(i).getString("ppob_ppj")
                                    angsuran = jsonArray.getJSONObject(i).getString("ppob_angsuran")
                                    noToken = jsonArray.getJSONObject(i).getString("ppob_stroomtoken")
                                    informasiListrik = jsonArray.getJSONObject(i).getString("ppob_footer")
                                }*/


                            } else {
                                progressDialog.dialog.dismiss()
                                mDialogGagal()
                            }
                        } else {
                            progressDialog.dialog.dismiss()
                            mDialogGagal()
                        }
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
//            val i = Intent(mContext, MainActivity::class.java)
//            startActivity(i)
            dialogSukses.dismiss()
            mDialog.dismiss()
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
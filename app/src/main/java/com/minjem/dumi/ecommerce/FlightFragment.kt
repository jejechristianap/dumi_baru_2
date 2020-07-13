package com.minjem.dumi.ecommerce

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.minjem.dumi.ecommerce.api.HttpRetrofitClient
import com.mdi.stockin.ApiHelper.RecyclerItemClickListener
import com.minjem.dumi.util.CustomProgressDialog
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.adapter.BandaraKotaAdapter
import com.minjem.dumi.ecommerce.response.BandaraData
import kotlinx.android.synthetic.main.ecommerce_flight.*
import kotlinx.android.synthetic.main.ecommerce_flight.view.*
import kotlinx.android.synthetic.main.ecommerce_flight.view.switchIv
import kotlinx.android.synthetic.main.ecommerce_flight_cityairport.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FlightFragment : Fragment() {
    lateinit var mView : View
    lateinit var mContext : Context
    var cal = Calendar.getInstance()
    lateinit var api : HttpRetrofitClient
    lateinit var bandaraAdapter : BandaraKotaAdapter
    private val progressDialog = CustomProgressDialog()
    lateinit var mDialog : Dialog
    private var cityBandara : String? = null
    private var dari = false
    private var tujuan = false
    val list : MutableList<BandaraData> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        mView = layoutInflater.inflate(R.layout.ecommerce_flight,container,false)
        mContext = this.context!!
        mDialog = Dialog(mContext, R.style.DialogTheme)
        updateDateInView()
        initOnTouch()

        api = HttpRetrofitClient


        return mView
    }

    private fun initOnTouch(){
        mView.dariTv.setOnClickListener {
//            getBandara()
            dari = true
            tujuan = false
            cariBandara()

        }

        mView.tujuanTv.setOnClickListener {
            dari = false
            tujuan = true
            cariBandara()
        }

        mView.radio_group_flight.setOnCheckedChangeListener { _, checkId ->
            val radio: RadioButton = mView.findViewById(checkId)
            Toast.makeText(mContext, "on check: ${radio.text}", Toast.LENGTH_LONG).show()
            if(radio.text == "Round Trip"){
                mView.roundTripCl.visibility = View.VISIBLE
            } else {
                mView.roundTripCl.visibility = View.GONE
            }

        }

        mView.switchIv.setOnClickListener {
            val dariKota = dariTv.text.toString()
            val tujuanKota = tujuanTv.text.toString()
            dariTv.text = tujuanKota
            tujuanTv.text = dariKota
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        mView.tglBerangkat.setOnClickListener {
            DatePickerDialog(mContext,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        mView.tglKeberangkatan!!.setOnClickListener {
            DatePickerDialog(mContext,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateDateInView() {
        val local = Locale("in", "ID")
        val myFormat = "EEEE, d MMM yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, local)
        mView.tglBerangkat.text = sdf.format(cal.time)
    }

    private fun cariBandara(){
        mDialog.setContentView(R.layout.ecommerce_flight_cityairport)
        mDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        mDialog.rvCityAirport.layoutManager = GridLayoutManager(mContext, 1)
        bandaraAdapter = BandaraKotaAdapter(mContext, list)
        mDialog.rvCityAirport.adapter = bandaraAdapter
        bandaraAdapter.notifyDataSetChanged()
        mDialog.show()

        mDialog.searchView.queryHint = "Cari Bandara/Kota"
        getBandara()
        rvClick()
    }

    private fun getBandara(){
        progressDialog.show(mContext, "Memuat Bandara")
        api.retrofit.getBandara().enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", t.message!!)
                progressDialog.dialog.dismiss()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    try {

                        Log.v("Response", response.body().toString())
                        val jsonArray = JSONArray(response.body()!!.string())
                        Log.d("JsonArray", jsonArray.length().toString())
                        for(i in 0 until jsonArray.length()){
                            val code = jsonArray.getJSONObject(i).getString("code").toString()
                            val city = jsonArray.getJSONObject(i).getString("city").toString()
                            val airport = jsonArray.getJSONObject(i).getString("airport").toString()
                            val grup = jsonArray.getJSONObject(i).getString("grup").toString()
                            val status = jsonArray.getJSONObject(i).getString("status").toString()

                            if (status == "ON" && grup == "lokal" && airport.isNotEmpty()){
                                val find = list.find(){ it.city == city && it.grup == grup}
                                if (find != null){
                                    list.forEachIndexed{ index, bandaraData ->
                                        if (bandaraData.city == city && bandaraData.grup == grup){
                                            val bandara = BandaraData()
                                            bandara.code = code
                                            bandara.city = city
                                            bandara.airport = airport
                                            bandara.grup = grup
                                            bandara.status = status
                                            list[index] = bandara
                                        }
                                    }
                                } else {
                                    val bandara = BandaraData()
                                    bandara.code = code
                                    bandara.city = city
                                    bandara.airport = airport
                                    bandara.grup = grup
                                    bandara.status = status
                                    list.add(bandara)
                                }
                            }
                            bandaraAdapter.filter(list)
                            mDialog.rvCityAirport.adapter = bandaraAdapter
                            bandaraAdapter.notifyDataSetChanged()
                            progressDialog.dialog.dismiss()
                            Log.d("Jumlah Bandara", list.size.toString())
                        }
                        progressDialog.dialog.dismiss()
                    }catch (e: IOException){
                        e.printStackTrace()
                    } catch (e: JSONException){
                        e.printStackTrace()
                        progressDialog.dialog.dismiss()
                    }
                } else {
                    Log.d("Bandara", "onResponse: ${response.body()!!.string()}")
                    progressDialog.dialog.dismiss()
                }
            }
        })

    }

    private fun rvClick(){
        mDialog.rvCityAirport.addOnItemTouchListener(RecyclerItemClickListener(mContext, object : RecyclerItemClickListener.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                if (dari){
                    cityBandara = "${bandaraAdapter.list[position].city} (${bandaraAdapter.list[position].code})"
                    mView.dariTv.text = cityBandara
                    mDialog.dismiss()
                } else {
                    cityBandara = "${bandaraAdapter.list[position].city} (${bandaraAdapter.list[position].code})"
                    mView.tujuanTv.text = cityBandara
                    mDialog.dismiss()
                }

            }

        }))
    }
}
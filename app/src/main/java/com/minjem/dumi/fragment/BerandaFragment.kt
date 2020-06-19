package com.minjem.dumi.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.minjem.dumi.SemuaEcommerceActivity
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.ECommerceActivity
import com.minjem.dumi.ecommerce.Helper.PASSWORD
import com.minjem.dumi.ecommerce.Helper.USERNAME
import com.minjem.dumi.ecommerce.api.BaseApiService
import com.minjem.dumi.ecommerce.transaction.RiwayatView
import com.minjem.dumi.jenispinjaman.PinjamanKilatActivity
import com.minjem.dumi.jenispinjaman.PinjamanRegularActivity
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.retrofit.RetrofitClient
import kotlinx.android.synthetic.main.fragment_beranda.view.*
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

class BerandaFragment : Fragment() {
    private var saldoUser = 0
    private var saldoTv: TextView? = null
    private var pulsaTv: TextView? = null
    private var plnTv: TextView? = null
    private var gopayTv: TextView? = null
    private var ovoTv: TextView? = null
    private var hotelTv: TextView? = null
    private var pesawatTv: TextView? = null
    private var keretaTv: TextView? = null
    private var semuaTv: TextView? = null
    private var localID: Locale? = null
    lateinit var formatRp: NumberFormat
    private var kilatIv: ImageView? = null
    private var regularIv: ImageView? = null
    private var pulsaIv: ImageView? = null
    private var plnIv: ImageView? = null
    private var gopayIv: ImageView? = null
    private var ovoIv: ImageView? = null
    private var hotelIv: ImageView? = null
    private var pesawatIv: ImageView? = null
    private var keretaIv: ImageView? = null
    private var semuaIv: ImageView? = null
    private var kilatB: Button? = null
    private var regularB: Button? = null
    private var tkb: Button? = null
    private var tkbt: Button? = null
    private var riwayatButton: Button? = null
    private var touch = false
    lateinit var mView : View
    lateinit var mContext : Context
    private val go: String? = null

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_beranda, container, false)
        mContext = this.context!!
        initView()
        initOnTouch()
        return mView
    }

    private fun initView() {
        kilatIv = mView.findViewById(R.id.cardKilat)
        regularIv = mView.findViewById(R.id.cardRegular)
        kilatB = mView.findViewById(R.id.kilatButton)
        regularB = mView.findViewById(R.id.regularButton)
        tkb = mView.findViewById(R.id.tkb)
        tkbt = mView.findViewById(R.id.tkbText)
        saldoTv = mView.findViewById(R.id.saldoPayLater)
        riwayatButton = mView.findViewById(R.id.riwayatButton)
        pulsaIv = mView.findViewById(R.id.icPulsa)
        pulsaTv = mView.findViewById(R.id.textPulsa)
        plnIv = mView.findViewById(R.id.icPln)
        plnTv = mView.findViewById(R.id.textPln)
        gopayIv = mView.findViewById(R.id.icGopay)
        gopayTv = mView.findViewById(R.id.textGopay)
        ovoIv = mView.findViewById(R.id.icOvo)
        ovoTv = mView.findViewById(R.id.textOvo)
        hotelIv = mView.findViewById(R.id.icHotel)
        hotelTv = mView.findViewById(R.id.textHotel)
        pesawatIv = mView.findViewById(R.id.icPesawat)
        pesawatTv = mView.findViewById(R.id.textPesawat)
        keretaIv = mView.findViewById(R.id.icKereta)
        keretaTv = mView.findViewById(R.id.textKereta)
        semuaIv = mView.findViewById(R.id.icSemua)
        semuaTv = mView.findViewById(R.id.textSemua)
    }

    private fun initOnTouch() {
        mView.cardKilat.setOnClickListener { goTo("kilat") }
        mView.kilatButton.setOnClickListener { goTo("kilat") }
        mView.cardRegular.setOnClickListener { goTo("regular") }
        mView.regularButton.setOnClickListener { goTo("regular") }
        mView.riwayatButton.setOnClickListener { goTo("riwayat") }
        mView.icPulsa.setOnClickListener { goTo("pulsa") }
        pulsaTv!!.setOnClickListener { goTo("pulsa") }
        plnIv!!.setOnClickListener { goTo("pln") }
        plnTv!!.setOnClickListener { goTo("pln") }
        gopayIv!!.setOnClickListener { goTo("na") }
        gopayTv!!.setOnClickListener { goTo("na") }
        ovoIv!!.setOnClickListener { goTo("na") }
        ovoTv!!.setOnClickListener { goTo("na") }
        hotelIv!!.setOnClickListener { goTo("na") }
        hotelTv!!.setOnClickListener { goTo("na") }
        pesawatIv!!.setOnClickListener { goTo("na") }
        pesawatTv!!.setOnClickListener { goTo("na") }
        keretaIv!!.setOnClickListener { goTo("na") }
        keretaTv!!.setOnClickListener { goTo("na") }
        semuaIv!!.setOnClickListener { goTo("semua") }
        semuaTv!!.setOnClickListener { goTo("semua") }
        tkb!!.setOnClickListener {
            if (touch) {
                tkbt!!.visibility = View.GONE
                touch = false
            } else {
                tkbt!!.visibility = View.VISIBLE
                touch = true
            }
        }
    }

    private fun goTo(item: String) {
        val intent: Intent
        when (item) {
            "kilat" -> startActivity(Intent(activity, PinjamanKilatActivity::class.java))
            "regular" -> startActivity(Intent(activity, PinjamanRegularActivity::class.java))
            "riwayat" -> startActivity(Intent(activity, RiwayatView::class.java))
            "pulsa" -> {
                intent = Intent(activity, ECommerceActivity::class.java)
                intent.putExtra("fragment", "pulsa")
                startActivity(intent)
            }
            "pln" -> {
                intent = Intent(activity, ECommerceActivity::class.java)
                intent.putExtra("fragment", "pln")
                startActivity(intent)
            }
            "semua" -> startActivity(Intent(activity, SemuaEcommerceActivity::class.java))
            else -> Toast.makeText(activity, "Tunggu update kami selanjutanya...", Toast.LENGTH_SHORT).show()
        }
    }

    /*
    ImageListener imageListener = (position, imageView) -> {
        imageView.setImageResource(sampleImages[position]);
        imageView.setOnClickListener(v -> {
            Log.d("Position", "Pos: " + position);
        });
    };*/
    override fun onStart() {
        super.onStart()
        saldo()
    }


    private fun saldo(){
            val idUser = SharedPrefManager.getInstance(activity).user.id
            val nipBaru = SharedPrefManager.getInstance(activity).user.nip
            val api = RetrofitClient.getClient().create(BaseApiService::class.java)
            val call = api.getSaldo(idUser, nipBaru, USERNAME, PASSWORD)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            if (jsonObject.getBoolean("status")) {
                                val jsonArray = JSONArray(jsonObject.getString("data"))
                                for (i in 0 until jsonArray.length()) {
                                    saldoUser = jsonArray.getJSONObject(i).optInt("saldo", 0)
                                }
                                Log.d("SaldoUser", "Saldo: $saldoUser")
                                localID = Locale("in", "ID")
                                formatRp = NumberFormat.getCurrencyInstance(localID!!)
                                saldoTv!!.text = formatRp.format(saldoUser)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }
            })
        }
}
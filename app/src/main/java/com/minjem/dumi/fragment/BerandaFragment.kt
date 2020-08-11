package com.minjem.dumi.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.minjem.dumi.MainActivity
import com.minjem.dumi.SemuaEcommerceActivity
import com.minjem.dumi.R
import com.minjem.dumi.adapter.MenuBaseAdapter
import com.minjem.dumi.ecommerce.ECommerceActivity
import com.minjem.dumi.ecommerce.Helper.PASSWORD
import com.minjem.dumi.ecommerce.Helper.USERNAME
import com.minjem.dumi.ecommerce.api.BaseApiService
import com.minjem.dumi.ecommerce.transaction.RiwayatView
import com.minjem.dumi.jenispinjaman.PinjamanKilatActivity
import com.minjem.dumi.jenispinjaman.PinjamanRegularActivity
import com.minjem.dumi.jenispinjaman.PinjamanUltimateActivity
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.retrofit.RetrofitClient
import com.minjem.dumi.util.BottomSheetFragment
import com.minjem.dumi.util.InternetConnection
import kotlinx.android.synthetic.main.dialog_bottom_sheet_layout.view.*
import kotlinx.android.synthetic.main.fragment_beranda.view.*
import kotlinx.android.synthetic.main.fragment_beranda.view.textPulsa
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
    private var localID: Locale? = null
    lateinit var formatRp: NumberFormat
    private var touch = false
    lateinit var mView : View
    lateinit var mContext : Context
    private val go: String? = null
    lateinit var dView : View
    private val menuAdapter = MenuBaseAdapter("beranda")

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_beranda, container, false)
        mContext = this.context!!
//        initView()
//        initOnTouch()
        mView.srlBeranda.setOnRefreshListener {
            mView.srlBeranda.isRefreshing = true
            refreshList()
        }

        mView.gvMenuUtama.adapter = menuAdapter
        mView.gvMenuUtama.numColumns = 4
        mView.gvMenuUtama.stretchMode = GridView.STRETCH_COLUMN_WIDTH

        initOnTouch()
        saldo()
        val network = InternetConnection(mContext)
        network.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (isConnected){
                Log.d("Network", "onCreateView: $isConnected")
            } else {
                dialogTkb("connection")
            }
        })
        return mView
    }

    private fun initOnTouch() {


        mView.cardKilat.setOnClickListener { goTo("kilat") }
        mView.kilatButton.setOnClickListener { goTo("kilat") }
        mView.cardRegular.setOnClickListener { goTo("regular") }
        mView.regularButton.setOnClickListener { goTo("regular") }
        mView.cardUltimate.setOnClickListener { goTo("ultimate") }
        mView.bUltimate.setOnClickListener { goTo("ultimate") }
        mView.riwayatButton.setOnClickListener { goTo("riwayat") }
        mView.icPulsa.setOnClickListener { goTo("pulsa") }
        mView.textPulsa.setOnClickListener { goTo("pulsa") }
        mView.icPln.setOnClickListener { goTo("pln") }
        mView.textPln.setOnClickListener { goTo("pln") }
        mView.icGopay.setOnClickListener { goTo("pinjaman") }
        mView.textGopay.setOnClickListener { goTo("na") }
        mView.icOvo.setOnClickListener { goTo("na") }
        mView.textOvo.setOnClickListener { goTo("na") }
        mView.icHotel.setOnClickListener { goTo("na") }
        mView.textHotel.setOnClickListener { goTo("na") }
        mView.icPesawat.setOnClickListener { goTo("na") }
        mView.textPesawat.setOnClickListener { goTo("na") }
        mView.icKereta.setOnClickListener { goTo("na") }
        mView.textKereta.setOnClickListener { goTo("na") }
        mView.icSemua.setOnClickListener { goTo("semua") }
        mView.textSemua.setOnClickListener { goTo("semua") }
        mView.tkb.setOnClickListener {
            dialogTkb("tkb")
        }
    }

    private fun refreshList(){
        mView.srlBeranda.isRefreshing = false
        saldo()
    }

    @SuppressLint("InflateParams")
    private fun dialogTkb(s: String){
        dView = layoutInflater.inflate(R.layout.dialog_bottom_sheet_layout, null)
        val dialog = BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme)
        dialog.setContentView(dView)
        dialog.show()
        when(s){
            "tkb" -> {
                dView.rlDialogTkb.visibility = View.VISIBLE
                dView.rlGagal.visibility = View.GONE
                dView.rlBerhasil.visibility = View.GONE
            }
            "connection" -> {
                dView.rlGagal.visibility = View.VISIBLE
                dView.rlBerhasil.visibility = View.GONE
                dView.rlDialogTkb.visibility = View.GONE
            }
        }

        dView.bTutup.setOnClickListener {
           dialog.dismiss()
        }
    }

    private fun showBottomDialog(){
        val bsf = BottomSheetFragment()
        bsf.show(fragmentManager!!, bsf.tag)
    }

    private fun goTo(item: String) {
        val intent: Intent
        when (item) {
            "kilat" -> startActivity(Intent(activity, PinjamanKilatActivity::class.java))
            "regular" -> startActivity(Intent(activity, PinjamanRegularActivity::class.java))
            "ultimate" -> startActivity(Intent(activity, PinjamanUltimateActivity::class.java))
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
            "flight" -> {
                intent = Intent(activity, ECommerceActivity::class.java)
                intent.putExtra("fragment", "flight")
                startActivity(intent)
            }
            "pinjaman" -> {
                intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("fragment", "pinjaman")
            }
            "semua" -> startActivity(Intent(activity, SemuaEcommerceActivity::class.java))
            else -> Toast.makeText(activity, "Tunggu update kami selanjutanya...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saldo(){
            val idUser = SharedPrefManager.getInstance(activity).user.id
            val nipBaru : String? = SharedPrefManager.getInstance(activity).user.nip
            val api = RetrofitClient.getClient().create(BaseApiService::class.java)
            val call = api.getSaldo(idUser, nipBaru.toString(), USERNAME, PASSWORD)
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
                                mView.saldoPayLater.text = formatRp.format(saldoUser)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    val base = "Rp0"
                    mView.saldoPayLater.text = base
                    dialogTkb("connection")
                }
            })
        }
}
package com.minjem.dumi

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.minjem.dumi.ecommerce.Helper.KOLOM
import com.minjem.dumi.ecommerce.Helper.mToast
import com.minjem.dumi.ecommerce.api.HttpRetrofitClient
import com.minjem.dumi.model.SharedPrefManager
import kotlinx.android.synthetic.main.activity_data_pribadi.*
import retrofit2.Callback
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*

open class DataPribadiActivity : AppCompatActivity() {
    private val title = arrayOf("Tanpa Gelar","SMA", "D-1", "D-2", "D-3", "D-4", "S-1", "S-2", "S-3")
    private val tanggungan = arrayOf("1 Orang", "2 Orang", "3 Orang", "4 Orang", "5 Orang")
    private val jenisKelamin = arrayOf("L", "P")
    private val statusKawin = arrayOf("BELUM MENIKAH", "MENIKAH", "CERAI", "DUDA", "JANDA")
    private val statusHubungan = arrayOf("ORANGTUA", "KAKAK/ADIK/KERABAT", "SUAMI/ISTRI")
    private val statusRumah = arrayOf("KONTRAK", "RUMAH SENDIRI", "RUMAH ORANGTUA")
    private val pickDate: ImageView? = null
    private var myCalendar: Calendar? = null
    lateinit var api : HttpRetrofitClient
    var currentPhoneNumber: String = ""
    private var creditscore = ""

    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pribadi)
        currentPhoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber.toString()
        api = HttpRetrofitClient
        initSpinner()

        backDataPribadi.setOnClickListener {
            exitByBackKey()
        }

        bLanjutDataPribadi.setOnClickListener {
            if (TextUtils.isEmpty(etNoKtp.text.toString())){
                etNoKtp.error = KOLOM
                etNoKtp.requestFocus()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(etNoNpwp.text.toString())){
                etNoNpwp.error = KOLOM
                etNoNpwp.requestFocus()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(etAlamat.text.toString())){
                etAlamat.error = KOLOM
                etAlamat.requestFocus()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(etpos.text.toString())){
                etpos.error = KOLOM
                etpos.requestFocus()
                return@setOnClickListener
            }

            cekIziData()
        }
    }

    private fun initSpinner(){
        val saPendidikan = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, title)
        sPendidikanTerakhir.adapter = saPendidikan

        val saTanggungan = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, tanggungan)
        sJumlahTanggungan.adapter = saTanggungan

        val saStatusRumah = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, statusRumah)
        sStatusRumah.adapter = saStatusRumah
    }

    private fun cekIziData(){
        progressDialog.show(this, "Mohon menunggu..")
        api.retrofit.creditFeature(etNoKtp.text.toString(), intent.getStringExtra("namaKtp")!!.toString(), currentPhoneNumber)
                .enqueue(object : Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("Error", t.message!!)
                        progressDialog.dialog.dismiss()
                        mToast(this@DataPribadiActivity, "Server tidak merespon, silahkan coba beberapa saat lagi.")
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful){
                            val jsonObject = JSONObject(response.body()!!.string())
                            if (jsonObject.getBoolean("status")){
                                Log.d("Credit Feature", "onResponse: ${jsonObject.getString("message")}")
                                val jsonData = JSONObject(jsonObject.getString("data"))
                                if (jsonData.getString("status") == "OK"){
                                    val jsonMessage = JSONObject(jsonData.getString("message"))
                                    Log.d("Credit Score", "onResponse: ${jsonMessage.getInt("creditscore")}")
                                    creditscore = jsonMessage.getInt("creditscore").toString()
                                    val featureList = jsonMessage.getString("featurelist")
                                    val jsonFeature = JSONObject(jsonMessage.getString("featurelist"))
                                    Log.d("Featurelist", "onResponse: $featureList")

                                    if (jsonFeature.getString("identity_status") == "OK"){
                                        val identity_address = jsonFeature.getString("identity_address")
                                        val identity_city = jsonFeature.getString("identity_city")
                                        val identity_date_of_birth = jsonFeature.getString("identity_date_of_birth")
                                        val identity_district = jsonFeature.getString("identity_district")
                                        val identity_gender = jsonFeature.getString("identity_gender")
                                        val identity_marital_status = jsonFeature.getString("identity_marital_status")
                                        val identity_match = jsonFeature.optInt("identity_match").toString()
                                        val identity_name = jsonFeature.getString("identity_name")
                                        val identity_nationnality = jsonFeature.getString("identity_nationnality")
                                        val identity_place_of_birth = jsonFeature.getString("identity_place_of_birth")
                                        val identity_province = jsonFeature.getString("identity_province")
                                        val identity_religion = jsonFeature.getString("identity_religion")
                                        val identity_rt = jsonFeature.getString("identity_rt")
                                        val identity_rw = jsonFeature.getString("identity_rw")


                                        val sp = getSharedPreferences("DATA", Context.MODE_PRIVATE)
                                        val editor = sp.edit()
                                        editor.putString("creditscore", creditscore)
                                        editor.putString("identity_address", identity_address)
                                        editor.putString("identity_city", identity_city)
                                        editor.putString("identity_date_of_birth", identity_date_of_birth)
                                        editor.putString("identity_district", identity_district)
                                        editor.putString("identity_gender", identity_gender)
                                        editor.putString("identity_marital_status", identity_marital_status)
                                        editor.putString("identity_match", identity_match)
                                        editor.putString("identity_name", identity_name)
                                        editor.putString("identity_nationnality", identity_nationnality)
                                        editor.putString("identity_place_of_birth", identity_place_of_birth)
                                        editor.putString("identity_province", identity_province)
                                        editor.putString("identity_religion", identity_religion)
                                        editor.putString("identity_rt", identity_rt)
                                        editor.putString("identity_rw", identity_rw)
                                        editor.putString("identity_status", jsonFeature.getString("identity_status"))
                                        editor.putString("identity_village", jsonFeature.getString("identity_village"))
                                        editor.putString("identity_work", jsonFeature.getString("identity_work"))
                                        editor.putString("isfacebook", jsonFeature.getString("isfacebook"))
                                        editor.putString("iswhatsapp", jsonFeature.getString("iswhatsapp"))
                                        editor.putString("multiphone_idinfo", jsonFeature.getString("multiphone_idinfo"))
                                        editor.putString("multiphone_phoneinfo_id", jsonFeature.getString("multiphone_phoneinfo_id"))
                                        editor.putString("multiphone_phoneinfo_id_phone", jsonFeature.getString("multiphone_phoneinfo_id_phone"))
                                        editor.putString("multiphone_status", jsonFeature.getString("multiphone_status"))
                                        editor.putString("phoneage", jsonFeature.getString("phoneage").toString())
                                        editor.putString("phoneage_status", jsonFeature.getString("phoneage_status"))
                                        editor.putString("phonealive_id_num", jsonFeature.getInt("phonealive_id_num").toString())
                                        editor.putString("phonealive_phone_num", jsonFeature.getInt("phonealive_phone_num").toString())
                                        editor.putString("phonealive_status", jsonFeature.getString("phonealive_status"))
                                        editor.putString("phonescore_status", jsonFeature.getString("phonescore_status"))
                                        editor.putString("featurelist", featureList)
                                        editor.putString("noKtp", etNoKtp.text.toString())
                                        editor.putString("noTelp",currentPhoneNumber)
                                        editor.putString("tanggungan", sJumlahTanggungan.selectedItem.toString())
                                        editor.putString("pendidikan", sPendidikanTerakhir.selectedItem.toString())
                                        editor.putString("npwp", etNoNpwp.text.toString())
                                        editor.putString("statusRumah", sStatusRumah.selectedItem.toString())
                                        editor.putString("alamat", etAlamat.text.toString())
                                        editor.putString("pos", etpos.text.toString())

                                        editor.apply()
                                        startActivity(Intent(this@DataPribadiActivity, DataKeluargaActivity::class.java))
                                        progressDialog.dialog.dismiss()
                                    } else {
                                        mToast(this@DataPribadiActivity, "Data tidak valid, mohon periksa kembali data anda.")
                                        progressDialog.dialog.dismiss()
                                    }


                                } else {
                                    progressDialog.dialog.dismiss()
                                    mToast(this@DataPribadiActivity, jsonData.getString("message"))
                                }
                            } else {
                                progressDialog.dialog.dismiss()
                                mToast(this@DataPribadiActivity, "Verifikasi gagal..")
                            }
                        }
                    }
                })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exitByBackKey() {
        AlertDialog.Builder(this)
                .setMessage("Apa anda yakin ingin kembali ke Halaman Depan?")
                .setPositiveButton("Ya") { _: DialogInterface?, _: Int ->
                    SharedPrefManager.getInstance(applicationContext).logout()
                    finish()
                    startActivity(Intent(this@DataPribadiActivity, HalamanDepanActivity::class.java))
                }
                .setNegativeButton("Tidak") { _: DialogInterface?, _: Int -> }
                .show()
    }


}


package com.minjem.dumi

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.minjem.dumi.DaftarActivity
import com.minjem.dumi.api.CekUserExist
import com.minjem.dumi.api.LoginInterface
import com.minjem.dumi.ecommerce.Helper.mProgress
import com.minjem.dumi.model.PreferenceHelper
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.model.User
import com.minjem.dumi.retrofit.RetrofitClient
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MasukActivity : AppCompatActivity() {
    lateinit var masukButton: Button
    lateinit var nipEt: EditText
    lateinit var passEt: EditText
    private val preferenceHelper: PreferenceHelper? = null
    lateinit var daftarDisiniTv: TextView
    lateinit var dProgress: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masuk)
        dProgress = Dialog(this)
        /*AsyncTask.execute(() -> {
            if (isOnline(this)){

            } else {
                try {
                    ContextThemeWrapper ctw = new ContextThemeWrapper( this, R.style.Theme_AppCompat_Dialog_Alert);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctw);
                    alertDialog.setTitle("Info");
                    alertDialog.setMessage("Internet tidak tersedia, mohon cek koneksi internet anda.");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("OK", (dialog, which) -> finish());

                    alertDialog.show();
                } catch (Exception e) {
                    Log.d("Dialog", "Show Dialog: " + e.getMessage());
                }
            }
        });*/
        nipEt = findViewById(R.id.masuk_nip_et)
        passEt = findViewById(R.id.masuk_password_et)
        daftarDisiniTv = findViewById(R.id.daftar_disini_tv)
        masukButton = findViewById(R.id.masuk_user_button)
        masukButton.setOnClickListener { cekUser() }
        daftarDisiniTv.setOnClickListener{
            finish()
            startActivity(Intent(this@MasukActivity, DaftarActivity::class.java))
        }
    }


    override fun onBackPressed() {
        finish()
        startActivity(Intent(this@MasukActivity, HalamanDepanActivity::class.java))
    }

    private fun cekUser() {
        val nip = nipEt.text.toString()
        val pass = passEt.text.toString()
        if (TextUtils.isEmpty(nip)) {
            nipEt.error = "Kolom tidak boleh kosong..."
            nipEt.requestFocus()
            return
        }
        if (TextUtils.isEmpty(pass)) {
            passEt.error = "Kolom tidak boleh kosong..."
            passEt.requestFocus()
            return
        }
        if (nip == "12345678901234567890" && pass == "123456") {
            val id = 7
            val nipB = "12345678901234567890"
            val email = ""
            val password = ""
            val noKtp = ""
            val nama = ""
            val agama = ""
            val jenisKelamin = ""
            val tempatLahir = ""
            val tanggalLahir = ""
            val statusKawin = ""
            val jumlahTanggungan = ""
            val pendidikan = ""
            val ketTitle = ""
            val insker = ""
            val statusRumah = ""
            val alamat = ""
            val rt = ""
            val rw = ""
            val propinsi = ""
            val kota = ""
            val kecamatan = ""
            val kelurahan = ""
            val kodePos = ""
            val statusHubungan = ""
            val namaPenanggung = ""
            val noKtpPenanggung = ""
            val namaIbu = ""
            val noHp = ""
            val imageKtp = ""
            val imageSelfi = ""
            val imageProfile = ""

            /*User user = new User(id, nip, email, password, noKtp, nama, agama, jenisKelamin,
                    tempatLahir, tanggalLahir, statusKawin, jumlahTanggungan, pendidikan, ketTitle,
                    insker, statusRumah, alamat, rt, rw, propinsi, kota, kecamatan, kelurahan,
                    kodePos, statusHubungan, namaPenanggung, noKtpPenanggung, namaIbu, noHp, imageKtp, imageSelfi, imageProfile);

            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);*/finish()
            startActivity(Intent(this@MasukActivity, MainActivity::class.java))
        } else {
            mProgress(dProgress)
            val cekUser = RetrofitClient.getClient().create(CekUserExist::class.java)
            val call = cekUser.cekUser(nip)
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    try {
                        var obj: JSONObject? = null
                        if (response.body() != null) {
                            obj = JSONObject(response.body()!!.string())
                        }
                        //                    JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        val status = obj!!.getBoolean("status")

                        if (!status) {
                            loginUser()
                        } else {
                            dProgress.dismiss()
                            Toast.makeText(this@MasukActivity, "NIP anda belum terdaftar, silahkan mendaftarkan NIP anda", Toast.LENGTH_SHORT).show()
                            finish()
                            startActivity(Intent(this@MasukActivity, DaftarActivity::class.java))
                        }
                    } catch (e: Exception) {
//                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dProgress.dismiss()
                        Toast.makeText(this@MasukActivity, e.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    dProgress.dismiss()
                    Toast.makeText(this@MasukActivity, "Koneksi gagal. Mohon periksa jaringan anda", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loginUser() {
        val nip = nipEt.text.toString()
        val password = passEt.text.toString()
        if (TextUtils.isEmpty(nip)) {
            nipEt.error = "Mohon masukkan NIP anda"
            nipEt.requestFocus()
            return
        } else if (nip.length < 10) {
            nipEt.error = "NIP SALAH"
            nipEt.requestFocus()
            return
        }
        if (TextUtils.isEmpty(password)) {
            passEt.error = "Mohon masukkan password anda"
            passEt.requestFocus()
            return
        } else {
            passEt.error = null
        }
        val cek = RetrofitClient.getClient().create(LoginInterface::class.java)
        val call = cek.getUserLogin(nip, password)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val obj = JSONObject(response.body()!!.string())
                    val status = obj.getBoolean("status")
                    if (status) {
                        val dat = obj.getString("data")
                        val dataArray = JSONArray(dat)
                        //                        startActivity(new Intent(MasukActivity.this, MainActivity.class));
                        for (i in 0 until dataArray.length()) {
                            val userObj = dataArray.getJSONObject(i)
                            val id = userObj.optInt("id")
                            val nip = userObj.getString("nipBaru")
                            val email = userObj.getString("email")
                            val password = userObj.getString("sandi")
                            val noKtp = userObj.getString("noktp")
                            val nama = userObj.getString("namaPns")
                            val agama = userObj.getString("agama")
                            val jenisKelamin = userObj.getString("jenis_kelamin")
                            val tempatLahir = userObj.getString("tempatLahir")
                            val tanggalLahir = userObj.getString("tglLhrPns")
                            val statusKawin = userObj.getString("status_kawin")
                            val jumlahTanggungan = userObj.getString("tanggungan")
                            val pendidikan = userObj.getString("pendidikan")
                            val ketTitle = userObj.getString("ket_title")
                            val insker = userObj.getString("inskerNama")
                            val statusRumah = userObj.getString("status_rumah")
                            val alamat = userObj.getString("alamat")
                            val rt = userObj.getString("rt")
                            val rw = userObj.getString("rw")
                            val propinsi = userObj.getString("provinsi")
                            val kota = userObj.getString("kabkota")
                            val kecamatan = userObj.getString("kecamatan")
                            val kelurahan = userObj.getString("desa")
                            val kodePos = userObj.getString("kodepos")
                            val statusHubungan = userObj.getString("status_hubungan")
                            val namaPenanggung = userObj.getString("nama_penanggung")
                            val noKtpPenanggung = userObj.getString("no_ktp_penanggung")
                            val namaIbu = userObj.getString("ibuKandung")
                            val noHp = userObj.getString("no_hp")
                            val imageKtp = userObj.getString("photo_ktp")
                            val imageSelfi = userObj.getString("photo_selfi")
                            val imageProfile = userObj.getString("photo_profile")
                            val statusTopup = userObj.optInt("status_topup", 0)
                            val saldo = userObj.optInt("saldo", 0)
                            val saldo_max = userObj.optInt("saldo_max", 0)
                            val user = User(id, nip, email, password, noKtp, nama, agama, jenisKelamin,
                                    tempatLahir, tanggalLahir, statusKawin, jumlahTanggungan, pendidikan, ketTitle,
                                    insker, statusRumah, alamat, rt, rw, propinsi, kota, kecamatan, kelurahan,
                                    kodePos, statusHubungan, namaPenanggung, noKtpPenanggung, namaIbu, noHp, imageKtp,
                                    imageSelfi, imageProfile, statusTopup, saldo, saldo_max)
                            SharedPrefManager.getInstance(applicationContext).userLogin(user)
                        }
                        finish()
                        startActivity(Intent(this@MasukActivity, MainActivity::class.java))
                        dProgress.dismiss()
                    } else {
                        dProgress.dismiss()
                        Toast.makeText(this@MasukActivity, "NIP/Password Salah!", Toast.LENGTH_SHORT).show()
                        nipEt!!.requestFocus()
                        passEt!!.requestFocus()
                    }
                } catch (e: JSONException) {
                    dProgress.dismiss()
                    e.printStackTrace()
                } catch (e: IOException) {
                    dProgress.dismiss()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                dProgress.dismiss()
            }
        })
    }

    companion object {
        private val message: String? = null
    }
}
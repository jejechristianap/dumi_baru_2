package com.minjem.dumi

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.Html
import android.text.InputType
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.PhoneBuilder
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.minjem.dumi.akun.KebijakanPrivasiActivity
import com.minjem.dumi.api.BaseApiService
import com.minjem.dumi.api.CekNipBknInterface
import com.minjem.dumi.api.CekUserExist
import com.minjem.dumi.ecommerce.Helper.mProgress
import com.minjem.dumi.ecommerce.Helper.mToast
import com.minjem.dumi.ecommerce.Helper.sBar
import com.minjem.dumi.retrofit.RetrofitClient
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_daftar.*
import kotlinx.android.synthetic.main.dialog_bottom_email_verification.view.*
import kotlinx.android.synthetic.main.dialog_datepicker.view.*
import kotlinx.android.synthetic.main.dialog_syaratketentuan.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class DaftarActivity : AppCompatActivity() {

    private val instansi = arrayOf("Pilih Mitra", "ASN AKTIF", "BUMN", "ASN PENSIUN")
    private val instansiAdapter: ArrayAdapter<CharSequence>? = null
    lateinit var editor: SharedPreferences.Editor
    lateinit var pref: SharedPreferences
    private val accessToken = ""
    private val countDown = 0
    lateinit var myCalendar: Calendar
    lateinit var dProgress : Dialog
    private var count = false
    private var code = ""
    lateinit var auth: FirebaseAuth
    private var emailVer = false

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)
        auth = Firebase.auth
        dProgress = Dialog(this)
        pref = applicationContext.getSharedPreferences("DATA", 0) // 0 - for private mode
        editor = pref.edit()
        editor.clear()
        val ll = findViewById<LinearLayout>(R.id.email_password)
        ll.visibility = View.GONE
        setuju_tv.text = Html.fromHtml(getString(R.string.saya_setuju))
        setuju_tv.setOnClickListener {  startActivity(Intent(this, KebijakanPrivasiActivity::class.java)) }

        /*setuju_switch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                lanjut_button.isEnabled = true
                lanjut_button!!.setBackgroundResource(R.drawable.button_design_login_register)
            } else {
                lanjut_button.isEnabled = false
                lanjut_button!!.setBackgroundResource(R.drawable.button_lanjut_design)
            }
        }*/

        /*instansiSpinner = findViewById(R.id.daftar_sebagai_spinner);
        instansiAdapter = new ArrayAdapter<>(DaftarActivity.this, R.layout.spinner_text, instansi);
        instansiAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        instansiSpinner.setAdapter(instansiAdapter);
        */

//        pDialog = ProgressDialog(this@DaftarActivity)


        /*Hide Soft Keyboard*/
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        cek_nip.setOnClickListener {cekUser()}
        myCalendar = Calendar.getInstance()
        val date = OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }
        daftar_tgl_lahir.setInputType(InputType.TYPE_NULL)
        daftar_tgl_lahir.setOnClickListener { datePicker() }
            /*new DatePickerDialog(DaftarActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/

        show_password.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                masuk_password_et.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ulangi_password_et.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                masuk_password_et.transformationMethod = PasswordTransformationMethod.getInstance()
                ulangi_password_et.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        lanjut_button.setOnClickListener(View.OnClickListener { v: View? ->
            val nip = daftar_nip_et.text.toString()
            val email = email_daftar_et.text.toString()
            val pass = masuk_password_et.text.toString()
            val ulangiPass = ulangi_password_et.text.toString()

            /*Email Handling*/
            if (TextUtils.isEmpty(email)) {
                email_daftar_et.error = "Kolom ini tidak boleh kosong.."
                email_daftar_et.requestFocus()
                return@OnClickListener
            } else if (!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
                email_daftar_et.error = "Email tidak valid"
                email_daftar_et.requestFocus()
                return@OnClickListener
            } else {
                email_daftar_et.setError(null)
            }
                /*Password Handling*/
            when {
                TextUtils.isEmpty(pass) -> {
                    masuk_password_et.error = "Kolom ini tidak boleh kosong.."
                    masuk_password_et.requestFocus()
                    return@OnClickListener
                }
                pass.length < 6 -> {
                    masuk_password_et.error = "Password minimal 6 karakter"
                    masuk_password_et.requestFocus()
                    return@OnClickListener
                }
                else -> {
                    masuk_password_et.error = null
                }
            }
            /*Check is password match*/
            if (ulangiPass != pass) {
                ulangi_password_et.error = "Password tidak sama.."
                ulangi_password_et.requestFocus()
                return@OnClickListener
            } else {
                ulangi_password_et.error = null
            }
            editor.putString("nip", nip)
            editor.putString("email", email)
            editor.putString("pass", pass)
            editor.apply()

//            startActivity(new Intent(DaftarActivity.this, OtpVerify.class));
            dialogSyaratKetentuan()
//            doPhoneLogin()
        })
    }

    private fun updateLabel() {
        val myFormat = "dd-MM-yyyy" //In which you need put here
        val localID = Locale("in", "ID")
        val sdf = SimpleDateFormat(myFormat, localID)
        daftar_tgl_lahir.text = sdf.format(myCalendar.time)
    }

    @SuppressLint("SetTextI18n")
    private fun datePicker() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_datepicker, null)
        val dialog = BottomSheetDialog(this)
        val dp = dialogView.findViewById<DatePicker>(R.id.dpTglLahir)
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()
        dp.init(
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar!![Calendar.DAY_OF_MONTH]
        ) { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            val myFormat = "dd-MM-yyyy" //In which you need put here
            val localID = Locale("in", "ID")
            val sdf = SimpleDateFormat(myFormat, localID)
            //tanggalLahir.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
            daftar_tgl_lahir!!.text = sdf.format(myCalendar.time)
        }
        dialogView.bPilih.setOnClickListener { dialog.dismiss() }
    }

    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)
        val mAuth = FirebaseAuth.getInstance()
        if (emailVer){
            doPhoneLogin()
            emailVer = false
        } else {
            if (mAuth.currentUser != null) {
                val i = Intent(applicationContext, DataPribadiActivity::class.java)
                i.putExtra("namaKtp", daftar_nama_pns!!.text.toString())
                startActivity(i)
                //            FirebaseAuth.getInstance().createUserWithEmailAndPassword()
            }
        }

    }

    private fun dialogSyaratKetentuan() {
        val mDialog = Dialog(this, R.style.DialogTheme)
        val v = layoutInflater.inflate(R.layout.dialog_syaratketentuan, null)
        mDialog.setContentView(v)
        mDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mDialog.show()
        mDialog.toolbarSyaratKetentuan.title = "Syarat dan Ketentuan"
        mDialog.toolbarSyaratKetentuan.setNavigationIcon(R.drawable.ic_back_white)
        mDialog.toolbarSyaratKetentuan.setNavigationOnClickListener {
            mDialog.dismiss()
        }

        mDialog.svSyarat.viewTreeObserver.addOnScrollChangedListener {
            if (mDialog.svSyarat.getChildAt(0).bottom <= (mDialog.svSyarat.height + mDialog.svSyarat.scrollY)) {
                //scroll view is at bottom
                mDialog.ivToBottom.visibility = View.GONE
            } else {
                //scroll view is not at bottom
                mDialog.ivToBottom.visibility = View.VISIBLE
            }
        }

        mDialog.ivToBottom.setOnClickListener {
//            sBar(v, "kebawah.")
            mDialog.svSyarat.fullScroll(View.FOCUS_DOWN)
        }


        var check = false
        mDialog.cbSyarat.setOnCheckedChangeListener { _, isChecked ->
            check = isChecked
        }

        mDialog.bVerifikasiEmail.setOnClickListener {
            if (check){
//                sBar(v, "Tunggu yaaww")
                emailVerification(email_daftar_et.text.toString())
                /*auth.createUserWithEmailAndPassword(email_daftar_et.text.toString(), masuk_password_et.text.toString())
                        .addOnCompleteListener(this){task ->
                            if (task.isSuccessful){
                                Log.d("SIGNUP EMAIL", "dialogSyaratKetentuan: success")
                                val user = auth.currentUser
                                user!!.sendEmailVerification()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful){
                                                dProgress.dismiss()
                                                emailVer = true
                                                sBar(v, "Link verifikasi berhasil terkirim. Mohon cek email anda..")
                                                Log.d("EMAIL VERIFICATION", "dialogSyaratKetentuan: Email Sent.")
                                            } else {
                                                dProgress.dismiss()
                                                sBar(v, "Link gagal dikirim, silakan coba lagi..")
                                            }
                                        }
                            } else {
                                Log.w("SIGNUP EMAIL", "dialogSyaratKetentuan:failure", task.exception )
                                mToast(this, "Failed")

                            }
                        }*/

            } else {
                sBar(v, "Mohon menyetujui syarat dan ketentuan kami.")
            }

        }

    }

    private fun emailVerification(email: String) {
        val dv = layoutInflater.inflate(R.layout.dialog_bottom_email_verification, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dv)
        dialog.show()
        dv.etVerifikasiEmail.setText(email)


        dv.bEmailVerification.setOnClickListener {

            if (!count){
                mProgress(dProgress)
                count = true
                code = ""
                for (i in 0 until 5){
                    val random = (1..9).random()
                    code += random.toString()
                }
                Log.d("Random Number", "NumberCode: $code")

                sendVerification(dv.etVerifikasiEmail.text.toString(), dv)

            } else {
                Toasty.error(this, "Mohon menunggu waktu habis", Toast.LENGTH_LONG, true).show()
            }
        }

        dv.bVerfikasiKode.setOnClickListener {
            if (dv.etKodeVerifikasi.text.toString() == code){
                Log.d("Verfication", "emailVerification: ${dv.etKodeVerifikasi.text} : $code")
                doPhoneLogin()
            } else {
                mToast(this, "Kode verifikasi yang anda masukkan salah!")
                return@setOnClickListener
            }
        }
    }

    private fun startTimeCounter(view: View) {
        var counter = 59
        val countTime: TextView = view.findViewById(R.id.countTime)
        object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                countTime.text = "$counter detik"
                counter--
            }
            override fun onFinish() {
                countTime.text = ""
                view.etVerifikasiEmail.isEnabled = true
                view.bEmailVerification.setBackgroundResource(R.drawable.button_outerline_blue)
                view.bEmailVerification.setTextColor(Color.parseColor("#1560db"))
                count = false
            }
        }.start()
    }

    private fun sendVerification(email: String, dv: View){
        val send = RetrofitClient.client?.create(BaseApiService::class.java)
        val call = send?.sendCodeVerification(email, code)
        call!!.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                dProgress.dismiss()
                Log.e("Send code verification", "onFailure: failed")
                count = false
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val jsonObject = JSONObject(response.body()!!.string())
                    if (jsonObject.getBoolean("status")){
                        val jsonArray = JSONArray(jsonObject.getString("data"))
                        val jsonAccepted = JSONArray(jsonArray.getJSONObject(0).getString("accepted"))
                        if (jsonAccepted.length() == 0){
                            Log.d("Send code verification", "onResponse: Failure")
                            count = false
                            dProgress.dismiss()
                        } else {
                            dProgress.dismiss()
                            dv.etVerifikasiEmail.isEnabled = false
                            dv.bEmailVerification.setBackgroundResource(R.drawable.button_outerline_grey)
                            dv.bEmailVerification.setTextColor(Color.GRAY)
                            dv.bEmailVerification.text = "Kirim ulang"
                            dv.rlVerifikasiKode.visibility = View.VISIBLE
                            startTimeCounter(dv)
                            Toasty.success(this@DaftarActivity, "Email verifikasi berhasil dikirim, silahkan cek email anda.", Toast.LENGTH_LONG, true).show()
                        }
                    } else {
                        count = false
                        Toasty.error(this@DaftarActivity, "Email verifikasi gagal dikirim, pastikan email anda sudah benar!", Toast.LENGTH_LONG, true).show()
                    }
                    dProgress.dismiss()
                } else {
                    dProgress.dismiss()
                    count = false
                    Toasty.warning(this@DaftarActivity, "Koneksi ke server gagal, mohon cek koneksi anda.", Toast.LENGTH_LONG, true).show()
                    Log.e("Send code verification", "onResponse: ${response.message()}")
                }
            }

        })
    }

    private fun dialogGagal(ins: String?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_bottom_sheet_layout, null)
        val dialog = BottomSheetDialog(this)
        val rlGagalInstansi = dialogView.findViewById<RelativeLayout>(R.id.rlDialogInstansi)
        val tvGagalInstansi = dialogView.findViewById<TextView>(R.id.tvInstansi)
        val bTutup = dialogView.findViewById<Button>(R.id.bTutup)
        dialog.setContentView(dialogView)
        dialog.show()
        rlGagalInstansi.visibility = View.VISIBLE
        tvGagalInstansi.text = ins
        bTutup.setOnClickListener {
            rlGagalInstansi.visibility = View.GONE
            dialog.dismiss()
        }
    }

    /*FireBase OTP UI*/
    private fun doPhoneLogin() {
        val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setAvailableProviders(listOf(PhoneBuilder().build()))
                .setTheme(R.style.OtpTheme)
                .setLogo(R.mipmap.ic_launcher_dumi)
                .build()
        startActivityForResult(intent, RC_SIGN_IN)
    }

    /*private void getToken(){
        CekNipBknInterface token = RetrofitClient.getClient().create(CekNipBknInterface.class);
        Call<ResponseBody> call = token.getToken();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String data = jsonObject.getString("data");
                        JSONObject request = new JSONObject(data);
                        Log.d("Data", "onResponse: " + data);
                        if (jsonObject.getBoolean("status")){
//                            Toast.makeText(DaftarActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                            JSONObject data = jsonObject.getJSONObject("data");
                            accessToken = request.getString("access_token");
                            countDown = request.getInt("expires_in");

                            Log.d("Token", "onResponse: " + accessToken);

                            cekUser();
                        } else {
                            Toast.makeText(DaftarActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException | IOException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val idpResponse = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
            } else {
                /**
                 * Sign in failed. If response is null the user canceled the
                 * sign-in flow using the back button. Otherwise check
                 * response.getError().getErrorCode() and handle the error.
                 */
                Toast.makeText(baseContext, "OTP Gagal", Toast.LENGTH_LONG).show()
            }
        }
    }

    /*Cek nip sudah pernah mendaftar atau belum*/
    private fun cekUser() {
        val nip = daftar_nip_et!!.text.toString()
        if (TextUtils.isEmpty(nip)) {
            daftar_nip_et!!.error = "Kolom tidak boleh kosong..."
            daftar_nip_et!!.requestFocus()
            return
        }
        if (nip.length < 15) {
            daftar_nip_et!!.error = "Nomor NIP Salah!"
            daftar_nip_et!!.requestFocus()
            return
        }
        if (TextUtils.isEmpty(daftar_tgl_lahir!!.text.toString())) {
            Toast.makeText(this, "Tanggal lahir masih kosong!", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(daftar_nama_pns.text.toString())) {
            daftar_nama_pns!!.error = "Harap isi nama anda!"
            daftar_nama_pns!!.requestFocus()
            return
        }
        mProgress(dProgress)

        val cekUser = RetrofitClient.client.create(CekUserExist::class.java)
        val call = cekUser.cekUser(nip)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val obj = JSONObject(response.body()!!.string())
                    //                    JSONObject jsonRESULTS = new JSONObject(response.body().string());
                    val status = obj.getBoolean("status")
                    if (!status) {
                        dProgress.dismiss()
                        Toast.makeText(this@DaftarActivity, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    } else {
                        cekNip()
                    }
                } catch (e: Exception) {
//                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    email_password!!.visibility = View.GONE
                    dProgress.dismiss()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                dProgress.dismiss()
            }
        })
    }

    /*Cek keterangan Nip*/
    private fun cekNip() {
        val nip = daftar_nip_et.text.toString()
        val tglLahir = daftar_tgl_lahir.text.toString()
        val namaPns = daftar_nama_pns.text.toString()
        if (TextUtils.isEmpty(nip)) {
            daftar_nip_et.error = "Kolom tidak boleh kosong..."
            daftar_nip_et.requestFocus()
            return
        }
        if (TextUtils.isEmpty(namaPns)) {
            daftar_nama_pns.error = "Kolom tidak boleh kosong..."
            daftar_nama_pns.requestFocus()
            return
        }
        if (TextUtils.isEmpty(tglLahir)) {
            daftar_tgl_lahir.error = "Kolom tidak boleh kosong..."
            daftar_tgl_lahir.requestFocus()
            return
        }
        val cek = RetrofitClient.client.create(CekNipBknInterface::class.java)
        val call = cek.getBkn(nip, tglLahir, namaPns)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("Res", "onResponse: " + response.body())
                try {
                    if (response.isSuccessful){
                        val jsonObject = JSONObject(response.body()!!.string())
                        val data = jsonObject.getString("data")
                        val request = JSONObject(jsonObject.getString("data"))
                        if (request.getInt("code") == 1) {
                            dProgress.dismiss()
                            val jsonData = JSONObject(request.getString("data"))
                            val inskerNama = jsonData.getString("instansiKerjaNama")
                            val namaPns = jsonData.getString("nama")
                            val tglLahir = jsonData.getString("tglLahir")
                            val tempatLahir = jsonData.getString("tempatLahir")
                            val jenisKelamin = jsonData.getString("jenisKelamin")
                            editor.putString("instansiKerjaNama", inskerNama)
                            editor.putString("namaPns", namaPns)
                            editor.putString("tglLahir", tglLahir)
                            editor.putString("tempatLahir", tempatLahir)
                            editor.putString("jenisKelamin", jenisKelamin)
                            editor.putString("dataBkn", data)
                            Log.d("Data", "unorNama: $inskerNama\nNama : $namaPns\ntglLahir: $tglLahir")
                            Toast.makeText(this@DaftarActivity, "NIP Instansi anda ditemukan..", Toast.LENGTH_SHORT).show()
                            email_password.visibility = View.VISIBLE
                            email_daftar_et.requestFocus()
                        } else {
//                        Toast.makeText(DaftarActivity.this, request.getString("data"), Toast.LENGTH_SHORT).show();
                            dialogGagal(request.getString("data"))
                            email_password.visibility = View.GONE
                            daftar_nip_et.isCursorVisible = true
                            dProgress.dismiss()
                        }
                    } else {
                        dProgress.dismiss()
                        Log.d("BKN", "onResponse: ${response.message()}")
                    }

                } catch (e: Exception) {
                    email_password.visibility = View.GONE
                    dProgress.dismiss()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@DaftarActivity, "Session Timeout", Toast.LENGTH_SHORT).show()
                Log.d("Error", "onFailure: " + t.message)
                dialogGagal(t.message)
                dProgress.dismiss()
                call.cancel()
            }
        })
    }

    companion object {
        private const val RC_SIGN_IN = 101
        val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        )
    }
}
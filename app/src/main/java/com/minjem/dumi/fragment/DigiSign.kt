package com.minjem.dumi.fragment

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.minjem.dumi.PelengkapanRegularActivity
import com.minjem.dumi.PersetujuanActivity
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.Helper.mProgress
import com.minjem.dumi.ecommerce.Helper.mToast
import com.minjem.dumi.ecommerce.api.HttpRetrofitClient
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.presenter.DigisignPrestImp
import com.minjem.dumi.presenter.UserPrestImp
import com.minjem.dumi.response.GUser
import com.minjem.dumi.response.RDigisign
import com.minjem.dumi.response.RUser
import com.minjem.dumi.view.DigisignView
import com.minjem.dumi.view.UserView
import id.zelory.compressor.Compressor.compress
import kotlinx.android.synthetic.main.d_webview.*
import kotlinx.android.synthetic.main.fragment_digisign.view.*
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DigiSign : Fragment(),UserView, DigisignView {
    lateinit var v : View
    lateinit var mContext: Context
    lateinit var api : HttpRetrofitClient
    lateinit var userPrestImp : UserPrestImp
    lateinit var digisignPrestImp : DigisignPrestImp
    private val REQUEST_TAKE_PHOTO = 1
    private var currentPhotoPathDiri: String = ""
    private var currentPhotoPathKtp: String = ""
    private var compressedImageDiri: File? = null
    private var compressedImageKTP: File? = null
    private lateinit var dWeb : Dialog
    lateinit var dProgress : Dialog
    private var ktpLast = ""
    private var diriLast = ""
    private var email = ""
    private var nik = ""
    private var cekAktivasi = false
    private var cekAktivasiToast = false
    private var isiNikUlang = false
    lateinit var data : List<GUser>
    private var isfotoCapture = true
    private var diriAtauKtp = false
    private var fromActivity: String? = null
    private var handler = Handler()
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            Log.d("Masuk Handler SUV >>>>",
                    "----------------------------------------- >>>>> $cekAktivasi")
            //digisignPrestImp.data(nik,email)
            if (!cekAktivasi){
                digisignPrestImp.data(nik,email)
                handler.postDelayed(this, 5000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        mContext = this.context!!
        api = HttpRetrofitClient
        dWeb = Dialog(mContext)
        dProgress = Dialog(mContext)
        userPrestImp = UserPrestImp(this)
        digisignPrestImp = DigisignPrestImp(this)

        if (checkPermssion()){
            Log.d("TAG", "onCreate: ${checkPermssion()}")
        } else {
            requestPermission()
        }
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = layoutInflater.inflate(R.layout.fragment_digisign,container,false)
        userPrestImp.data(SharedPrefManager.getInstance(mContext).user.nip,SharedPrefManager.getInstance(mContext).user.password)
        v.id_btn_data_digisign.setOnClickListener {
            //userPrestImp.data(v.id_nip_digisign.text.toString(),v.id_pass_digisign.text.toString())
        }
        v.id_btn_registrasi_digisign.setOnClickListener {
            if (isiNikUlang){
                if (validasiInput()){
                    registrasiDIGISIGN(data)
                }
            } else {
                registrasiDIGISIGN(data)
            }
        }
        v.id_date_picker_digi.setOnClickListener {
            val now = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(mContext, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                now.set(Calendar.YEAR, year)
                now.set(Calendar.MONTH, month)
                now.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd-MM-yyyy"
                val localID = Locale("in", "ID")
                val sdf = SimpleDateFormat(myFormat, localID)
                v.id_tgl_lahir_digisign.setText(sdf.format(now.time))
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }
        v.id_btn_aktivasi_digisign.setOnClickListener {
            handler.removeCallbacks(runnable)
        }
        return v
    }

    private fun validasiInput(): Boolean {
        if (TextUtils.isEmpty(v.id_nik_digisign.text.toString())){
            v.id_nik_digisign.error = "NIK belum di input !"
            return false
        }
        if (TextUtils.isEmpty(v.id_nama_digisign.text.toString())){
            v.id_nama_digisign.error = "Nama belum di input !"
            return false
        }
        if (TextUtils.isEmpty(v.id_tgl_lahir_digisign.text.toString())){
            v.id_tgl_lahir_digisign.error = "Tanggal Lahir belum di input !"
            return false
        }
        return true
    }

    private fun registrasiDIGISIGN(data : List<GUser>) {
        mProgress(dProgress)
        val jenisKelamin = if (data[0].jenis_kelamin == "L") "Laki-laki" else "Perempuan"
        val npwp = if (data[0].npwp != null) data[0].npwp else ""

        if (isfotoCapture){
            val splitKTP = data[0].photo_ktp!!.split("/")
            ktpLast = splitKTP.last()
            val splitDiri = data[0].photo_selfi!!.split("/")
            diriLast = splitDiri.last()
            var namaPns = data[0].namaPns!!
            var noktp1 = data[0].noktp1!!
            var tglLhrPns = data[0].tglLhrPns!!

            if (isiNikUlang){
                namaPns = v.id_nama_digisign.text.toString()
                noktp1 = v.id_nik_digisign.text.toString()
                tglLhrPns = v.id_tgl_lahir_digisign.text.toString()
            }
            Log.d("KTP data",ktpLast)
            Log.d("Diri data",diriLast)
            Log.d("KTP data",namaPns)
            Log.d("Diri data",data[0].no_hp!!)
            Log.d("KTP data",noktp1)
            Log.d("Diri data",data[0].alamat_ktp!!)
            Log.d("KTP data",jenisKelamin)
            Log.d("Diri data",data[0].kecamatan!!)
            Log.d("KTP data",data[0].kelurahan!!)
            Log.d("Diri data",data[0].kodepos!!)
            Log.d("KTP data",data[0].kabkota!!)
            Log.d("Diri data",tglLhrPns)
            Log.d("KTP data",data[0].provinsi!!)
            Log.d("Diri data",data[0].tempatLahir!!)
            Log.d("Diri data",data[0].email!!)
            Log.d("Diri data",npwp!!)
            Log.d("Diri data",ktpLast)
            Log.d("Diri data",diriLast)

            api.retrofit.registrasiDigisignTanpaFoto(namaPns,
                    data[0].no_hp!!,noktp1,data[0].alamat_ktp!!,
                    jenisKelamin,data[0].kecamatan!!,data[0].kelurahan!!,
                    data[0].kodepos!!,data[0].kabkota!!,tglLhrPns,data[0].provinsi!!,
                    data[0].tempatLahir!!,data[0].email!!, npwp,ktpLast,diriLast,"",
                    "",true)
                    .enqueue(object : Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("ERROR","DIGISIGN ERROR REGISTRASI")
                    Log.e("ERROR",t.message!!)
                    dProgress.dismiss()
                    Toast.makeText(mContext,"Koneksi terputus, Coba ulangi lagi !",Toast.LENGTH_SHORT).show()
                }

                @RequiresApi(Build.VERSION_CODES.M)
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful){
                        try {
                            Log.d("RESPONSE DIGISIGN",response.body().toString())
                            val jsonResult = JSONObject(response.body()!!.string())
                            resultDigisign(jsonResult,data)
                        } catch (e: JSONException){
                            e.printStackTrace()
                        } catch (e: IOException){
                            e.printStackTrace()
                        }
                    } else {
                        dProgress.dismiss()
                        Toast.makeText(mContext,"Koneksi terputus, Coba ulangi lagi !",Toast.LENGTH_SHORT).show()
                        Log.e("ERROR","RESPONSE DIGISIGN ERROR")
                    }
                }
            })
        } else {
            if (currentPhotoPathDiri != "" && currentPhotoPathKtp != ""){
                var namaPns = RequestBody.create(MediaType.parse("text/plain"), data[0].namaPns!!)
                val noHp = RequestBody.create(MediaType.parse("text/plain"), data[0].no_hp!!)
                var noktp1 = RequestBody.create(MediaType.parse("text/plain"), data[0].noktp1!!)
                val alamatKtp = RequestBody.create(MediaType.parse("text/plain"), data[0].alamat_ktp.toString())
                val jenisKelaminn = RequestBody.create(MediaType.parse("text/plain"), jenisKelamin)
                val kecamatan = RequestBody.create(MediaType.parse("text/plain"), data[0].kecamatan!!)
                val kelurahan = RequestBody.create(MediaType.parse("text/plain"), data[0].kelurahan!!)
                val kodepos = RequestBody.create(MediaType.parse("text/plain"), data[0].kodepos!!)
                val kabkota = RequestBody.create(MediaType.parse("text/plain"), data[0].kabkota!!)
                var tglLhrPns = RequestBody.create(MediaType.parse("text/plain"), data[0].tglLhrPns!!)
                val provinsi = RequestBody.create(MediaType.parse("text/plain"), data[0].provinsi!!)
                val tempatLahir = RequestBody.create(MediaType.parse("text/plain"), data[0].tempatLahir!!)
                val email = RequestBody.create(MediaType.parse("text/plain"), data[0].email!!)
                val npwpp = RequestBody.create(MediaType.parse("text/plain"), npwp!!)
                val isFoto = RequestBody.create(MediaType.parse("text/plain"), "false")

                if (isiNikUlang){
                    namaPns = RequestBody.create(MediaType.parse("text/plain"), v.id_nama_digisign.text.toString())
                    noktp1 = RequestBody.create(MediaType.parse("text/plain"), v.id_nik_digisign.text.toString())
                    tglLhrPns = RequestBody.create(MediaType.parse("text/plain"), v.id_tgl_lahir_digisign.text.toString())
                }

                val bodyKtp = MultipartBody.Part.createFormData("fotoktp",
                        currentPhotoPathKtp,
                        RequestBody.create(MediaType.parse("image/*"), compressedImageKTP!!))

                val bodyDiri = MultipartBody.Part.createFormData("fotodiri",
                        currentPhotoPathDiri,
                        RequestBody.create(MediaType.parse("image/*"), compressedImageDiri!!))

                val bodyTtd = MultipartBody.Part.createFormData("ttd",
                        "",
                        RequestBody.create(MediaType.parse("image/*"), compressedImageDiri!!))

                val bodyNpwp = MultipartBody.Part.createFormData("fotonpwp",
                        "",
                        RequestBody.create(MediaType.parse("image/*"), compressedImageDiri!!))

                api.retrofit.registrasiDigisignPakaiFoto(namaPns, noHp,noktp1,
                        alamatKtp,jenisKelaminn,kecamatan,kelurahan,kodepos,kabkota,
                        tglLhrPns,provinsi,tempatLahir,email,npwpp,isFoto,bodyKtp,
                        bodyDiri,bodyTtd,bodyNpwp)
                        .enqueue(object : Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("ERROR","DIGISIGN ERROR REGISTRASI PAKAI FOTO")
                        Log.e("ERROR",t.message!!)
                        dProgress.dismiss()
                        Toast.makeText(mContext,"Koneksi terputus, Coba ulangi lagi !",Toast.LENGTH_SHORT).show()
                    }
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful){
                            try {
                                Log.d("RESPONSE DIGISIGN FOTO",response.body().toString())
                                val jsonResult = JSONObject(response.body()!!.string())
                                resultDigisign(jsonResult,data)
                            } catch (e: JSONException){
                                e.printStackTrace()
                            } catch (e: IOException){
                                e.printStackTrace()
                            }
                        } else {
                            dProgress.dismiss()
                            Toast.makeText(mContext,"Koneksi terputus, Coba ulangi lagi !",Toast.LENGTH_SHORT).show()
                            Log.e("ERROR","RESPONSE DIGISIGN ERROR")
                        }
                    }
                })
            } else {
                dProgress.dismiss()
                Toast.makeText(mContext,"Photo Diri / Ktp terlebih dahulu.",Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun resultDigisign(jsonResult: JSONObject, data: List<GUser>) {
        dProgress.dismiss()
        if (jsonResult.getBoolean("status")){
            val dataResponse = jsonResult.getJSONObject("data")
            val jsonFile = dataResponse.getJSONObject("JSONFile")
            val result = jsonFile.getString("result")
            val notif = jsonFile.getString("notif")
            if (result == "00"){
                handler = Handler()

                handler.postDelayed(runnable, 5000)
                if (notif.contains("Berhasil")){
                    Log.d("Contains",notif)
                    webView(jsonFile,false)
                    /*handler.removeCallbacks(runnable)
                    handler.removeCallbacksAndMessages(null)*/
                } else {
                    Log.d("else Contains",notif)
                    Toast.makeText(mContext,notif,Toast.LENGTH_LONG).show()
                    activation(data)
                    /*handler.removeCallbacks(runnable)
                    handler.removeCallbacksAndMessages(null)*/
                }
            } else if(result == "14"){
                Log.d("Result != 00",notif)
                Toast.makeText(mContext,notif,Toast.LENGTH_LONG).show()
                activation(data)
            } else if (result == "12"){
                Toast.makeText(mContext,notif,Toast.LENGTH_LONG).show()
                if (notif.toLowerCase(Locale.ROOT).contains("nik") ||
                        notif.toLowerCase(Locale.ROOT).contains("nama") ||
                        notif.toLowerCase(Locale.ROOT).contains("tanggal lahir")){
                    Toast.makeText(mContext,notif,Toast.LENGTH_LONG).show()
                    v.id_l_tulis_digisign.visibility = View.VISIBLE
                    isiNikUlang = true
                } else {
                    Toast.makeText(mContext,"Verifikasi photo gagal, Coba ulangi lagi",Toast.LENGTH_LONG).show()
                    capturePhoto()
                }
            } else if(result == "05") {
                Toast.makeText(mContext,"Hubungi Admin DUMI, Data foto tidak ditemukan !",Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(mContext,"Koneksi terputus, Coba ulangi lagi !",Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("STATUS ELSE","GAGAL")
            Toast.makeText(mContext,"Verivikasi gagal, Coba konfirmasi admin DUMI.",Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun capturePhoto() {
        v.id_btn_foto_diri_digisign.visibility = View.VISIBLE
        v.id_btn_foto_ktp_digisign.visibility = View.VISIBLE
        v.id_btn_foto_diri_digisign.setOnClickListener {
            isfotoCapture = false
            diriAtauKtp = true
            if (checkPermssion()) {
                dispatchTakePictureIntent()
            } else {
                requestPermission()
            }
        }
        v.id_btn_foto_ktp_digisign.setOnClickListener {
            isfotoCapture = false
            diriAtauKtp = false
            if (checkPermssion()) {
                dispatchTakePictureIntent()
            } else {
                requestPermission()
            }
        }
    }

    private fun activation(data: List<GUser>) {
        Log.d("AKTIVASI FUNC","MASUK")
        v.id_btn_registrasi_digisign.visibility = View.GONE
        v.id_btn_aktivasi_digisign.visibility = View.VISIBLE
        api.retrofit.activationDigisign(data[0].email!!).enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Activation","ERROR")
                Log.e("Activation",t.message!!)
                Toast.makeText(mContext,"Koneksi terputus, Coba ulangi lagi !",Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    Log.d("RESPONSE","RESPONSE SUCCESS")
                    val jsonResult = JSONObject(response.body()!!.string())
                    if (jsonResult.getBoolean("status")){
                        val dataResponse = jsonResult.getJSONObject("data")
                        if (dataResponse.isNull("JSONFile")){
                            Log.d("AKTIVASI","Email sudah teraktivasi")
                            if (dataResponse.getString("result") == "14"){
                                val note = dataResponse.getString("notif")
                                v.id_btn_registrasi_digisign.visibility = View.GONE
                                Toast.makeText(mContext,note,Toast.LENGTH_LONG).show()
                            }
                        } else {
                            val jsonFile = dataResponse.getJSONObject("JSONFile")
                            val result = jsonFile.getString("result")
                            if (result == "00"){
                                webView(jsonFile,true)
                            } else {
                                Log.d("Result != 00",result)
                                Toast.makeText(mContext,"Activation gagal, Coba cek konfirmasi admin DUMI.",Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Log.d("STATUS ELSE","GAGAL")
                        Toast.makeText(mContext,"Activation gagal, Coba cek konfirmasi admin DUMI.",Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("Activation","ERROR RESPONSE")
                    Toast.makeText(mContext,"Koneksi terputus, Coba ulangi lagi !",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webView(jsonFile: JSONObject, boolean : Boolean) {
        dWeb.setContentView(R.layout.d_webview)
        dWeb.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dWeb.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.MATCH_PARENT)
        dWeb.window!!.attributes.windowAnimations = R.style.Animation_Design_BottomSheetDialog
        dWeb.show()
        if (boolean){
            val url = jsonFile.getString("link")
            Log.d("Activation WEB VIEW",url)
            try {
                val web = dWeb.id_webview_digisign
                web.webViewClient = WebViewClient()
                web.settings.loadWithOverviewMode = true
                web.settings.useWideViewPort = true
                web.settings.builtInZoomControls = true
                web.settings.javaScriptEnabled = true
                web.loadUrl(url)
            } catch (e : IOException){
                e.printStackTrace()
            } catch (e : JSONException){
                e.printStackTrace()
            }
        } else {
            val url = jsonFile.getString("info")
            Log.d("REGISTRATION WEB VIEW",url)
            try {
                val web = dWeb.id_webview_digisign
                web.webViewClient = WebViewClient()
                web.settings.loadWithOverviewMode = true
                web.settings.useWideViewPort = true
                web.settings.builtInZoomControls = true
                web.settings.javaScriptEnabled = true
                web.loadUrl(url)
            }catch (e: IOException){
                e.printStackTrace()
            } catch (e : JSONException){
                e.printStackTrace()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            mContext,
                            "com.minjem.dumi.fileprovider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val isNama: String = if (diriAtauKtp)"DIRI" else "KTP"
        return File.createTempFile(
                "${isNama}_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if (diriAtauKtp){
                currentPhotoPathDiri = absolutePath
                Log.d("PHOTO LOCATION",currentPhotoPathDiri)
            } else {
                currentPhotoPathKtp = absolutePath
                Log.d("KTP LOCATION",currentPhotoPathKtp)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
//            ivFotoKtp.setImageURI(Uri.parse(currentPhotoPath))

            val foto: File
            if (diriAtauKtp){
                foto = File(currentPhotoPathDiri)
                Log.d("DIRI PHOTO LOCATION",foto.toString())
            } else {
                foto = File(currentPhotoPathKtp)
                Log.d("KTP PHOTO LOCATION",foto.toString())
            }

            foto.let { imageFile ->
                lifecycleScope.launch {
                    // Default compression
                    if (diriAtauKtp){
                        compressedImageDiri = compress(mContext, imageFile)
                        setCompressedImage()
                    } else {
                        compressedImageKTP = compress(mContext, imageFile)
                        setCompressedImage()
                    }
                }
            }
        }
    }

    private fun showError(errorMessage: String) {
        Log.e("Error F",errorMessage)
        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun setCompressedImage() {
        if (diriAtauKtp){
            compressedImageDiri?.let {
                v.id_image_foto_diri_digisign.setBackgroundColor(Color.GRAY)
                v.id_image_foto_diri_digisign.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
                Log.d("Compressor DIRI", "Compressed image save in " + it.path)
            }
        } else {
            compressedImageKTP?.let {
                v.id_image_foto_ktp_digisign.setBackgroundColor(Color.GRAY)
                v.id_image_foto_ktp_digisign.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
                Log.d("Compressor KTP", "Compressed image save in " + it.path)
            }

        }

    }

    private fun checkPermssion(): Boolean {
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", "onRequestPermissionsResult: $grantResults")
                } else {
                    Toast.makeText(mContext, "Permission Denied...", Toast.LENGTH_SHORT).show()
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission(){
        ActivityCompat.requestPermissions(activity as AppCompatActivity,
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_TAKE_PHOTO)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun response(response: RUser) {
        Log.d("Response Fragment", response.message!!)
        data = response.data!!
        Log.d("DATA",data.toString())
        Log.d("LOGIN", "response: ${SharedPrefManager.getInstance(mContext).user.imageKtp}, ${SharedPrefManager.getInstance(mContext).user.imageSelfi}")

        if (data.isNotEmpty()){
            if (TextUtils.isEmpty(data[0].photo_selfi.toString()) && TextUtils.isEmpty(data[0].photo_ktp.toString())){
                isfotoCapture = false
                v.id_btn_foto_diri_digisign.visibility = View.VISIBLE
                v.id_btn_foto_ktp_digisign.visibility = View.VISIBLE
                capturePhoto()
            } else {
                v.id_image_foto_diri_digisign.setBackgroundColor(Color.GRAY)
                v.id_image_foto_ktp_digisign.setBackgroundColor(Color.GRAY)

                Glide.with(mContext)
                        .load(data[0].photo_selfi)
                        .into(v.id_image_foto_diri_digisign)

                Glide.with(mContext)
                        .load(data[0].photo_ktp)
                        .into(v.id_image_foto_ktp_digisign)

                email   = data[0].email!!
                nik     = data[0].noktp1!!
                isfotoCapture = true
            }
        } else {
            Log.d("IS FOTO CAPTURE","FALSE")
            isfotoCapture = false
            v.id_btn_foto_diri_digisign.visibility = View.VISIBLE
            v.id_btn_foto_ktp_digisign.visibility = View.VISIBLE
            capturePhoto()
        }
    }

    override fun error(text: String) {
        Log.e("Error",text)
    }

    override fun digiResponse(response: RDigisign) {
        Log.d("Masuk Handler SUV >>>>"," ----------------------------------------- >>>>> RESPONSE")
        if(response.data!!.isNotEmpty()){
            if (!cekAktivasiToast){
                dWeb.dismiss()
                mToast(mContext,"Selamat Akun Anda Sudah Teraktivasi")
                v.id_btn_registrasi_digisign.visibility = View.GONE
                v.id_btn_aktivasi_digisign.visibility = View.GONE

            }
            Log.d("Masuk Handler SUV >>>>"," ----------------------------------------- >>>>> STOP")
            cekAktivasiToast = true
            cekAktivasi = true
            Log.d("From Activity", "onCreate: ${arguments!!.getString("activity")}")
            if (arguments!!.getString("activity") == "kilat"){
                val i = Intent(mContext, PersetujuanActivity::class.java)
                i.putExtra("activity", arguments!!.getString("activity").toString())
                startActivity(i)
            } else if(arguments!!.getString("activity") == "regular"){
                val i = Intent(mContext, PelengkapanRegularActivity::class.java)
                i.putExtra("activity", arguments!!.getString("activity").toString())
                startActivity(i)
            }
        } else {
            cekAktivasi = false
            v.id_btn_registrasi_digisign.visibility = View.GONE
            v.id_btn_aktivasi_digisign.visibility = View.VISIBLE
            //mToast(mContext,"NIK belum teraktivasi")
        }
    }

    override fun digiError(error: String) {
        cekAktivasi = false
        Log.e("Error",error)
    }
}
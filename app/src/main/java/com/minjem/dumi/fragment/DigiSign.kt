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
import com.minjem.dumi.MainActivity
import com.minjem.dumi.PelengkapanRegularActivity
import com.minjem.dumi.PersetujuanActivity
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.ECommerceActivity
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
import kotlinx.android.synthetic.main.history_pinjaman.*
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
    val REQUEST_TAKE_PHOTO = 1
    var currentPhotoPathDiri: String = ""
    var currentPhotoPathKtp: String = ""
    private var compressedImageDiri: File? = null
    private var compressedImageKTP: File? = null

    lateinit var dWeb : Dialog
    lateinit var dProgress : Dialog

    var ktpLast = ""
    var diriLast = ""

    var email = ""
    var nik = ""

    var CEK_AKTIVASI = false
    var CEK_AKTIVASI_TOAST = false

    var ISI_NIK_ULANG = false

    lateinit var data : List<GUser>

    var is_foto_capture = true

    var diri_atau_ktp = false

    var data_activation : MutableList<GUser> = ArrayList()

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
        userPrestImp.data("196209111983121001","123456")

        v.id_btn_data_digisign.setOnClickListener {
            //userPrestImp.data(v.id_nip_digisign.text.toString(),v.id_pass_digisign.text.toString())
        }

        v.id_btn_registrasi_digisign.setOnClickListener {
            if (ISI_NIK_ULANG){
                if (validasiInput()){
                    registrasi_DIGISIGN(data)
                }
            } else {
                registrasi_DIGISIGN(data)
            }
        }

        v.id_date_picker_digi.setOnClickListener {
            val now = Calendar.getInstance()
            val datepicker = DatePickerDialog(mContext, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                now.set(Calendar.YEAR, year)
                now.set(Calendar.MONTH, month)
                now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd-MM-yyyy"
                val localID = Locale("in", "ID")
                val sdf = SimpleDateFormat(myFormat, localID)
                v.id_tgl_lahir_digisign.setText(sdf.format(now.getTime()))

            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))

            datepicker.show()
        }

        v.id_btn_aktivasi_digisign.setOnClickListener {
            activation(data_activation)
        }



        return v
    }

    private fun validasiInput(): Boolean {

        if (TextUtils.isEmpty(v.id_nik_digisign.text.toString())){
            v.id_nik_digisign.setError("NIK belum di input !")
            return false
        }

        if (TextUtils.isEmpty(v.id_nama_digisign.text.toString())){
            v.id_nama_digisign.setError("Nama belum di input !")
            return false
        }

        if (TextUtils.isEmpty(v.id_tgl_lahir_digisign.text.toString())){
            v.id_tgl_lahir_digisign.setError("Tanggal Lahir belum di input !")
            return false
        }

        return true
    }

    private fun registrasi_DIGISIGN(data : List<GUser>) {

        mProgress(dProgress)

        val jenis_kelamin = if (data[0].jenis_kelamin == "L") "Laki-laki" else "Perempuan"
        val npwp = if (data[0].npwp != null) data[0].npwp else ""

        if (is_foto_capture){
            val splitKTP = data[0].photo_ktp!!.split("/")
            ktpLast = splitKTP.last()
            val splitDiri = data[0].photo_selfi!!.split("/")
            diriLast = splitDiri.last()

            var namaPns = data[0].namaPns!!
            var noktp1 = data[0].noktp1!!
            var tglLhrPns = data[0].tglLhrPns!!


            if (ISI_NIK_ULANG){
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
            Log.d("KTP data",jenis_kelamin)
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


            api.retrofit.registrasiDigisignTanpaFoto(namaPns,data[0].no_hp!!,noktp1,data[0].alamat_ktp!!,jenis_kelamin,data[0].kecamatan!!,data[0].kelurahan!!,data[0].kodepos!!,data[0].kabkota!!,tglLhrPns,data[0].provinsi!!,data[0].tempatLahir!!,data[0].email!!,npwp!!,ktpLast,diriLast,"","",true).enqueue(object : Callback<ResponseBody>{
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
                val no_hp = RequestBody.create(MediaType.parse("text/plain"), data[0].no_hp!!)
                var noktp1 = RequestBody.create(MediaType.parse("text/plain"), data[0].noktp1!!)
                val alamat_ktp = RequestBody.create(MediaType.parse("text/plain"), data[0].alamat_ktp!!)
                val jenis_kelaminn = RequestBody.create(MediaType.parse("text/plain"), jenis_kelamin)
                val kecamatan = RequestBody.create(MediaType.parse("text/plain"), data[0].kecamatan!!)
                val kelurahan = RequestBody.create(MediaType.parse("text/plain"), data[0].kelurahan!!)
                val kodepos = RequestBody.create(MediaType.parse("text/plain"), data[0].kodepos!!)
                val kabkota = RequestBody.create(MediaType.parse("text/plain"), data[0].kabkota!!)
                var tglLhrPns = RequestBody.create(MediaType.parse("text/plain"), data[0].tglLhrPns!!)
                val provinsi = RequestBody.create(MediaType.parse("text/plain"), data[0].provinsi!!)
                val tempatLahir = RequestBody.create(MediaType.parse("text/plain"), data[0].tempatLahir!!)
                val email = RequestBody.create(MediaType.parse("text/plain"), data[0].email!!)
                val npwpp = RequestBody.create(MediaType.parse("text/plain"), npwp!!)
                val is_foto = RequestBody.create(MediaType.parse("text/plain"), "false")

                if (ISI_NIK_ULANG){
                    namaPns = RequestBody.create(MediaType.parse("text/plain"), v.id_nama_digisign.text.toString())
                    noktp1 = RequestBody.create(MediaType.parse("text/plain"), v.id_nik_digisign.text.toString())
                    tglLhrPns = RequestBody.create(MediaType.parse("text/plain"), v.id_tgl_lahir_digisign.text.toString())
                }

                val body_ktp = MultipartBody.Part.createFormData("fotoktp",
                        currentPhotoPathKtp,
                        RequestBody.create(MediaType.parse("image/*"), compressedImageKTP!!))

                val body_diri = MultipartBody.Part.createFormData("fotodiri",
                        currentPhotoPathDiri,
                        RequestBody.create(MediaType.parse("image/*"), compressedImageDiri!!))

                val body_ttd = MultipartBody.Part.createFormData("ttd",
                        "",
                        RequestBody.create(MediaType.parse("image/*"), compressedImageDiri!!))

                val body_npwp = MultipartBody.Part.createFormData("fotonpwp",
                        "",
                        RequestBody.create(MediaType.parse("image/*"), compressedImageDiri!!))

                api.retrofit.registrasiDigisignPakaiFoto(namaPns,no_hp,noktp1,alamat_ktp,jenis_kelaminn,kecamatan,kelurahan,kodepos,kabkota,tglLhrPns,provinsi,tempatLahir,email,npwpp,is_foto,body_ktp,body_diri,body_ttd,body_npwp).enqueue(object : Callback<ResponseBody>{
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

        data_activation = data as MutableList<GUser>

        dProgress.dismiss()
        if (jsonResult.getBoolean("status")){
            val dataResponse = jsonResult.getJSONObject("data")
            val jsonFile = dataResponse.getJSONObject("JSONFile")
            val result = jsonFile.getString("result")
            val notif = jsonFile.getString("notif")
            if (result == "00"){

                val handler = Handler()
                val runnable: Runnable = object : Runnable {
                    override fun run() {
                        Log.d("Masuk Handler SUV >>>>","----------------------------------------- >>>>> " + CEK_AKTIVASI.toString())
                        //digisignPrestImp.data(nik,email)

                        if (!CEK_AKTIVASI){
                            digisignPrestImp.data(nik,email, arguments!!.getInt("idPinjaman"))
                            handler.postDelayed(this, 5000)
                        }
                    }
                }

                handler.postDelayed(runnable, 5000)

                if (notif.contains("Berhasil")){
                    Log.d("Contains",notif)

                    webView(jsonFile,false)
                } else {
                    Log.d("else Contains",notif)
                    Toast.makeText(mContext,notif,Toast.LENGTH_LONG).show()
                    activation(data)
                }
            } else if(result == "14"){
                Log.d("Result != 00",notif)
                Toast.makeText(mContext,notif,Toast.LENGTH_LONG).show()
                activation(data)
            } else if (result == "12"){
                Toast.makeText(mContext,notif,Toast.LENGTH_LONG).show()

                if (notif.toLowerCase().contains("nik") || notif.toLowerCase().contains("nama") || notif.toLowerCase().contains("tanggal lahir")){
                    Toast.makeText(mContext,notif,Toast.LENGTH_LONG).show()
                    v.id_l_tulis_digisign.visibility = View.VISIBLE
                    ISI_NIK_ULANG = true
                } else {
                    Toast.makeText(mContext,"Verifikasi photo gagal, Coba ulangi lagi",Toast.LENGTH_LONG).show()
                    capture_photo()
                }

            } else if (result == "93"){
                Toast.makeText(mContext,notif,Toast.LENGTH_LONG).show()
                capture_photo()
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
    private fun capture_photo() {
        v.id_btn_foto_diri_digisign.visibility = View.VISIBLE
        v.id_btn_foto_ktp_digisign.visibility = View.VISIBLE
        v.id_btn_foto_diri_digisign.setOnClickListener {
            is_foto_capture = false
            diri_atau_ktp = true
            if (checkPermssion()) {
                dispatchTakePictureIntent()
            } else {
                requestPermission()
            }
        }

        v.id_btn_foto_ktp_digisign.setOnClickListener {
            is_foto_capture = false
            diri_atau_ktp = false
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
        dWeb.toolbarWvDigisign.title = ""
        dWeb.toolbarWvDigisign.setNavigationIcon(R.drawable.ic_back_white)
        dWeb.toolbarWvDigisign.setNavigationOnClickListener {
            dWeb.dismiss()
            startActivity(Intent(mContext, MainActivity::class.java))
        }
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
                            "com.minjem.dumi.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        var is_nama = ""
        if (diri_atau_ktp){
            is_nama = "DIRI"
        } else {
            is_nama = "KTP"
        }

        return File.createTempFile(
                "${is_nama}_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if (diri_atau_ktp){
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

            var foto = File("")

            if (diri_atau_ktp){
                foto = File(currentPhotoPathDiri)
                Log.d("DIRI PHOTO LOCATION",foto.toString())
            } else {
                foto = File(currentPhotoPathKtp)
                Log.d("KTP PHOTO LOCATION",foto.toString())
            }

            foto.let { imageFile ->
                lifecycleScope.launch {
                    // Default compression
                    if (diri_atau_ktp){
                        compressedImageDiri = compress(mContext, imageFile)
                        setCompressedImage()
                    } else {
                        compressedImageKTP = compress(mContext, imageFile)
                        setCompressedImage()
                    }
                }
            } ?: showError("Please choose an image!")
        }
    }

    private fun showError(errorMessage: String) {
        Log.e("Error F",errorMessage)
        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun setCompressedImage() {
        if (diri_atau_ktp){
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
        ActivityCompat.requestPermissions(activity as AppCompatActivity, arrayOf(android.Manifest.permission.CAMERA), REQUEST_TAKE_PHOTO)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun response(response: RUser) {
        Log.d("Response Fragment",response.message!!)
        data = response.data!!
        Log.d("DATA",data.toString())
        if (data.isNotEmpty()){
            if (data[0].photo_selfi != null && data[0].photo_ktp != null){
                v.id_image_foto_diri_digisign.setBackgroundColor(Color.GRAY)
                v.id_image_foto_ktp_digisign.setBackgroundColor(Color.GRAY)
                Glide.with(mContext).load(data[0].photo_selfi).into(v.id_image_foto_diri_digisign)
                Glide.with(mContext).load(data[0].photo_ktp).into(v.id_image_foto_ktp_digisign)

                email   = data[0].email!!
                nik     = data[0].noktp1!!

                is_foto_capture = true

            } else {
                is_foto_capture = false

                v.id_btn_foto_diri_digisign.visibility = View.VISIBLE
                v.id_btn_foto_ktp_digisign.visibility = View.VISIBLE
                capture_photo()
            }
        } else {
            Log.d("IS FOTO CAPTURE","FALSE")
            is_foto_capture = false
            v.id_btn_foto_diri_digisign.visibility = View.VISIBLE
            v.id_btn_foto_ktp_digisign.visibility = View.VISIBLE
            capture_photo()
        }
    }

    override fun error(text: String) {
        Log.e("Error",text)
    }

    override fun digiResponse(response: RDigisign) {
        Log.d("Masuk Handler SUV >>>>","----------------------------------------- >>>>> RESPONSE")
        if(response.data!!.isNotEmpty()){
            if (response.data[0].result == "00"){
                if (response.data[0].result_activation == "00"){
                    dWeb.dismiss()
                    CEK_AKTIVASI = true
                    CEK_AKTIVASI_TOAST = true
                    mToast(mContext,"Selamat Akun Anda Sudah Teraktivasi")
                    v.id_btn_registrasi_digisign.visibility = View.GONE
                    v.id_btn_aktivasi_digisign.visibility = View.GONE
                    activity!!.finish()

                } else {
                    dProgress.dismiss()
                    Log.d("Masuk Handler SUV >>>>","----------------------------------------- >>>>> STOP")
                    CEK_AKTIVASI_TOAST = false
                    CEK_AKTIVASI = false
                    /* val i = Intent(mContext, ECommerceActivity::class.java)
                     i.putExtra("fragment", "digisign")
                     i.putExtra("activity", "kilat")
                     startActivity(i)*/
                }
                dProgress.dismiss()
            }

        } else {
            CEK_AKTIVASI = false

            v.id_btn_registrasi_digisign.visibility = View.GONE
            v.id_btn_aktivasi_digisign.visibility = View.VISIBLE
            //mToast(mContext,"NIK belum teraktivasi")
        }
    }

    override fun digiError(text: String) {
        CEK_AKTIVASI = false
        Log.e("Error",text)
    }
}
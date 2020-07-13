package com.minjem.dumi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.minjem.dumi.api.RegisterInterface
import com.minjem.dumi.api.UploadImageInterface
import com.minjem.dumi.ecommerce.Helper.mToast
import com.minjem.dumi.ecommerce.api.HttpRetrofitClient
import com.minjem.dumi.retrofit.RetrofitClient
import com.minjem.dumi.util.CustomProgressDialog
import com.minjem.dumi.util.FileUtils
import id.zelory.compressor.Compressor.compress
import kotlinx.android.synthetic.main.activity_foto_selfi.*
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FotoSelfiActivity : AppCompatActivity() {
    val REQUEST_TAKE_PHOTO = 1
    lateinit var currentPhotoPath: String
    lateinit var api : HttpRetrofitClient
    private val progressDialog = CustomProgressDialog()
    private var compressedImageKtp: File? = null
    private var compressedImageSelfi: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_selfi)
        api = HttpRetrofitClient
        currentPhotoPath = ""
        if (checkPermssion()){
            Log.d("TAG", "onCreate: ${checkPermssion()}")
        } else {
            requestPermission()
        }

        backFotoSelfi.setOnClickListener { finish() }

        takeImgSelfi.setOnClickListener{
            if (checkPermssion()){
                dispatchTakePictureIntent()
            } else {
                requestPermission()
            }
        }

        bLanjutSelfi.setOnClickListener {
            daftar()
//            uploadImg()
        }
    }


    override fun onStart() {
        super.onStart()
        if (currentPhotoPath.isNotEmpty()){
            tvUlangiFotoSelfi.visibility = View.VISIBLE
            bLanjutSelfi.visibility = View.VISIBLE
            text2.visibility = View.GONE
            takeImgSelfi.visibility = View.GONE
        } else {
            tvUlangiFotoSelfi.visibility = View.GONE
            bLanjutSelfi.visibility = View.GONE
            text2.visibility = View.VISIBLE
            takeImgSelfi.visibility = View.VISIBLE
        }
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_TAKE_PHOTO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "onRequestPermissionsResult: $grantResults")
            } else {
                Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun checkPermssion(): Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            return false
        }
        return true
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.minjem.dumi.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "SELFIE_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            ivFotoKtp.setImageURI(Uri.parse(currentPhotoPath))

            val selfi = File(currentPhotoPath)
            val ktp = File(intent.getStringExtra("ktp")!!.toString())

            selfi.let { imageFile ->

                lifecycleScope.launch {
                    Log.d("KTP Before Compress", "onActivityResult: ${String.format("Selfi Size : %s", FileUtils.getReadableFileSize(imageFile.length().toInt()))}")
                    compressedImageSelfi = compress(this@FotoSelfiActivity, imageFile)
                    setCompressedImageSelfi()
                }
            }

            ktp.let {  imageFile ->
                lifecycleScope.launch {
                    Log.d("Selfi Before Compress", "onActivityResult: ${String.format("Selfi Size : %s", FileUtils.getReadableFileSize(imageFile.length().toInt()))}")
                    compressedImageKtp = compress(this@FotoSelfiActivity, imageFile)
                    setCompressedImage()
                } }



        }
    }

    private fun setCompressedImageSelfi() {
        compressedImageSelfi?.let {
            ivFotoSelfi.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
//            compressedSizeTextView.text = String.format("Size : %s", getReadableFileSize(it.length()))
//            Toast.makeText(this, "Compressed image save in " + it.path, Toast.LENGTH_LONG).show()
            Log.d(" Selfi Size", "setCompressedImage: ${String.format("Selfi Size : %s", FileUtils.getReadableFileSize(it.length().toInt()))}")
            Log.d("Compressor", "Compressed image save in " + it.path)
        }
    }

    private fun setCompressedImage() {
        compressedImageKtp?.let {
//            ivFotoKtp.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
//            compressedSizeTextView.text = String.format("Size : %s", getReadableFileSize(it.length()))
//            Toast.makeText(this, "Compressed image save in " + it.path, Toast.LENGTH_LONG).show()
            Log.d("Size KTP", "setCompressedImage: ${String.format("KTP Size : %s", FileUtils.getReadableFileSize(it.length().toInt()))}")
            Log.d("Compressor", "Compressed image save in " + it.path)
        }
    }


    private fun daftar(){
        val sp = getSharedPreferences("DATA", Context.MODE_PRIVATE)
        progressDialog.show(this,"Membuat akun..")
        Log.d("Test", "daftar: ${sp.getString("nip", "")}" +
                "\nVillage: ${sp.getString("identity_village", "")}")

        /*val nip = sp.getString("nip", null)
        val email = sp.getString("email", "")!!.toString()
        val pass = sp.getString("pass", "")!!.toString()
        val noTelp = sp.getString("noTelp", "")!!.toString()*/



        val regis = RetrofitClient.getClient().create(RegisterInterface::class.java)
        val call = regis.createUser(sp.getString("nip", "")!!.toString()
                , sp.getString("email", "")!!.toString()
                , sp.getString("pass", "")!!.toString()
                ,sp.getString("noTelp", "")!!.toString(),
                sp.getString("noKtp", "")!!.toString(),
                sp.getString("namaPns", "")!!.toString(),
                sp.getString("identity_religion", "")!!.toString(),
                sp.getString("jenisKelamin", "")!!.toString(),
                sp.getString("tempatLahir", "")!!.toString(),
                sp.getString("tglLahir", "")!!.toString(),
                sp.getString("identity_marital_status", "")!!.toString(),
                sp.getString("tanggungan", "")!!.toString(),
                sp.getString("pendidikan", "")!!.toString(),
                sp.getString("pendidikan", "")!!.toString(),
                sp.getString("instansiKerjaNama", "")!!.toString(),
                sp.getString("statusRumah", "")!!.toString(),
                sp.getString("alamat", "")!!.toString(),
                sp.getString("identity_rt", "")!!.toString(),
                sp.getString("identity_rw", "")!!.toString(),
                sp.getString("identity_province", "")!!.toString(),
                sp.getString("identity_city", "")!!.toString(),
                sp.getString("identity_district", "")!!.toString(),
                sp.getString("identity_village", "")!!.toString(),
                sp.getString("pos", "")!!.toString(),
                sp.getString("kdHubungan", "")!!.toString(),
                sp.getString("kdNama", "")!!.toString(),
                sp.getString("kdTelp", "")!!.toString(),
                sp.getString("namaIbu", "")!!.toString(),
                sp.getString("dataBkn", "")!!.toString(),
                sp.getString("featurelist", "")!!.toString(),
                sp.getString("creditscore", "")!!.toString(),
                sp.getString("identity_address", "")!!.toString(),
                sp.getString("identity_match", "")!!.toString(),
                sp.getString("identity_nationnality", "")!!.toString(),
                sp.getString("identity_status", "")!!.toString(),
                sp.getString("identity_work", "")!!.toString(),
                sp.getString("isfacebook", "")!!.toString(),
                sp.getString("iswhatsapp", "")!!.toString(),
                sp.getString("multiphone_idinfo", "")!!.toString(),
                sp.getString("multiphone_phoneinfo_id", "")!!.toString(),
                sp.getString("multiphone_phoneinfo_id_phone", "")!!.toString(),
                sp.getString("multiphone_status", "")!!.toString(),
                sp.getString("phoneage", "")!!.toString(),
                sp.getString("phoneage_status", "")!!.toString(),
                sp.getString("phonealive_id_num", "")!!.toString(),
                sp.getString("phonealive_phone_num", "")!!.toString(),
                sp.getString("phonealive_status", "")!!.toString(),
                sp.getString("phonescore_status", "")!!.toString(),
                sp.getString("npwp", "")!!.toString(),
                sp.getString("namaPasangan", "")!!.toString(),
                sp.getString("noKtpPasangan", "")!!.toString(),
                sp.getString("tglLahirPasangan", "")!!.toString(),
                sp.getString("namaBank", "")!!.toString(),
                sp.getString("namaRekening", "")!!.toString(),
                sp.getString("noRekening", "")!!.toString()
        )

        call.enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", t.message!!)
                progressDialog.dialog.dismiss()
                mToast(this@FotoSelfiActivity, "Server tidak merespon, silahkan coba beberapa saat lagi.")
            }

            @SuppressLint("CommitPrefEdits")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){

                    val jsonObject = JSONObject(response.body()!!.string())
                    if (jsonObject.getBoolean("status")){
//                        mToast(this@FotoSelfiActivity, "Berhasil")
                        val sharedPreference =  getSharedPreferences("DATA",Context.MODE_PRIVATE)
                        val editor = sharedPreference.edit()
                        editor.clear()
                        Log.d("Response", "onResponse: ${jsonObject.getString("message")}")
                        uploadImg()

                    } else {
                        progressDialog.dialog.dismiss()
                        mToast(this@FotoSelfiActivity, "Registrasi gagal, silahkan coba lagi.")
                        Log.d("Response", "onResponse: ${jsonObject.getString("message")}")
                    }

                } else{
                    mToast(this@FotoSelfiActivity, response.message().toString())
                    progressDialog.dialog.dismiss()
                }

            }

        })
    }

    private fun uploadImg(){
        val sp = getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val nip = sp.getString("nip", null)

        val bodyNip = RequestBody.create(MediaType.parse("text/plain"), nip!!.toString())
        val fileKtp = File(intent.getStringExtra("ktp")!!.toString())
        val fileSelfi = File(currentPhotoPath)

        Log.d("Foto", "uploadImg: ${intent.getStringExtra("ktp")}, $currentPhotoPath")

        val bodyKtp = MultipartBody.Part.createFormData("image_ktp",
                intent.getStringExtra("ktp")!!.toString(),
                RequestBody.create(MediaType.parse("image/*"), compressedImageKtp!!))

        val bodySelfi = MultipartBody.Part.createFormData("image_selfi",
                currentPhotoPath,
                RequestBody.create(MediaType.parse("image/*"), compressedImageSelfi!!))

        val upload = RetrofitClient.getClient().create(UploadImageInterface::class.java)
        val call = upload.uploadImages(bodyNip, bodyKtp, bodySelfi)
        call.enqueue(object: Callback<ResponseBody>{
             override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", t.message!!)
                progressDialog.dialog.dismiss()
                mToast(this@FotoSelfiActivity, "Server tidak merespon, silahkan coba beberapa saat lagi.")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    Log.d("Response", "onResponse: ${response.message().toString()}")
                    val jsonObject = JSONObject(response.body()!!.string())
                    if (jsonObject.getBoolean("status")){
                        progressDialog.dialog.dismiss()
//                                Log.d("Response", "onResponse: ${jsonObject.getString("message")}")

                        finish()
                        startActivity(Intent(this@FotoSelfiActivity, MasukActivity::class.java))
                    } else {
                        progressDialog.dialog.dismiss()
                        mToast(this@FotoSelfiActivity, "Registrasi gagal, silahkan coba lagi.")
                        Log.d("Response", "onResponse: ${jsonObject.getString("message")}")
                    }

                } else{
                    mToast(this@FotoSelfiActivity, response.message().toString())
                    progressDialog.dialog.dismiss()
                }
            }

        })
    }
}
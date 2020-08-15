package com.minjem.dumi.akun

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.minjem.dumi.R
import com.minjem.dumi.api.UploadImageInterface
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.model.User
import com.minjem.dumi.retrofit.RetrofitClient
import com.minjem.dumi.util.FileUtils
import id.zelory.compressor.Compressor.compress
import kotlinx.android.synthetic.main.activity_rincian_akun.*
import kotlinx.android.synthetic.main.fragment_akun.view.*
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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RincianAkunActivity : AppCompatActivity() {
    private var imgDp: Uri? = null
    lateinit var currentPhotoPath: String
    private var imgDpIv: ImageView? = null
    lateinit var prop: UploadImageInterface
    lateinit var prefManager: User
    lateinit var pref: SharedPreferences
    private var filePicture: File? = null
    lateinit var editor: SharedPreferences.Editor

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rincian_akun)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            val permission = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permission, 1)
        }

        Glide.with(this).load(R.drawable.ic_camera_2).override(200, 200).into(takeImg)
        /*pref = applicationContext.getSharedPreferences("Profile", 0) // 0 - for private mode
        editor = pref.edit()*/
        prop = RetrofitClient.client.create(UploadImageInterface::class.java)
//        imgDpIv = findViewById(R.id.img_dp_iv)
//        takeImg = findViewById(R.id.takeImg)
//        back = findViewById(R.id.back_rincian)


//        Button kembaliButton = findViewById(R.id.kembali_button);
        prefManager = SharedPrefManager.getInstance(applicationContext).user
        val noKtp = prefManager.getNoKtp()
        val nama = prefManager.getNamaLengkap()
        val agama = prefManager.getAgama()
        val jenisKl = prefManager.getJenisKelamin()
        val tempatTglLhr = prefManager.getTempatLahir() + ", " + prefManager.getTanggalLahir()
        val statusKawin = prefManager.getStatusKawin()
        val jmlTang = prefManager.getJumlahTanggungan()
        val title = prefManager.getTitle() + " (" + prefManager.getKetTitle() + ")"
        val insker = prefManager.getInskerKerja()
        val statusRumah = prefManager.getStatusRumah()
        val rt = prefManager.getRt()
        val rw = prefManager.getRw()
        val provinsi = prefManager.getPropinsi()
        val kelurahan = prefManager.kelurahan
        val kecamatan = prefManager.kecamatan
        val kota = prefManager.kota
        val alamat = prefManager.alamat
        val kodePos = prefManager.getKodePos()
        val noTelp = prefManager.getNoTelp()
        val statushubungan = prefManager.getStatusHubungan()
        val namaKerabat = prefManager.getNamaPenanggung()
        val noKtpKerabat = prefManager.getNoKtpPenanggung()
        val namaIbu = prefManager.getNamaIbu()



        /*rt_rincian.isEnabled = false
        rw_rincian.isEnabled = false
        kelurahan_rincian.isEnabled = false
        kecamatan_rincian.isEnabled = false
        kota_rincian.isEnabled = false
        jenis_kelamin_rincian.isEnabled = false
        tempat_lahir_rincian.isEnabled = false
        status_kawin_rincian.isEnabled = false
        provinsi_rincian.isEnabled = false
        status_hubungan_rincian.isEnabled = false
        nama_kerabat_rincian.isEnabled = false
        no_ktp_kerabat_rincian.isEnabled = false
        nama_ibu_rincian.isEnabled = false*/

        /*rtEt.setText(rt)
        rwEt.setText(rw)
        provinsiEt.setText(provinsi)
        kelurahanEt.setText(kelurahan)
        kecamatanEt.setText(kecamatan)
        kotaEt.setText(kota)
        jenisKEt.setText(jenisKl)
        tempatTglEt.setText(tempatTglLhr)
        statusKawinEt.setText(statusKawin)
        statusHubEt.setText(statushubungan)
        namaKerabatEt.setText(namaKerabat)
        noKtpKerEt.setText(noKtpKerabat)
        namaIbuEt.setText(namaIbu)*/


        no_ktp_rincian.isEnabled = false
        nama_lengkap_rincian.isEnabled = false
        title_rincian.isEnabled = false
        alamat_rincian.isEnabled = false
        kode_pos_rincian.isEnabled = false
        no_telp_rincian.isEnabled = false
        jumlah_tanggungan_rincian.isEnabled = false
        insker_nama_rincian.isEnabled = false
        status_rumah_rincian.isEnabled = false


        no_ktp_rincian.setText(noKtp)
        nama_lengkap_rincian.setText(nama)

//        agama_rincian.setText(agama)

        jumlah_tanggungan_rincian.setText(jmlTang)
        insker_nama_rincian.setText(insker)
        status_rumah_rincian.setText(statusRumah)
        title_rincian.setText(title)

        alamat_rincian.setText(alamat)
        kode_pos_rincian.setText(kodePos)
        no_telp_rincian.setText(noTelp)


        takeImg.setOnClickListener { takeImg() }
        back_rincian.setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        prefManager = SharedPrefManager.getInstance(Objects.requireNonNull(applicationContext)).user
        val apiPhotoPath = SharedPrefManager.getInstance(this).user.imageProfile
        Glide.with(this)
                .load(apiPhotoPath)
                .error(R.drawable.layout_round_corner_beranda)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .transform(CircleCrop(), RoundedCorners(16))
                .into(img_dp_iv)
    }

    private fun uploadProfile() {
        val nipBaru = prefManager.nip

        val nip = RequestBody.create(MediaType.parse("text/plain"), nipBaru)
//        val fileReqBodyProfile = RequestBody.create(MediaType.parse("image/*"), filePicture!!)

        val bodyProfile = MultipartBody.Part.createFormData("image_profile",
                currentPhotoPath, RequestBody.create(MediaType.parse("image/*"), filePicture!!))

        val pDialog = ProgressDialog(this@RincianAkunActivity)
        pDialog.setMessage("Loading...")
        pDialog.show()
        val call = prop.uploadProfile(nip, bodyProfile)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val obj = JSONObject(response.body()!!.string())
                    val status = obj.getBoolean("status")
                    if (status) {
                        pDialog.dismiss()
                        Log.v("Upload", "success" + response.body().toString())
                        Toast.makeText(this@RincianAkunActivity, "Foto profil berhasil diganti", Toast.LENGTH_SHORT).show()

                    } else {
                        pDialog.dismiss()
                        Toast.makeText(this@RincianAkunActivity, "Mohon maaf upload gagal, silahkan mencoba lagi..", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    pDialog.dismiss()
                    Toast.makeText(this@RincianAkunActivity, "Mohon maaf upload gagal, silahkan mencoba lagi..", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    pDialog.dismiss()
                    Toast.makeText(this@RincianAkunActivity, "Mohon maaf upload gagal, silahkan mencoba lagi..", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                pDialog.dismiss()
                Toast.makeText(this@RincianAkunActivity, "Mohon maaf upload gagal, silahkan mencoba lagi..", Toast.LENGTH_SHORT).show()
            }
        })

        Glide.with(this)
                .load(filePicture)
                .error(R.drawable.ic_profil)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .transform(CircleCrop(), RoundedCorners(16))
                .into(img_dp_iv)
    }

    private fun takeImg() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImage()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imgDp = FileProvider.getUriForFile(this,
                        "com.minjem.dumi.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgDp)
                startActivityForResult(takePictureIntent, 1)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImage(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "PP_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
//            Toast.makeText(this, "data: " + data.getData(), Toast.LENGTH_SHORT).show();
            when (requestCode) {
                1 -> {
                    val compressFile = File(currentPhotoPath)
                    Log.d("Camera", "onActivityResult: $currentPhotoPath")
                    compressFile.let { image ->
                        lifecycleScope.launch{
                            filePicture = compress(this@RincianAkunActivity, image)
                            Log.d("Camera", "onActivityResult: ${filePicture.toString()}")
                            comp()
                        }
                    }
//                    uploadProfile()
                }
            }
        }
    }

    private fun comp(){
        filePicture?.let {
            Log.d("Size", "setCompressedImage: ${String.format("Size : %s", FileUtils.getReadableFileSize(it.length().toInt()))}")
            Log.d("Compressor", "Compressed image save in " + it.path)
            uploadProfile()
        }
    }

    /*fun saveBitmapToFile(file: File) {
        try {

            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image
            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val REQUIRED_SIZE = 75

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()

            // here i override the original image file
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        } catch (e: Exception) {
        }
    }*/
}
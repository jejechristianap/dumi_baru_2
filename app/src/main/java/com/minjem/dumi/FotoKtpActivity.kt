package com.minjem.dumi

import android.annotation.SuppressLint
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
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_foto_ktp.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FotoKtpActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_TAKE_PHOTO = 1
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_ktp)
        if (checkPermssion()){
            Log.d("TAG", "onCreate: ${checkPermssion()}")
        } else {
            requestPermission()
        }

        backFotoKtp.setOnClickListener { finish() }

        currentPhotoPath = ""

        takeImgKtp.setOnClickListener{
            if (checkPermssion()){
                dispatchTakePictureIntent()
            } else {
                requestPermission()
            }
        }

        tvUlangiFotoKtp.setOnClickListener{
            dispatchTakePictureIntent()
        }

        bLanjutKtp.setOnClickListener {
            val i = Intent(this, FotoSelfiActivity::class.java)
            i.putExtra("ktp", currentPhotoPath)
            startActivity(i)
        }
    }

    override fun onStart() {
        super.onStart()
        if (currentPhotoPath.isNotEmpty()){
            tvUlangiFotoKtp.visibility = View.VISIBLE
            bLanjutKtp.visibility = View.VISIBLE
            text.visibility = View.GONE
            takeImgKtp.visibility = View.GONE
        } else {
            tvUlangiFotoKtp.visibility = View.GONE
            bLanjutKtp.visibility = View.GONE
            text.visibility = View.VISIBLE
            takeImgKtp.visibility = View.VISIBLE
        }
    }

    private fun checkPermssion(): Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            return false
        }
        return true
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


    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "KTP_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
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

    private fun setPic() {
        // Get the dimensions of the View
        val targetW = ivFotoKtp.width
        val targetH = ivFotoKtp.height
        Log.d("TAG", "setPic: ${ivFotoKtp.width}, ${ivFotoKtp.height}")

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            ivFotoKtp.setImageBitmap(bitmap)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            ivFotoKtp.setImageURI(Uri.parse(currentPhotoPath))
            Glide.with(this)
                    .load(currentPhotoPath)
                    .centerCrop()
                    .into(ivFotoKtp)
//            setPic()
        }
    }



}
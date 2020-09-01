package com.minjem.dumi

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.minjem.dumi.util.FileUtils
import id.zelory.compressor.Compressor
import id.zelory.compressor.Compressor.compress
import kotlinx.android.synthetic.main.activity_foto_ktp.*
import kotlinx.android.synthetic.main.activity_pelengkapan_regular.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

//import com.github.barteksc.pdfviewer.PDFView;
//import org.apache.commons.io.IOUtils;
class PelengkapanRegularActivity : AppCompatActivity() {
    private var uploadSkBt: ImageView? = null
    private var uploadPaBt: ImageView? = null
    private var uploadSuratKBt: ImageView? = null
    private var lanjutBt: Button? = null
    private var ivSkCpns: ImageView? = null
    private var ivPa: ImageView? = null
    private var ivSk: ImageView? = null
    lateinit var fotoSkCpnsPath: String
    lateinit var fotoPaPath: String
    lateinit var fotoSkPath: String
    private var imgSk: Uri? = null
    private var imgPa: Uri? = null
    private var imgSurat: Uri? = null

    private var compressedImageSkcpns: File? = null
    private var compressedImagePa: File? = null
    private var compressedImageSk: File? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pelengkapan_regular)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            val permission = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permission, 1)
        }
        fotoPaPath = ""
        fotoSkCpnsPath = ""
        fotoSkPath = ""

        /*uploadSkBt = findViewById(R.id.upload_sk_button)
        uploadPaBt = findViewById(R.id.upload_pa)
        uploadSuratKBt = findViewById(R.id.upload_surat_kuasa_button)
        lanjutBt = findViewById(R.id.lanjut_button_foto)*/
        upload_sk_button.setOnClickListener { fotoSk() }
        upload_pa.setOnClickListener { fotoPa() }
        upload_surat_kuasa_button.setOnClickListener { fotoSurat() }
        /*kirimFormatButton = findViewById(R.id.kirim_format_pdf_button)
        kirimFormatButton.setOnClickListener(View.OnClickListener { v: View? -> kirimFormat() })*/
        lanjut_button_foto.setOnClickListener(View.OnClickListener { v: View? ->
            if (TextUtils.isEmpty(fotoSkCpnsPath)) {
                Toast.makeText(this, "Foto SK Kosong", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(fotoPaPath)) {
                Toast.makeText(this, "Foto Persetujuan Atasan Kosong", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(fotoSkPath)) {
                Toast.makeText(this, "Foto Surat Kuasa Kosong", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

//            startActivity(new Intent(this, PersetujuanActivity.class));
//            uploadSurat();
            val i = Intent(baseContext, PersetujuanActivity::class.java)
            i.putExtra("fotoSkCpns", fotoSkCpnsPath)
            i.putExtra("fotoSk", fotoSkPath)
            i.putExtra("fotoPa", fotoPaPath)
            i.putExtra("activity", "regular")
            startActivity(i)
        })
    }

    private fun fotoSk() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFileSk()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imgSk = FileProvider.getUriForFile(this,
                        "com.minjem.dumi.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgSk)
                startActivityForResult(takePictureIntent, 1)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFileSk(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "SK" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        fotoSkCpnsPath = image.absolutePath
        return image
    }

    private fun fotoPa() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFilePa()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imgPa = FileProvider.getUriForFile(this,
                        "com.minjem.dumi.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgPa)
                startActivityForResult(takePictureIntent, 2)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFilePa(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "SK" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        fotoPaPath = image.absolutePath
        return image
    }

    private fun fotoSurat() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFileSurat()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imgSurat = FileProvider.getUriForFile(this,
                        "com.minjem.dumi.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgSurat)
                startActivityForResult(takePictureIntent, 3)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFileSurat(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "SK" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        fotoSkPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> {
                    val skcpns = File(fotoSkCpnsPath)
                    skcpns.let {imageFile ->
                        lifecycleScope.launch {
                            compressedImageSkcpns = compress(this@PelengkapanRegularActivity, imageFile)
                            setCompressedImage()
                        }
                    }
                    /*ivSkCpns!!.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivSkCpns!!.setImageURI(Uri.parse(fotoSkCpnsPath))*/
                }
                2 -> {
                    val pa = File(fotoPaPath)
                    pa.let { imageFile ->
                        lifecycleScope.launch {
                            compressedImagePa = compress(this@PelengkapanRegularActivity, imageFile)
                            setCompressedImagePa()
                        }
                    }
                    /*ivPa!!.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivPa!!.setImageURI(Uri.parse(fotoPaPath))*/
                }
                3 -> {
                    val sk = File(fotoSkPath)
                    sk.let { imageFile ->
                        lifecycleScope.launch {
                            compressedImageSk = compress(this@PelengkapanRegularActivity, imageFile)
                            setCompressedImageSk()

                        }
                    }
                    /*ivSk!!.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivSk!!.setImageURI(Uri.parse(fotoSkPath))*/
                }
            }
        }
    }
    private fun setCompressedImage() {
        Glide.with(this).load(compressedImageSkcpns).override(200, 200).into(hasil_foto_skcpns)
        compressedImageSkcpns?.let {
//            hasil_foto_skcpns.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
//            compressedSizeTextView.text = String.format("Size : %s", getReadableFileSize(it.length()))
//            Toast.makeText(this, "Compressed image save in " + it.path, Toast.LENGTH_LONG).show()
            Log.d("Size", "setCompressedImage: ${String.format("Size : %s", FileUtils.getReadableFileSize(it.length().toInt()))}")
            Log.d("Compressor", "Compressed image save in " + it.path)
        }
    }

    private fun setCompressedImagePa() {
        Glide.with(this).load(compressedImagePa).override(200, 200).into(hasil_foto_pa)
        compressedImagePa?.let {
//            hasil_foto_pa.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
//            compressedSizeTextView.text = String.format("Size : %s", getReadableFileSize(it.length()))
//            Toast.makeText(this, "Compressed image save in " + it.path, Toast.LENGTH_LONG).show()
            Log.d("Size", "setCompressedImage: ${String.format("Size : %s", FileUtils.getReadableFileSize(it.length().toInt()))}")
            Log.d("Compressor", "Compressed image save in " + it.path)
        }
    }

    private fun setCompressedImageSk() {
        Glide.with(this).load(compressedImageSk).override(200, 200).into(hasil_foto_surat_kuasa)
        compressedImageSk?.let {
//            hasil_foto_surat_kuasa.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
//            compressedSizeTextView.text = String.format("Size : %s", getReadableFileSize(it.length()))
//            Toast.makeText(this, "Compressed image save in " + it.path, Toast.LENGTH_LONG).show()
            Log.d("Size", "setCompressedImage: ${String.format("Size : %s", FileUtils.getReadableFileSize(it.length().toInt()))}")
            Log.d("Compressor", "Compressed image save in " + it.path)
        }
    }
}

/*private fun kirimFormat() {
    myDialog = AlertDialog.Builder(this@PelengkapanRegularActivity)
    myDialog!!.setCancelable(false)
    inflater = LayoutInflater.from(this@PelengkapanRegularActivity)
    view = inflater.inflate(R.layout.kirim_format_file, null)
    myDialog!!.setView(view)
    myDialog!!.setTitle("Contoh Format Dokumen")
    myDialog!!.setNegativeButton("Tutup") { dialog: DialogInterface?, which: Int -> }
    dialog = myDialog!!.create()
    dialog!!.show()
    val kirimEmailButton = view.findViewById<Button>(R.id.kirim_email_pdf_button)
    val emailEt = view.findViewById<EditText>(R.id.kirim_email_pdf_et)
    val pdfSkp = view.findViewById<Button>(R.id.draf_skp)
    val pdfRek = view.findViewById<Button>(R.id.draf_rek_atasan)
    val fileSk = File(this.cacheDir, "draft_skp.pdf")
    val fileRek = File(this.cacheDir, "draft_rek.pdf")
    val pdfSk = "draft_skp.pdf"
    val pdfRekA = "draft_rek.pdf"

    *//*pdfSkp.setOnClickListener(v -> {
            *//*
        *//*Intent i = new Intent(this, PdfViewerActivity.class);
            i.putExtra("nameFile", "skp");
            startActivity(i);*//*
        *//*

            new DownloadTask(PelengkapanRegularActivity.this, URL_SK);
        });

        pdfRek.setOnClickListener(v -> {
            *//*
        *//*Intent i = new Intent(this, PdfViewerActivity.class);
            i.putExtra("nameFile", "rek");
            startActivity(i);*//*
        *//*
            new DownloadTask(PelengkapanRegularActivity.this, URL_RA);
        });*//*


        *//*kirimEmailButton.setOnClickListener(v -> {
            String email = emailEt.getText().toString();
            if (TextUtils.isEmpty(email)){
                emailEt.setError("Email tidak boleh kosong!");
                return;
            }
            finish();

        });*//*
    }*/
package com.minjem.dumi

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.minjem.dumi.api.BaseApiService
import com.minjem.dumi.api.PinjamanKilatInterface
import com.minjem.dumi.api.UploadImageInterface
import com.minjem.dumi.ecommerce.Helper.mToast
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.retrofit.RetrofitClient
import com.minjem.dumi.util.FileUtils
import id.zelory.compressor.Compressor.compress
import kotlinx.android.synthetic.main.activity_persetujuan.*
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

class PersetujuanActivity : AppCompatActivity() {
    private var fotoSkPath = ""
    private var fotoSkCpnsPath = ""
    private var fotoPaPath = ""
    private var compressImgSkCpns: File? = null
    private var compressImgPa: File? = null
    private var compressImgSk: File? = null
    lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_persetujuan)
        val pref = applicationContext.getSharedPreferences("ajukanPinjaman", 0)

        if (intent.getStringExtra("activity") == "regular"){
            fotoSkCpnsPath = intent.getStringExtra("fotoSkCpns")!!
            fotoPaPath = intent.getStringExtra("fotoPa")!!
            fotoSkPath = intent.getStringExtra("fotoSk")!!
            compressImg()
        }


        cek_setuju.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                ajukan_pinjaman_button.visibility = View.VISIBLE
                //                cekUser();
            } else {
                ajukan_pinjaman_button.visibility = View.GONE
            }
        }

        ajukan_pinjaman_button.setOnClickListener {
            val namaBank = ""
            val noRek = ""
            val pemilik = ""
            val nip = pref.getString("nip", null)
            val pinjaman = pref.getFloat("pinjaman", 0f)
            val plafond = pref.getInt("lamaPinjaman", 0)
            val bunga = pref.getFloat("bunga", 0f)
            val admin = pref.getFloat("admin", 0f)
            val angsuran = pref.getFloat("angsuran", 0f)
            val diterima = pref.getFloat("diterima", 0f)
            val tujuan = pref.getString("tujuan", null)
            val tglMulai = pref.getString("tglMulai", null)
            val tglAkhir = pref.getString("tglAkhir", null)
            val asuransi = pref.getFloat("asuransi", 0f)
            Log.d("Pinjaman",
                    """
                        nip: $nip
                        Pinjaman: $pinjaman
                        Plafond: $plafond
                        Bunga: $bunga
                        Admin: $admin
                        Angsuran: $angsuran
                        Tujuan: $tujuan
                        TglPinjam: $tglMulai
                        AkhirTgl: $tglAkhir
                        Sisa: $diterima
                        asurans: $asuransi
                        Bank: $namaBank
                        noRek: $noRek
                        Nama: $pemilik
                        """.trimIndent())

            /*Progress Dialog*/
            val pDialog = ProgressDialog(this)
            pDialog.setMessage("Memuat Data...")
            pDialog.show()
            val pinjam = RetrofitClient.getClient().create(PinjamanKilatInterface::class.java)
            val call = pinjam.ajukanPinjaman(nip, pinjaman, plafond.toFloat(), 0f,
                    9.9f, bunga, admin, angsuran, 6500f, tujuan,
                    tglMulai, tglAkhir, "", diterima, asuransi, namaBank, noRek, pemilik)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    try {
                        val obj = JSONObject(response.body()!!.string())
                        val status = obj.getBoolean("status")
                        if (status) {
                            pDialog.dismiss()
                            if (intent.getStringExtra("activity") == "regular"){
                                uploadSurat()
                            } else {
                                val handler = Handler()
                                handler.postDelayed({
                                    //Do something after 2s
                                    pushNotificationKilat()
                                }, 3000)
                                finish()
                                message = obj.getString("message")
                                Log.d("Pinjaman Kilat", "onResponse: $message")
                                startActivity(Intent(this@PersetujuanActivity, MainActivity::class.java))

                            }
                        } else {
                            Toast.makeText(this@PersetujuanActivity, "Mohon maaf terjadi kesalahan, silahkan coba beberapa saat lagi.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        pDialog.dismiss()
                        e.printStackTrace()
                    } catch (e: IOException) {
                        pDialog.dismiss()
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    pDialog.dismiss()
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        getInfo()
    }

    private fun compressImg(){
        val fileSkCpns = File(fotoSkCpnsPath)
        val filePa = File(fotoPaPath)
        val fileSk = File(fotoSkPath)

        fileSkCpns.let { imageFile -> lifecycleScope.launch {
            Log.d("SKCPNS Before Compress", "onActivityResult: ${String.format("Size : %s", FileUtils.getReadableFileSize(imageFile.length().toInt()))}")
            compressImgSkCpns = compress(this@PersetujuanActivity, imageFile)
        } }

        filePa.let { imageFile -> lifecycleScope.launch {
            Log.d("PA Before Compress", "onActivityResult: ${String.format("Size : %s", FileUtils.getReadableFileSize(imageFile.length().toInt()))}")
            compressImgPa = compress(this@PersetujuanActivity, imageFile)
        } }

        fileSk.let { imageFile -> lifecycleScope.launch {
            Log.d("SK Before Compress", "onActivityResult: ${String.format("Size : %s", FileUtils.getReadableFileSize(imageFile.length().toInt()))}")
            compressImgSk = compress(this@PersetujuanActivity, imageFile)
        } }
    }

    private fun getInfo(){
        val pDialog = ProgressDialog(this@PersetujuanActivity)
        pDialog.setMessage("Memuat data...")
        pDialog.show()
        val info = RetrofitClient.getClient().create(BaseApiService::class.java)
        val call = info.info
        call.enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", t.message!!)
                pDialog.dismiss()
                mToast(this@PersetujuanActivity, "Server tidak merespon, silahkan coba beberapa saat lagi.")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val jsonObject = JSONObject(response.body()!!.string())
                    if (jsonObject.getBoolean("status")){
                        Log.d("Info", "onResponse: ${jsonObject.getString("message")}")
                        val jsonData = JSONObject(jsonObject.getString("data"))
                        tvInfo.text = jsonData.getString("info")
                        tvIsi.text = jsonData.getString("isi")
                        pDialog.dismiss()
                    } else {
                        Log.d("Info", "onResponse: ${jsonObject.getString("message")}")
                    }
                } else {
                    mToast(this@PersetujuanActivity, response.message().toString())
                    pDialog.dismiss()
                }
            }

        })
    }

    private fun uploadSurat() {
        val pref = SharedPrefManager.getInstance(applicationContext).user
        val nip = pref.nip
        val service = RetrofitClient.getClient().create(UploadImageInterface::class.java)

        /*val fileSkCpns = File(fotoSkCpnsPath!!)
        val filePa = File(fotoPaPath!!)
        val fileSk = File(fotoSkPath!!)*/



        val nipBaru = RequestBody.create(MediaType.parse("text/plain"), nip)
        val fileReqBodySkCpns = RequestBody.create(MediaType.parse("image/*"), compressImgSkCpns!!)
        val fileReqBodyPa = RequestBody.create(MediaType.parse("image/*"), compressImgPa!!)
        val fileReqBodySk = RequestBody.create(MediaType.parse("image/*"), compressImgSk!!)

        val bodySk = MultipartBody.Part.createFormData("img_surat_kuasa", fotoSkPath, fileReqBodySk)
        val bodyPa = MultipartBody.Part.createFormData("img_persetujuan_atasan", fotoPaPath, fileReqBodyPa)
        val bodySkCpns = MultipartBody.Part.createFormData("img_sk_cpns", fotoSkCpnsPath, fileReqBodySkCpns)

        val pDialog = ProgressDialog(this@PersetujuanActivity)
        pDialog.setMessage("Sedang mengirim data...")
        pDialog.show()
        //        Call call = (Call) service.uploadSurat(nipBaru, bodySk, bodyPa, bodySkCpns);
        val call = service.uploadSurat(nipBaru, bodySk, bodyPa, bodySkCpns)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val obj = JSONObject(response.body()!!.string())
                    val status = obj.getBoolean("status")
                    if (status) {
                        pDialog.dismiss()
                        Log.v("Upload", "success" + response.body().toString())
//                        Toast.makeText(this@PersetujuanActivity, "Upload Berhasil", Toast.LENGTH_SHORT).show()
                        val handler = Handler()
                        handler.postDelayed({
                            //Do something after 2s
                            pushNotificationRegular()
                        }, 3000)
                        finish()
                        startActivity(Intent(this@PersetujuanActivity, MainActivity::class.java))
                    } else {
                        pDialog.dismiss()
                        Toast.makeText(this@PersetujuanActivity, "Mohon maaf pengajuan gagal, silahkan mencoba lagi..", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    private fun pushNotificationKilat() {
        // Send Notification
        val mBuilder = NotificationCompat.Builder(applicationContext, "notify_001")
        val ii = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this@PersetujuanActivity, 0, ii, 0)
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText(message)
        bigText.setSummaryText("Pengajuan Pinjaman Kilat")
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.dumi_logo_real)
        mBuilder.setContentTitle("Pinjaman Kilat")
        mBuilder.priority = Notification.PRIORITY_MAX
        mBuilder.setStyle(bigText)
        mBuilder.setAutoCancel(true)
        val mNotificationManager: NotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Your_channel_id"
            val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }
        mNotificationManager.notify(0, mBuilder.build())
    }

    private fun pushNotificationRegular() {
        // Send Notification
        val mBuilder = NotificationCompat.Builder(applicationContext, "notify_001")
        val ii = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this@PersetujuanActivity, 0, ii, 0)
        val bigText = NotificationCompat.BigTextStyle()
        /*bigText.bigText("""Pengajuan anda berhasil kami terima. Proses dapat berlangsung 1-3 hari kerja setelah data anda terverifikasi, mohon menunggu.
                    """.trimIndent())*/
        bigText.setSummaryText("Pengajuan Pinjaman Regular")
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.dumi_logo_real)
        mBuilder.setContentTitle("Pinjaman Regular")
        mBuilder.priority = Notification.PRIORITY_MAX
        mBuilder.setStyle(bigText)
        mBuilder.setAutoCancel(true)
        val mNotificationManager: NotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Your_channel_id"
            val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }
        mNotificationManager.notify(0, mBuilder.build())
    }
}
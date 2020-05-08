package com.minjem.dumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.minjem.dumi.api.PinjamanKilatInterface;
import com.minjem.dumi.api.UploadImageInterface;
import com.minjem.dumi.model.SharedPrefManager;
import com.minjem.dumi.model.User;
import com.minjem.dumi.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersetujuanActivity extends AppCompatActivity {

    private EditText namaBankEt, noRekEt, pemilikEt;
    private Button ajukanButton;
    private CheckBox checkBox;
    private SharedPreferences pref;
    String fotoSkPath, fotoSkCpnsPath, fotoPaPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persetujuan);

        pref = getApplicationContext().getSharedPreferences("ajukanPinjaman", 0);

        fotoSkCpnsPath = getIntent().getStringExtra("fotoSkCpns");
        fotoPaPath = getIntent().getStringExtra("fotoPa");
        fotoSkPath = getIntent().getStringExtra("fotoSk");

        /*namaBankEt = findViewById(R.id.nama_bank_et);
        noRekEt = findViewById(R.id.no_rek_et);
        pemilikEt = findViewById(R.id.nama_pemilik_rek_et);*/
        checkBox = findViewById(R.id.cek_setuju);
        ajukanButton = findViewById(R.id.ajukan_pinjaman_button);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ajukanButton.setVisibility(View.VISIBLE);
//                cekUser();
            } else {
                ajukanButton.setVisibility(View.GONE);
            }
        });


        ajukanButton.setOnClickListener(v -> {
            String namaBank = "";
            String noRek = "";
            String pemilik = "";

            /*if (TextUtils.isEmpty(namaBank)){
                namaBankEt.setError("Kolom ini tidak boleh kosong..");
                namaBankEt.requestFocus();
                return;
            } else {
                namaBankEt.setError(null);
            }

            if (TextUtils.isEmpty(noRek)){
                noRekEt.setError("Kolom ini tidak boleh kosong..");
                noRekEt.requestFocus();
                return;
            } else {
                noRekEt.setError(null);
            }

            if (TextUtils.isEmpty(pemilik)){
                pemilikEt.setError("Kolom ini tidak boleh kosong..");
                pemilikEt.requestFocus();
                return;
            } else {
                pemilikEt.setError(null);
            }*/

            String nip = pref.getString("nip", null);
            float pinjaman = pref.getFloat("pinjaman", 0.f);
            int plafond = pref.getInt("lamaPinjaman", 0);
            float bunga = pref.getFloat("bunga", 0.f);
            float admin = pref.getFloat("admin", 0.f);
            float angsuran = pref.getFloat("angsuran", 0.f);
            float diterima = pref.getFloat("diterima", 0.f);
            String tujuan = pref.getString("tujuan", null);
            String tglMulai = pref.getString("tglMulai", null);
            String tglAkhir = pref.getString("tglAkhir", null);
            float asuransi = pref.getFloat("asuransi", 0.f);

            Log.d("Pinjaman",
                    "nip: " + nip +
                            "\nPinjaman: " + pinjaman +
                            "\nPlafond: " + plafond +
                            "\nBunga: " + bunga +
                            "\nAdmin: " + admin +
                            "\nAngsuran: " + angsuran +
                            "\nTujuan: " + tujuan +
                            "\nTglPinjam: " + tglMulai +
                            "\nAkhirTgl: " + tglAkhir +
                            "\nSisa: " + diterima +
                            "\nasurans: " + asuransi +
                            "\nBank: " + namaBank +
                            "\nnoRek: " + noRek + "\nNama: " + pemilik);

            /*Progress Dialog*/
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Memuat Data...");
            pDialog.show();

            PinjamanKilatInterface pinjam = RetrofitClient.getClient().create(PinjamanKilatInterface.class);
            Call<ResponseBody> call = pinjam.ajukanPinjaman(nip, pinjaman, plafond, 0,
                    9.9f, bunga, admin, angsuran, 6500, tujuan,
                    tglMulai, tglAkhir, "", diterima, asuransi,namaBank, noRek, pemilik);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        boolean status = obj.getBoolean("status");
                        if(status){
                            pDialog.dismiss();
                            if (fotoPaPath == null && fotoSkCpnsPath == null && fotoSkPath == null){
//                                Toast.makeText(PersetujuanActivity.this, "Null", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    //Do something after 2s
                                    pushNotification();
                                }, 3000);
                                finish();
                                startActivity(new Intent(PersetujuanActivity.this, MainActivity.class));
                            }else {
//                                Toast.makeText(PersetujuanActivity.this, "Not Null", Toast.LENGTH_SHORT).show();
                                uploadSurat();
                            }
//                            uploadSurat();
//                            Toast.makeText(PersetujuanActivity.this, "Terima kasih, pengajuan anda akan kami proses.", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(PersetujuanActivity.this, "Mohon maaf terjadi kesalahan, silahkan coba beberapa saat lagi.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                    } catch (IOException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pDialog.dismiss();
                }
            });
        });

    }

    private void uploadSurat(){
        User pref = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String nip = pref.getNip();

        UploadImageInterface service = RetrofitClient.getClient().create(UploadImageInterface.class);
        File fileSkCpns  = new File(fotoSkCpnsPath);
        File filePa = new File(fotoPaPath);
        File fileSk = new File(fotoSkPath);

        RequestBody nipBaru = RequestBody.create(MediaType.parse("text/plain"), nip);
        RequestBody fileReqBodySkCpns = RequestBody.create(MediaType.parse("image/*"), fileSkCpns);
        RequestBody fileReqBodyPa = RequestBody.create(MediaType.parse("image/*"), filePa);
        RequestBody fileReqBodySk = RequestBody.create(MediaType.parse("image/*"), fileSk);

        MultipartBody.Part bodySk = MultipartBody.Part.createFormData("img_surat_kuasa", fotoSkPath, fileReqBodySk);
        MultipartBody.Part bodyPa = MultipartBody.Part.createFormData("img_persetujuan_atasan", fotoPaPath, fileReqBodyPa);
        MultipartBody.Part bodySkCpns = MultipartBody.Part.createFormData("img_sk_cpns", fotoSkCpnsPath, fileReqBodySkCpns);

        ProgressDialog pDialog = new ProgressDialog(PersetujuanActivity.this);
        pDialog.setMessage("Sedang mengupload foto...");
        pDialog.show();
//        Call call = (Call) service.uploadSurat(nipBaru, bodySk, bodyPa, bodySkCpns);
        retrofit2.Call<ResponseBody> call = service.uploadSurat(nipBaru, bodySk, bodyPa,bodySkCpns);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    boolean status = obj.getBoolean("status");
                    if (status){
                        pDialog.dismiss();
                        Log.v("Upload", "success" + response.body().toString());
                        Toast.makeText(PersetujuanActivity.this, "Upload Berhasil", Toast.LENGTH_SHORT).show();

                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            //Do something after 2s
                            pushNotification();
                        }, 3000);

                        finish();
                        startActivity(new Intent(PersetujuanActivity.this, MainActivity.class));
                    } else{
                        pDialog.dismiss();
                        Toast.makeText(PersetujuanActivity.this, "Mohon maaf upload gagal, silahkan mencoba lagi..", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public void pushNotification(){
        // Send Notification
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(PersetujuanActivity.this, 0, ii, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Pengajuan anda berhasil kami terima." +
                "\nProses dapat berlangsung 1-3 hari kerja setelah data anda terverifikasi, mohon menunggu." );
        bigText.setSummaryText("Pengajuan");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_mail);
        mBuilder.setContentTitle("Terima kasih,");
        mBuilder.setContentText("Pengajuan anda akan diproses.");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setAutoCancel(true);
        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }
}

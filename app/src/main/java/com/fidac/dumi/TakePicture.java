package com.fidac.dumi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fidac.dumi.api.RegisterInterface;
import com.fidac.dumi.api.UploadImageInterface;
import com.fidac.dumi.retrofit.RetrofitClient;
import com.fidac.dumi.util.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakePicture extends AppCompatActivity {

    private static final int PERMISSION_CODE_KTP = 1000;
    private static final int IMAGE_CAPTURE_CODE_KTP = 1001;
    private static final int PERMISSION_CODE_SELFI = 1002;
    private static final int IMAGE_CAPTURE_CODE_SELFI = 1003;
    private ImageView imgKtpIv, imgSelfiIv;
    private Button ktpButton, selfiButton, konfirmasiButton;

    private SharedPreferences pref;
    ProgressDialog pDialog;

    private Uri imgKtp, imgSelfi;
    boolean ktp = false;
    boolean selfi = false;
    String filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        pref = getApplicationContext().getSharedPreferences("Daftar", 0 );
        imgKtpIv = findViewById(R.id.iv_ktp);
        imgSelfiIv = findViewById(R.id.iv_selfi);
        ktpButton = findViewById(R.id.ktp_button);
        selfiButton = findViewById(R.id.selfi_button);
        konfirmasiButton = findViewById(R.id.konfirmasi_nomor_telp);
        ktpButton.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED){
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_CODE_KTP);
                } else {
                    openCameraKtp();
                }
            } else {
                openCameraKtp();
            }
        });
        selfiButton.setOnClickListener(v ->{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED){
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_CODE_SELFI);
                } else {
                    openCameraSelfi();
                }
            } else {
                openCameraSelfi();
            }
        });
        konfirmasiButton.setOnClickListener(v -> {
//            uploadFile("197301092000032001", imgKtp, imgSelfi);
            regisUser();
        });
    }
    public void regisUser(){


        /*Progress Dialog*/
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Memuat Data...");
        pDialog.show();

        /*Value from DaftarActivity */
        String nip = pref.getString("nip", null);
        String email = pref.getString("email", null);
        String pass = pref.getString("pass", null);
        /*Value from LengkapiData*/
        String noKtp = pref.getString("no_ktp", null);
        String namaLengkap = pref.getString("nama_lengkap", null);
        String agama = pref.getString("agama", null);
        String jenisKelamin = pref.getString("jenis_kelamin", null);
        String tempatLahir = pref.getString("tempat_lahir", null);
        String tanggalLahir = pref.getString("tanggal_lahir", null);
        String statusKawin = pref.getString("status_kawin", null);
        String jumlahTanggungan = pref.getString("jumlah_tanggungan", null);
        String title = pref.getString("title", null);
        String ketTitle = pref.getString("ket_title", null);
        String inskerKerja = pref.getString("insker", null);
        String statusRumah = pref.getString("status_rumah", null);
        String alamat = pref.getString("alamat", null);
        String rt = pref.getString("rt", null);
        String rw = pref.getString("rw", null);
        String propinsi = pref.getString("propinsi", null);
        String kota = pref.getString("kota", null);
        String kecamatan = pref.getString("kecamatan", null);
        String kelurahan = pref.getString("kelurahan", null);
        String kodePos = pref.getString("kode_pos", null);
        String statusHubungan = pref.getString("status_hubungan", null);
        String namaPenanggung = pref.getString("nama_penanggung", null);
        String noKtpPenanggung = pref.getString("no_ktp_penanggung", null);
        String namaIbu = pref.getString("nama_ibu", null);
        String noTelp = pref.getString("no_telp", null);

        Log.d("Input", "createUser:" + "\nNIP: " + nip + "\nEmail: " + email + "\nPass: " + pass +
                "\nTelp: " + noTelp + "\nKTP: " + noKtp + "\nNama: " + namaLengkap +
                "\nAgama: " + agama + "\nJK: " + jenisKelamin + "\n TempatLahir: " + tempatLahir +
                "\nTanggalLahir: " + tanggalLahir + "\nStatusKawin: " + statusKawin +
                "\nTanggungan: " + jumlahTanggungan + "\nGelar: " + title + "\nKetTit: " + ketTitle +
                "\nInskerKerja: " + inskerKerja + "\nStatusRumah: " + statusRumah + "\nAlamat: " + alamat +
                "\nRt: " + rt + "\nRw: " + rw + "\nPropinsi: " + propinsi + "\nKota: " + kota +
                "\nKecamatan: " + kecamatan + "\nKelurahan: " + kelurahan + "\nPos: " + kodePos +
                "\nStatus Hubungan: " + statusHubungan + "\nNama Penanggung: " + namaPenanggung +
                "\nKtp Penanggung: " + noKtpPenanggung + "\nNama Ibu: " + namaIbu);

        /*API Call Registrasi*/
        RegisterInterface regis = RetrofitClient.getClient().create(RegisterInterface.class);
        Call<ResponseBody> call = regis.createUser(nip, email, pass, noTelp, noKtp,
                namaLengkap, agama, jenisKelamin,tempatLahir, tanggalLahir, statusKawin, jumlahTanggungan,
                title, ketTitle, inskerKerja, statusRumah, alamat, rt, rw, propinsi, kota, kecamatan, kelurahan,
                kodePos, statusHubungan, namaPenanggung, noKtpPenanggung, namaIbu);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Res", "onResponse: " +response.body());
                pDialog.dismiss();
                finish();
                Toast.makeText(TakePicture.this, "Selamat, Pendaftaran Berhasil", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TakePicture.this, MasukActivity.class));
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Error", "onFailure: " + t.getMessage());
                pDialog.dismiss();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(ktp && selfi){
            konfirmasiButton.setVisibility(View.VISIBLE);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            //moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    protected void exitByBackKey() {
        // do something when the button is clicked
        // do something when the button is clicked
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Data yang anda masukkan akan hilang, apa anda yakin?")
                .setPositiveButton("Ya", (arg0, arg1) -> {
                    finish();
                    startActivity(new Intent(TakePicture.this, HalamanDepanActivity.class));
                    //close();
                })
                .setNegativeButton("Tidak", (arg0, arg1) -> {
                })
                .show();
    }

    private void openCameraKtp() {
        ContentValues values = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        values.put(MediaStore.Images.Media.TITLE, "ktp_"+timeStamp);
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imgKtp = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgKtp);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE_KTP);
    }
    private void openCameraSelfi() {
        ContentValues values = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        values.put(MediaStore.Images.Media.TITLE, "selfi_"+timeStamp);
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imgSelfi = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgSelfi);
//        cameraIntent.putExtra()
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE_SELFI);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE_KTP: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCameraKtp();
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSION_CODE_SELFI: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCameraSelfi();
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
//            Toast.makeText(this, "data: " + data.getData(), Toast.LENGTH_SHORT).show();
            switch (requestCode){
                case IMAGE_CAPTURE_CODE_KTP:
                    imgKtpIv.setImageURI(imgKtp);
                    ktp = true;
                    break;
                case IMAGE_CAPTURE_CODE_SELFI:
                    imgSelfiIv.setImageURI(imgSelfi);
                    selfi = true;
                    break;
            }
        }
    }

    /*private void uploadFile(String nip, Uri ktp, Uri selfi) {
        // create upload service client
        UploadImageInterface service = RetrofitClient.getClient().create(UploadImageInterface.class);

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, ktp);

        // create RequestBody instance from file
        RequestBody requestFileKtp =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(ktp)),
                        file
                );
        RequestBody requestFileSelfi =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(selfi)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part bodyKtp =
                MultipartBody.Part.createFormData("ktp", file.getName(), requestFileKtp);
        MultipartBody.Part bodySelfi =
                MultipartBody.Part.createFormData("selfi", file.getName(), requestFileSelfi);

        // add another part within the multipart request
        String descriptionString = "foto ktp";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = service.uploadImages(requestFileKtp, bodyKtp, bodySelfi);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
                Toast.makeText(TakePicture.this, "Yeah", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                Toast.makeText(TakePicture.this, "NOPE", Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}
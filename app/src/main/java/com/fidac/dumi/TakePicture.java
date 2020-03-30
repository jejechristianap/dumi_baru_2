package com.fidac.dumi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fidac.dumi.api.CekNipBknInterface;
import com.fidac.dumi.api.RegisterInterface;
import com.fidac.dumi.api.UploadImageInterface;
import com.fidac.dumi.model.RetrofitClient;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.util.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

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
        String nip = pref.getString("nip", null);
        String email = pref.getString("email", null);
        String pass = pref.getString("pass", null);
        String noKtp = pref.getString("no_ktp", null);
        String namaLengkap = pref.getString("nama_lengkap", null);
        String title = pref.getString("title", null);
        String ketTitle = pref.getString("ket_title", null);
        String rt = pref.getString("rt", null);
        String rw = pref.getString("rw", null);
        String kelurahan = pref.getString("kelurahan", null);
        String kecamatan = pref.getString("kecamatan", null);
        String kota = pref.getString("kota", null);
        String alamat = pref.getString("alamat", null);
        String kodePos = pref.getString("kode_pos", null);
        String jenisKelamin = pref.getString("jenis_kelamin", null);
        String agama = pref.getString("agama", null);
        String noTelp = pref.getString("no_telp", null);
        /*pDialog.setMessage("Terima kasih, data anda sedang diproses...");
        pDialog.show();*/
        Log.d("USER", nip+"\n"+email+"\n"+pass+"\n"+noKtp+"\n"+namaLengkap+"\n"+title+"\n"+ketTitle+"\n"+
                rt+"\n"+rw+"\n"+kelurahan+"\n"+kecamatan+"\n"+kota+"\n"+alamat+"\n"+kodePos+"\n"+jenisKelamin);
        RegisterInterface regis = RetrofitClient.getClient().create(RegisterInterface.class);
        Call<ResponseBody> call = regis.createUser(nip, email, pass, noKtp,
                namaLengkap, jenisKelamin, agama, title, ketTitle, rt, rw, kelurahan,
                kecamatan, kota, alamat, kodePos, noTelp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Res", "onResponse: " +response.body());
//                pDialog.dismiss();
                finish();
                startActivity(new Intent(TakePicture.this, MainActivity.class));
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Error", "onFailure: " + t.getMessage());
//                pDialog.dismiss();
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
                .setMessage("Apa anda yakin ingin keluar?")
                .setPositiveButton("Ya", (arg0, arg1) -> {
                    finish();
                    startActivity(new Intent());
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

    private void uploadFile(String nip, Uri ktp, Uri selfi) {
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
    }
}
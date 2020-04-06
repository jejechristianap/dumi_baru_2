package com.fidac.dumi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.fidac.dumi.api.RegisterInterface;
import com.fidac.dumi.api.UploadImageInterface;
import com.fidac.dumi.retrofit.RetrofitClient;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakePicture extends AppCompatActivity {

    private ImageView imgKtpIv, imgSelfiIv;
    private Button ktpButton, selfiButton, konfirmasiButton;
    private SharedPreferences pref;

    private Uri imgKtp, imgSelfi;
    private boolean ktp = false;
    private boolean selfi = false;
    private String currentPhotoPathKtp, currentPhotoPathSelfi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, 1);
            }
        }


        pref = getApplicationContext().getSharedPreferences("Daftar", 0 );
        imgKtpIv = findViewById(R.id.iv_ktp);
        imgSelfiIv = findViewById(R.id.iv_selfi);
        ktpButton = findViewById(R.id.ktp_button);
        selfiButton = findViewById(R.id.selfi_button);
        konfirmasiButton = findViewById(R.id.konfirmasi_nomor_telp);
        ktpButton.setOnClickListener(v -> {
            takeImgKtp();
        });
        selfiButton.setOnClickListener(v ->{
           takeImgSelfi();
        });
        konfirmasiButton.setOnClickListener(v -> {
            uploadFile();
//            regisUser();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ktp && selfi){

            selfiButton.setBackgroundResource(R.drawable.button_masuk);
            konfirmasiButton.setVisibility(View.VISIBLE);
        }
    }

    /*Take Image ktp*/
    private void takeImgKtp() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFileKtp();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                ktp = true;
                imgKtp = FileProvider.getUriForFile(this,
                        "com.fidac.dumi.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgKtp);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }
    /*Take Image selfi*/
    private void takeImgSelfi() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFileSelfi();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                selfi = true;
                imgSelfi = FileProvider.getUriForFile(this,
                        "com.fidac.dumi.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgSelfi);
                startActivityForResult(takePictureIntent, 2);
            }
        }
    }

    /* Create File path for image ktp */
    private File createImageFileKtp() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "KTP_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPathKtp = image.getAbsolutePath();
        return image;
    }

    /* Create File path for image selfi */
    private File createImageFileSelfi() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SELFI_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPathSelfi = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
            break;
        }
    }
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
//            Toast.makeText(this, "data: " + data.getData(), Toast.LENGTH_SHORT).show();
            switch (requestCode){
                case 1:
                    imgKtpIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imgKtpIv.setImageURI(Uri.parse(currentPhotoPathKtp));
                    ktpButton.setBackgroundResource(R.drawable.button_masuk);
                    ktpButton.setTextColor(R.color.colorBlue);
                    Log.d("KTP", "onActivityResult: " + currentPhotoPathKtp);
//                    ImageCropFunction();

                    break;
                case 2:
                    imgSelfiIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imgSelfiIv.setImageURI(Uri.parse(currentPhotoPathSelfi));
                    selfiButton.setBackgroundResource(R.drawable.button_masuk);
                    selfiButton.setTextColor(R.color.colorBlue);
                    Log.d("SELFI", "onActivityResult: " + currentPhotoPathSelfi);
                    break;

            }
        }
    }

    /* Upload Image to API */
    private void uploadFile() {

        String nipBaru = pref.getString("nip", null);
        // create upload service client
        UploadImageInterface service = RetrofitClient.getClient().create(UploadImageInterface.class);


        RequestBody nip = RequestBody.create(MediaType.parse("text/plain"), nipBaru);

        File fileKtp  = new File(currentPhotoPathKtp);
        File fileSelfi = new File(currentPhotoPathSelfi);

        RequestBody fileReqBodyKtp = RequestBody.create(MediaType.parse("image/*"), fileKtp);
        RequestBody fileReqBodySelfi = RequestBody.create(MediaType.parse("image/*"), fileSelfi);



        // MultipartBody.Part is used to send also the actual param name
        MultipartBody.Part bodyKtp = MultipartBody.Part.createFormData("image_ktp", currentPhotoPathKtp, fileReqBodyKtp);
        MultipartBody.Part bodySelfi = MultipartBody.Part.createFormData("image_selfi", currentPhotoPathSelfi, fileReqBodySelfi);

        // finally, execute the request
        Call<ResponseBody> call = service.uploadImages(nip, bodyKtp, bodySelfi);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success" + response.body().toString());
                Toast.makeText(TakePicture.this, "Upload Berhasil", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                Toast.makeText(TakePicture.this, "Mohon maaf terjadi kesalahan, silahkan coba lagi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Upload user data to API */
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

    /* Back button dialog */
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
}
package com.fidac.dumi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fidac.dumi.api.UploadImageInterface;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;
import com.fidac.dumi.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PelengkapanRegularActivity extends AppCompatActivity {

    private Button uploadSkBt, uploadPaBt, uploadSuratKBt, lanjutBt;
    private ImageView ivSkCpns, ivPa, ivSk;
    private String fotoSkCpnsPath, fotoPaPath, fotoSkPath;
    private Uri imgSk, imgPa, imgSurat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelengkapan_regular);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, 1);
            }
        }

        uploadSkBt = findViewById(R.id.upload_sk_button);
        uploadPaBt = findViewById(R.id.upload_pa);
        uploadSuratKBt = findViewById(R.id.upload_surat_kuasa_button);
        lanjutBt = findViewById(R.id.lanjut_button_foto);

        ivSkCpns = findViewById(R.id.hasil_foto_sk);
        ivPa = findViewById(R.id.hasil_foto_pa);
        ivSk = findViewById(R.id.hasil_foto_surat_kuasa);

        uploadSkBt.setOnClickListener(v -> fotoSk());
        uploadPaBt.setOnClickListener(v -> fotoPa());
        uploadSuratKBt.setOnClickListener(v -> fotoSurat());

        lanjutBt.setOnClickListener(v -> {
            if(TextUtils.isEmpty(fotoSkCpnsPath)){
                Toast.makeText(this, "Foto SK Kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(fotoPaPath)){
                Toast.makeText(this, "Foto Persetujuan Atasan Kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(fotoSkPath)){
                Toast.makeText(this, "Foto Surat Kuasa Kosong", Toast.LENGTH_SHORT).show();
                return;
            }

//            startActivity(new Intent(this, BankActivity.class));
            uploadSurat();
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
        ProgressDialog pDialog = new ProgressDialog(PelengkapanRegularActivity.this);
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
                        Toast.makeText(PelengkapanRegularActivity.this, "Upload Berhasil", Toast.LENGTH_SHORT).show();
//                        regisUser();
                        finish();
                        startActivity(new Intent(PelengkapanRegularActivity.this, BankActivity.class));
                    } else{
                        pDialog.dismiss();
                        Toast.makeText(PelengkapanRegularActivity.this, "Mohon maaf upload gagal, silahkan mencoba lagi..", Toast.LENGTH_SHORT).show();
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

    private void fotoSk() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFileSk();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imgSk = FileProvider.getUriForFile(this,
                        "com.fidac.dumi.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgSk);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }
    private File createImageFileSk() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SK" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        fotoSkCpnsPath = image.getAbsolutePath();
        return image;
    }

    private void fotoPa() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFilePa();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imgPa = FileProvider.getUriForFile(this,
                        "com.fidac.dumi.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgPa);
                startActivityForResult(takePictureIntent, 2);
            }
        }
    }
    private File createImageFilePa() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SK" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        fotoPaPath = image.getAbsolutePath();
        return image;
    }

    private void fotoSurat() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFileSurat();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imgSurat = FileProvider.getUriForFile(this,
                        "com.fidac.dumi.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgSurat);
                startActivityForResult(takePictureIntent, 3);
            }
        }
    }
    private File createImageFileSurat() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SK" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        fotoSkPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
//            Toast.makeText(this, "data: " + data.getData(), Toast.LENGTH_SHORT).show();
            switch (requestCode){
                case 1:
                    ivSkCpns.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ivSkCpns.setImageURI(Uri.parse(fotoSkCpnsPath));
                    break;
                case 2:
                    ivPa.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ivPa.setImageURI(Uri.parse(fotoPaPath));
                    break;
                case 3:
                    ivSk.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ivSk.setImageURI(Uri.parse(fotoSkPath));
                    break;

            }
        }
    }
}

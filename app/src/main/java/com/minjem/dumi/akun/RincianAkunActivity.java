package com.minjem.dumi.akun;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.minjem.dumi.R;
import com.minjem.dumi.api.UploadImageInterface;
import com.minjem.dumi.model.SharedPrefManager;
import com.minjem.dumi.model.User;
import com.minjem.dumi.retrofit.RetrofitClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RincianAkunActivity extends AppCompatActivity {
    private Uri imgDp;
    private String currentPhotoPath;
    private ImageView imgDpIv, takeImg, back;
    private UploadImageInterface prop;
    private User prefManager;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private EditText noKtpEt, namaEt, agamaEt, jenisKEt, tempatTglEt, statusKawinEt,
            jmlTangEt, titleEt, inskerEt, statusRumahEt, rtEt, rwEt, kelurahanEt, kecamatanEt,
            kotaEt, alamatEt, kodePosEt, noTelpEt, provinsiEt, statusHubEt, namaKerabatEt,
            noKtpKerEt, namaIbuEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rincian_akun);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, 1);
            }
        }

        pref = getApplicationContext().getSharedPreferences("Profile", 0); // 0 - for private mode
        editor = pref.edit();
        prop = RetrofitClient.getClient().create(UploadImageInterface.class);

        imgDpIv = findViewById(R.id.img_dp_iv);
        takeImg = findViewById(R.id.takeImg);
        back = findViewById(R.id.back_rincian);



//        Button kembaliButton = findViewById(R.id.kembali_button);


        prefManager = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String noKtp = prefManager.getNoKtp();
        String nama = prefManager.getNamaLengkap();
        String agama = prefManager.getAgama();
        String jenisKl = prefManager.getJenisKelamin();
        String tempatTglLhr = prefManager.getTempatLahir() + ", " + prefManager.getTanggalLahir();
        String statusKawin = prefManager.getStatusKawin();
        String jmlTang = prefManager.getJumlahTanggungan();
        String title = prefManager.getTitle() + " (" + prefManager.getKetTitle() + ")";
        String insker = prefManager.getInskerKerja();
        String statusRumah = prefManager.getStatusRumah();
        String rt = prefManager.getRt();
        String rw = prefManager.getRw();
        String provinsi = prefManager.getPropinsi();
        String kelurahan = prefManager.getKelurahan();
        String kecamatan = prefManager.getKecamatan();
        String kota = prefManager.getKota();
        String alamat = prefManager.getAlamat();
        String kodePos = prefManager.getKodePos();
        String noTelp = prefManager.getNoTelp();
        String statushubungan = prefManager.getStatusHubungan();
        String namaKerabat = prefManager.getNamaPenanggung();
        String noKtpKerabat = prefManager.getNoKtpPenanggung();
        String namaIbu = prefManager.getNamaIbu();

        noKtpEt = findViewById(R.id.no_ktp_rincian);
        namaEt = findViewById(R.id.nama_lengkap_rincian);
        agamaEt = findViewById(R.id.agama_rincian);
        jenisKEt = findViewById(R.id.jenis_kelamin_rincian);
        tempatTglEt = findViewById(R.id.tempat_lahir_rincian);
        statusKawinEt = findViewById(R.id.status_kawin_rincian);
        jmlTangEt = findViewById(R.id.jumlah_tanggungan_rincian);
        titleEt = findViewById(R.id.title_rincian);
        inskerEt = findViewById(R.id.insker_nama_rincian);
        statusRumahEt = findViewById(R.id.status_rumah_rincian);
        rtEt = findViewById(R.id.rt_rincian);
        rwEt = findViewById(R.id.rw_rincian);
        provinsiEt = findViewById(R.id.provinsi_rincian);
        kelurahanEt = findViewById(R.id.kelurahan_rincian);
        kecamatanEt = findViewById(R.id.kecamatan_rincian);
        kotaEt = findViewById(R.id.kota_rincian);
        alamatEt = findViewById(R.id.alamat_rincian);
        kodePosEt = findViewById(R.id.kode_pos_rincian);
        noTelpEt = findViewById(R.id.no_telp_rincian);
        statusHubEt = findViewById(R.id.status_hubungan_rincian);
        namaKerabatEt = findViewById(R.id.nama_kerabat_rincian);
        noKtpKerEt = findViewById(R.id.no_ktp_kerabat_rincian);
        namaIbuEt = findViewById(R.id.nama_ibu_rincian);

        noKtpEt.setEnabled(false);
        namaEt.setEnabled(false);
        agamaEt.setEnabled(false);
        titleEt.setEnabled(false);
        rtEt.setEnabled(false);
        rwEt.setEnabled(false);
        kelurahanEt.setEnabled(false);
        kecamatanEt.setEnabled(false);
        kotaEt.setEnabled(false);
        alamatEt.setEnabled(false);
        kodePosEt.setEnabled(false);
        noTelpEt.setEnabled(false);
        jenisKEt.setEnabled(false);
        tempatTglEt.setEnabled(false);
        statusKawinEt.setEnabled(false);
        jmlTangEt.setEnabled(false);
        inskerEt.setEnabled(false);
        statusRumahEt.setEnabled(false);
        provinsiEt.setEnabled(false);
        statusHubEt.setEnabled(false);
        namaKerabatEt.setEnabled(false);
        noKtpKerEt.setEnabled(false);
        namaIbuEt.setEnabled(false);

        noKtpEt.setText(noKtp);
        namaEt.setText(nama);
        agamaEt.setText(agama);
        jenisKEt.setText(jenisKl);
        tempatTglEt.setText(tempatTglLhr);
        statusKawinEt.setText(statusKawin);
        jmlTangEt.setText(jmlTang);
        inskerEt.setText(insker);
        statusRumahEt.setText(statusRumah);
        titleEt.setText(title);
        rtEt.setText(rt);
        rwEt.setText(rw);
        provinsiEt.setText(provinsi);
        kelurahanEt.setText(kelurahan);
        kecamatanEt.setText(kecamatan);
        kotaEt.setText(kota);
        alamatEt.setText(alamat);
        kodePosEt.setText(kodePos);
        noTelpEt.setText(noTelp);
        statusHubEt.setText(statushubungan);
        namaKerabatEt.setText(namaKerabat);
        noKtpKerEt.setText(noKtpKerabat);
        namaIbuEt.setText(namaIbu);

        takeImg.setOnClickListener(v -> takeImg());
        back.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();

        prefManager = SharedPrefManager.getInstance(Objects.requireNonNull(getApplicationContext())).getUser();
        String apiPhotoPath = prefManager.getImageProfile();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        ImageLoader imageLoader = ImageLoader.getInstance();

        if(apiPhotoPath != null){
            imgDpIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            photoIv.setImageURI(Uri.parse(apiPhotoPath));
            imageLoader.displayImage(apiPhotoPath, imgDpIv);
        }
        ImageLoader.getInstance().destroy();
    }

    private void uploadProfile(){
        String nipBaru = prefManager.getNip();

        RequestBody nip = RequestBody.create(MediaType.parse("text/plain"), nipBaru);
        File fileProfile = new File(currentPhotoPath);
        RequestBody fileReqBodyProfile = RequestBody.create(MediaType.parse("image/*"), fileProfile);
        MultipartBody.Part bodyProfile = MultipartBody.Part.createFormData("image_profile", currentPhotoPath, fileReqBodyProfile);
        ProgressDialog pDialog;
        pDialog = new ProgressDialog(RincianAkunActivity.this);
        pDialog.setMessage("Sedang mengupload foto...");
        pDialog.show();
        Call<ResponseBody> call = prop.uploadProfile(nip, bodyProfile);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    boolean status = obj.getBoolean("status");
                    if (status){
                        pDialog.dismiss();
                        Log.v("Upload", "success" + response.body().toString());
                        Toast.makeText(RincianAkunActivity.this, "Upload Berhasil", Toast.LENGTH_SHORT).show();
                        editor.putString("image_profile", currentPhotoPath);
                        editor.commit();
                    } else{
                        pDialog.dismiss();
                        Toast.makeText(RincianAkunActivity.this, "Mohon maaf upload gagal, silahkan mencoba lagi..", Toast.LENGTH_SHORT).show();
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

    private void takeImg() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImage();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imgDp = FileProvider.getUriForFile(this,
                        "com.minjem.dumi.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgDp);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    private File createImage() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PP_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
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
                    imgDpIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imgDpIv.setImageURI(Uri.parse(currentPhotoPath));
                    Log.d("Profile", "onActivityResult: " + currentPhotoPath);
                    uploadProfile();
//                    ImageCropFunction();

                    break;

            }
        }
    }
}

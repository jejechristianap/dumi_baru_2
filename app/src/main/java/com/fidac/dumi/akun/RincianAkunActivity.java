package com.fidac.dumi.akun;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fidac.dumi.R;
import com.fidac.dumi.fragment.AkunFragment;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RincianAkunActivity extends AppCompatActivity {
    private Uri imgDp;
    private String currentPhotoPath;
    private ImageView imgDpIv, takeImg, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rincian_akun);

        imgDpIv = findViewById(R.id.img_dp_iv);
        takeImg = findViewById(R.id.takeImg);
        back = findViewById(R.id.back_rincian);

        EditText noKtpEt, namaEt, agamaEt, titleEt, rtEt, rwEt, kelurahanEt, kecamatanEt,
                kotaEt, alamatEt, kodePosEt, noTelpEt;

//        Button kembaliButton = findViewById(R.id.kembali_button);


        User prefManager = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String noKtp = prefManager.getNoKtp();
        String nama = prefManager.getNamaLengkap();
        String agama = prefManager.getAgama();
        String title = prefManager.getTitle() + " (" + prefManager.getKetTitle() + ")";
        String rt = prefManager.getRt();
        String rw = prefManager.getRw();
        String kelurahan = prefManager.getKelurahan();
        String kecamatan = prefManager.getKecamatan();
        String kota = prefManager.getKota();
        String alamat = prefManager.getAlamat();
        String kodePos = prefManager.getKodePos();
        String noTelp = prefManager.getNoTelp();

        noKtpEt = findViewById(R.id.no_ktp_rincian);
        namaEt = findViewById(R.id.nama_lengkap_et);
        agamaEt = findViewById(R.id.agama_rincian);
        titleEt = findViewById(R.id.title_rincian);
        rtEt = findViewById(R.id.rt_rincian);
        rwEt = findViewById(R.id.rw_rincian);
        kelurahanEt = findViewById(R.id.kelurahan_rincian);
        kecamatanEt = findViewById(R.id.kecamatan_rincian);
        kotaEt = findViewById(R.id.kota_rincian);
        alamatEt = findViewById(R.id.alamat_rincian);
        kodePosEt = findViewById(R.id.kode_pos_rincian);
        noTelpEt = findViewById(R.id.no_telp_rincian);

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

        noKtpEt.setText(noKtp);
        namaEt.setText(nama);
        agamaEt.setText(agama);
        titleEt.setText(title);
        rtEt.setText(rt);
        rwEt.setText(rw);
        kelurahanEt.setText(kelurahan);
        kecamatanEt.setText(kecamatan);
        kotaEt.setText(kota);
        alamatEt.setText(alamat);
        kodePosEt.setText(kodePos);
        noTelpEt.setText(noTelp);

        takeImg.setOnClickListener(v -> takeImg());
        back.setOnClickListener(v -> finish());
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
                        "com.fidac.dumi.fileprovider",
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
                    Log.d("KTP", "onActivityResult: " + currentPhotoPath);
//                    ImageCropFunction();

                    break;

            }
        }
    }
}

package com.fidac.dumi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePicture extends AppCompatActivity {

    private static final int PERMISSION_CODE_KTP = 1000;
    private static final int IMAGE_CAPTURE_CODE_KTP = 1001;
    private static final int PERMISSION_CODE_SELFI = 1002;
    private static final int IMAGE_CAPTURE_CODE_SELFI = 1003;
    private ImageView imgKtpIv, imgSelfiIv;
    private Button ktpButton, selfiButton;

    private Uri imgKtp, imgSelfi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        imgKtpIv = findViewById(R.id.iv_ktp);
        imgSelfiIv = findViewById(R.id.imv_selfi);

        ktpButton = findViewById(R.id.ktp_button);
        selfiButton = findViewById(R.id.selfi_button);

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
        imgKtp = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgKtp);
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
            imgKtpIv.setImageURI(imgKtp);
            imgSelfiIv.setImageURI(imgSelfi);
        }
    }
}

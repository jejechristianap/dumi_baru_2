package com.fidac.dumi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.fidac.dumi.api.UploadImageInterface;
import com.fidac.dumi.model.DownloadTask;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;
import com.fidac.dumi.retrofit.RetrofitClient;
//import com.github.barteksc.pdfviewer.PDFView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import org.apache.commons.io.IOUtils;

public class PelengkapanRegularActivity extends AppCompatActivity {

    private static final String URL_SK = "http://minjem.com/wp-content/uploads/2020/04/Draft-Surat-Kuasa-Peminjam-1.pdf";
    private static final String URL_RA = "http://minjem.com/wp-content/uploads/2020/04/draftrekomendasiatasan.pdf";
    private Button uploadSkBt, uploadPaBt, uploadSuratKBt, lanjutBt;
    private ImageView ivSkCpns, ivPa, ivSk;
    private String fotoSkCpnsPath, fotoPaPath, fotoSkPath;
    private Uri imgSk, imgPa, imgSurat;

    private Button kirimFormatButton;

    private View view;
    private AlertDialog.Builder myDialog;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    ProgressDialog mProgressDialog;

    private ImageView pdfSkp, pdfRek;
    private InputStream in;

    public static String networkErrorMessage = "Internet tidak tersedia";
    public static boolean checkInternetConnection = true;
    public static boolean showErrorMessage = true;

    boolean isImageFitToScreen;

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



        kirimFormatButton = findViewById(R.id.kirim_format_pdf_button);
        kirimFormatButton.setOnClickListener(v -> {
            kirimFormat();
        });

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
//            uploadSurat();
            Intent i = new Intent(getBaseContext(), BankActivity.class);
            i.putExtra("fotoSkCpns", fotoSkCpnsPath);
            i.putExtra("fotoSk", fotoSkPath);
            i.putExtra("fotoPa", fotoPaPath);
            startActivity(i);
        });

    }

    private void kirimFormat(){
        myDialog = new AlertDialog.Builder(PelengkapanRegularActivity.this);
        myDialog.setCancelable(false);
        inflater = LayoutInflater.from(PelengkapanRegularActivity.this);
        view = inflater.inflate(R.layout.kirim_format_file, null);
        myDialog.setView(view);

        myDialog.setTitle("Contoh Format Dokumen");
        myDialog.setNegativeButton("Tutup", (dialog, which) -> {
//                finish();
        });
        dialog = myDialog.create();
        dialog.show();

        Button kirimEmailButton = view.findViewById(R.id.kirim_email_pdf_button);
        EditText emailEt = view.findViewById(R.id.kirim_email_pdf_et);

        Button pdfSkp = view.findViewById(R.id.draf_skp);
        Button pdfRek = view.findViewById(R.id.draf_rek_atasan);
        File fileSk = new File(this.getCacheDir(), "draft_skp.pdf");
        File fileRek = new File(this.getCacheDir(), "draft_rek.pdf");

        String pdfSk = "draft_skp.pdf";
        String pdfRekA = "draft_rek.pdf";

        pdfSkp.setOnClickListener(v -> {
            /*Intent i = new Intent(this, PdfViewerActivity.class);
            i.putExtra("nameFile", "skp");
            startActivity(i);*/

            new DownloadTask(PelengkapanRegularActivity.this, URL_SK);
        });

        pdfRek.setOnClickListener(v -> {
            /*Intent i = new Intent(this, PdfViewerActivity.class);
            i.putExtra("nameFile", "rek");
            startActivity(i);*/
            new DownloadTask(PelengkapanRegularActivity.this, URL_RA);
        });



        kirimEmailButton.setOnClickListener(v -> {
            String email = emailEt.getText().toString();
            if (TextUtils.isEmpty(email)){
                emailEt.setError("Email tidak boleh kosong!");
                return;
            }
            finish();

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

    /*    public static Boolean isAppAvailable(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        boolean isInstalled;
        try {
            pm.getPackageInfo(appName,PackageManager.GET_ACTIVITIES);
            isInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isInstalled = false;
        }
        return isInstalled;
    }

    public static boolean isNetworkAvailable(Context context) {

        if (checkInternetConnection) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting())
                return true;
            else {
                if (showErrorMessage)
                    Toast.makeText(context, networkErrorMessage, Toast.LENGTH_SHORT).show();

                return false;
            }
        } else
            return true;
    }*/
    /*private void uploadSurat(){
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

    }*/
   /* private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream("/sdcard/Download/Dokumen Rekomendasi Atasan.pdf");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else{
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
            }

        }
    }*/
}



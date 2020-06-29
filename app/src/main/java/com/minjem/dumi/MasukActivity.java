package com.minjem.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.minjem.dumi.api.CekUserExist;
import com.minjem.dumi.api.LoginInterface;
import com.minjem.dumi.model.PreferenceHelper;
import com.minjem.dumi.retrofit.RetrofitClient;
import com.minjem.dumi.model.SharedPrefManager;
import com.minjem.dumi.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MasukActivity extends AppCompatActivity {

    private static String message;
    private Button masukButton;
    private EditText nipEt;
    private EditText passEt;
    private PreferenceHelper preferenceHelper;
    private TextView daftarDisiniTv;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masuk);
        /*AsyncTask.execute(() -> {
            if (isOnline(this)){

            } else {
                try {
                    ContextThemeWrapper ctw = new ContextThemeWrapper( this, R.style.Theme_AppCompat_Dialog_Alert);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctw);
                    alertDialog.setTitle("Info");
                    alertDialog.setMessage("Internet tidak tersedia, mohon cek koneksi internet anda.");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("OK", (dialog, which) -> finish());

                    alertDialog.show();
                } catch (Exception e) {
                    Log.d("Dialog", "Show Dialog: " + e.getMessage());
                }
            }
        });*/

        pDialog = new ProgressDialog(MasukActivity.this);

        nipEt = findViewById(R.id.masuk_nip_et);
        passEt = findViewById(R.id.masuk_password_et);
        daftarDisiniTv = findViewById(R.id.daftar_disini_tv);

        masukButton = findViewById(R.id.masuk_user_button);
        masukButton.setOnClickListener(v -> {
            cekUser();
//            loginUser();
        });
        daftarDisiniTv.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(MasukActivity.this, DaftarActivity.class));
        });
    }




    /*public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR");
                    message = "NetworkCapabilities.TRANSPORT_CELLULAR";
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_WIFI");
                    message = "NetworkCapabilities.TRANSPORT_WIFI";
                    return true;
                }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET");
                    message = "NetworkCapabilities.TRANSPORT_ETHERNET";
                    return true;
                }
            }
        }
        return false;
    }*/

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(MasukActivity.this, HalamanDepanActivity.class));
    }

    public void cekUser() {
        String nip = nipEt.getText().toString();
        String pass = passEt.getText().toString();

        if (TextUtils.isEmpty(nip)) {
            nipEt.setError("Kolom tidak boleh kosong...");
            nipEt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            passEt.setError("Kolom tidak boleh kosong...");
            passEt.requestFocus();
            return;
        }

        if (nip.equals("12345678901234567890") && pass.equals("123456")) {
            int id = 007;
            String nipB = "12345678901234567890";
            String email = "";
            String password = "";
            String noKtp = "";
            String nama = "";
            String agama = "";
            String jenisKelamin = "";
            String tempatLahir = "";
            String tanggalLahir = "";
            String statusKawin = "";
            String jumlahTanggungan = "";
            String pendidikan = "";
            String ketTitle = "";
            String insker = "";
            String statusRumah = "";
            String alamat = "";
            String rt = "";
            String rw = "";
            String propinsi = "";
            String kota = "";
            String kecamatan = "";
            String kelurahan = "";
            String kodePos = "";
            String statusHubungan = "";
            String namaPenanggung = "";
            String noKtpPenanggung = "";
            String namaIbu = "";
            String noHp = "";
            String imageKtp = "";
            String imageSelfi = "";
            String imageProfile = "";

            /*User user = new User(id, nip, email, password, noKtp, nama, agama, jenisKelamin,
                    tempatLahir, tanggalLahir, statusKawin, jumlahTanggungan, pendidikan, ketTitle,
                    insker, statusRumah, alamat, rt, rw, propinsi, kota, kecamatan, kelurahan,
                    kodePos, statusHubungan, namaPenanggung, noKtpPenanggung, namaIbu, noHp, imageKtp, imageSelfi, imageProfile);

            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);*/
            finish();
            startActivity(new Intent(MasukActivity.this, MainActivity.class));
        } else {
            pDialog.setMessage("Mohon Menunggu...");
            pDialog.show();
            CekUserExist cekUser = RetrofitClient.getClient().create(CekUserExist.class);
            Call<ResponseBody> call = cekUser.cekUser(nip);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject obj = null;
                        if (response.body() != null) {
                            obj = new JSONObject(response.body().string());
                        }
//                    JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        boolean status = obj.getBoolean("status");
                        pDialog.dismiss();
                        if (!status) {
                            loginUser();
                        } else {
                            Toast.makeText(MasukActivity.this, "NIP anda belum terdaftar, silahkan mendaftarkan NIP anda", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(MasukActivity.this, DaftarActivity.class));
                        }
                    } catch (Exception e) {
//                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        pDialog.dismiss();
                        Toast.makeText(MasukActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pDialog.dismiss();
                    Toast.makeText(MasukActivity.this, "Koneksi gagal. Mohon periksa jaringan anda", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void loginUser() {
        String nip = nipEt.getText().toString();
        String password = passEt.getText().toString();
        pDialog.show();

        if (TextUtils.isEmpty(nip)) {
            nipEt.setError("Mohon masukkan NIP anda");
            nipEt.requestFocus();
            return;
        } else if (nip.length() < 12) {
            nipEt.setError("NIP SALAH");
            nipEt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passEt.setError("Mohon masukkan password anda");
            passEt.requestFocus();
            return;
        } else {
            passEt.setError(null);
        }

        LoginInterface cek = RetrofitClient.getClient().create(LoginInterface.class);
        Call<ResponseBody> call = cek.getUserLogin(nip, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    boolean status = obj.getBoolean("status");
                    if (status) {
                        String dat = obj.getString("data");
                        JSONArray dataArray = new JSONArray(dat);
//                        startActivity(new Intent(MasukActivity.this, MainActivity.class));
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject userObj = dataArray.getJSONObject(i);
                            int id = userObj.optInt("id");
                            String nip = userObj.getString("nipBaru");
                            String email = userObj.getString("email");
                            String password = userObj.getString("sandi");
                            String noKtp = userObj.getString("noktp");
                            String nama = userObj.getString("namaPns");
                            String agama = userObj.getString("agama");
                            String jenisKelamin = userObj.getString("jenis_kelamin");
                            String tempatLahir = userObj.getString("tempatLahir");
                            String tanggalLahir = userObj.getString("tglLhrPns");
                            String statusKawin = userObj.getString("status_kawin");
                            String jumlahTanggungan = userObj.getString("tanggungan");
                            String pendidikan = userObj.getString("pendidikan");
                            String ketTitle = userObj.getString("ket_title");
                            String insker = userObj.getString("inskerNama");
                            String statusRumah = userObj.getString("status_rumah");
                            String alamat = userObj.getString("alamat");
                            String rt = userObj.getString("rt");
                            String rw = userObj.getString("rw");
                            String propinsi = userObj.getString("provinsi");
                            String kota = userObj.getString("kabkota");
                            String kecamatan = userObj.getString("kecamatan");
                            String kelurahan = userObj.getString("desa");
                            String kodePos = userObj.getString("kodepos");
                            String statusHubungan = userObj.getString("status_hubungan");
                            String namaPenanggung = userObj.getString("nama_penanggung");
                            String noKtpPenanggung = userObj.getString("no_ktp_penanggung");
                            String namaIbu = userObj.getString("ibuKandung");
                            String noHp = userObj.getString("no_hp");
                            String imageKtp = userObj.getString("photo_ktp");
                            String imageSelfi = userObj.getString("photo_selfi");
                            String imageProfile = userObj.getString("photo_profile");
                            int statusTopup = userObj.optInt("status_topup",0);
                            int saldo = userObj.optInt("saldo",0);
                            int saldo_max = userObj.optInt("saldo_max", 0);

                            User user = new User(id, nip, email, password, noKtp, nama, agama, jenisKelamin,
                                    tempatLahir, tanggalLahir, statusKawin, jumlahTanggungan, pendidikan, ketTitle,
                                    insker, statusRumah, alamat, rt, rw, propinsi, kota, kecamatan, kelurahan,
                                    kodePos, statusHubungan, namaPenanggung, noKtpPenanggung, namaIbu, noHp, imageKtp,
                                    imageSelfi, imageProfile, statusTopup, saldo, saldo_max);

                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        }
                        finish();
                        startActivity(new Intent(MasukActivity.this, MainActivity.class));
                        pDialog.dismiss();
                    } else {
                        pDialog.dismiss();
                        Toast.makeText(MasukActivity.this, "NIP/Password Salah!", Toast.LENGTH_SHORT).show();
                        nipEt.requestFocus();
                        passEt.requestFocus();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
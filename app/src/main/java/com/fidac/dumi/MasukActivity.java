package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fidac.dumi.api.BaseApiService;
import com.fidac.dumi.api.LoginInterface;
import com.fidac.dumi.model.PreferenceHelper;
import com.fidac.dumi.retrofit.RetrofitClient;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MasukActivity extends AppCompatActivity {

    private Button masukButton;
    private EditText nipEt;
    private EditText passEt;
    private PreferenceHelper preferenceHelper;
    private TextView daftarDisiniTv;

    Context mContext;
    BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masuk);

        nipEt = findViewById(R.id.masuk_nip_et);
        passEt = findViewById(R.id.masuk_password_et);
        daftarDisiniTv = findViewById(R.id.daftar_disini_tv);

        masukButton = findViewById(R.id.masuk_user_button);
        masukButton.setOnClickListener(v -> {
            loginUser();
        });
        daftarDisiniTv.setOnClickListener(v ->{
            startActivity(new Intent(MasukActivity.this, HalamanDepanActivity.class));
        });
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(MasukActivity.this, HalamanDepanActivity.class));
    }

    private void loginUser() {
        String nip = nipEt.getText().toString();
        String password = passEt.getText().toString();

        if (TextUtils.isEmpty(nip)){
            nipEt.setError("Mohon masukkan NIP anda");
            nipEt.requestFocus();
            return;
        } else if(nip.length() < 12){
            nipEt.setError("NIP SALAH");
            nipEt.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password)){
            passEt.setError("Mohon masukkan password anda");
            passEt.requestFocus();
            return;
        } else {
            passEt.setError(null);
        }
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Mohon Menunggu...");
        pDialog.show();

        LoginInterface cek = RetrofitClient.getClient().create(LoginInterface.class);
        Call<ResponseBody> call = cek.getUserLogin(nip, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                   JSONObject obj = new JSONObject(response.body().string());
                    boolean status = obj.getBoolean("status");
                    if (status) {
                        pDialog.dismiss();
                        String dat = obj.getString("data");
                        JSONArray dataArray = new JSONArray(dat);
//                        startActivity(new Intent(MasukActivity.this, MainActivity.class));
                        for (int i = 0; i < dataArray.length(); i++){
                            JSONObject userObj = dataArray.getJSONObject(i);
                            int id = userObj.getInt("id");
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

                            User user = new User(id, nip, email, password, noKtp, nama, agama, jenisKelamin,
                                    tempatLahir, tanggalLahir, statusKawin, jumlahTanggungan, pendidikan, ketTitle,
                                    insker, statusRumah, alamat, rt, rw, propinsi, kota, kecamatan, kelurahan,
                                    kodePos, statusHubungan, namaPenanggung, noKtpPenanggung, namaIbu, noHp);

                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                            Toast toast= Toast.makeText(MasukActivity.this,
                                    "Selamat datang, " + nama, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                        startActivity(new Intent(MasukActivity.this, MainActivity.class));
                    } else {
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

    private void parseLoginData(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true")) {

                saveInfo(response);

                Toast.makeText(MasukActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MasukActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                this.finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void saveInfo(String response){

        preferenceHelper.putIsLogin(true);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {

                    JSONObject dataobj = dataArray.getJSONObject(i);
                    preferenceHelper.putName(dataobj.getString("name"));
                    preferenceHelper.putHobby(dataobj.getString("hobby"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*private void userLogin() {
        //first getting the values
        final String nip = nipEt.getText().toString();
        final String password = passEt.getText().toString();

        //validating inputs
        if (nip.isEmpty()) {
            nipEt.setError("Please enter your username");
            nipEt.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passEt.setError("Please enter your password");
            passEt.requestFocus();
            return;
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressBar.setVisibility(View.GONE);
                        Log.d("Response", response);
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("data");
                            //if no error in response 12345678901234567890 123123123
                            if (obj.getBoolean("status")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject userObj = jsonArray.getJSONObject(i);
                                    String id = userObj.getString("id_user");
                                    String nip = userObj.getString("nip");
                                    String nama = userObj.getString("nama");
                                    String email = userObj.getString("email");
                                    Toast.makeText(MasukActivity.this, "Selamat datang " + nama , Toast.LENGTH_SHORT).show();
                                    Log.d("User", "nama: " + nama + "\nNip: " + nip + "\nID: " + id);
                                    User user = new User(id, nip, nama, email);
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                }


                                //storing the user in shared preferences
//                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(jsonArray);

                                //starting the profile activity
                                pDialog.dismiss();
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MasukActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nip", nip);
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }*/
}

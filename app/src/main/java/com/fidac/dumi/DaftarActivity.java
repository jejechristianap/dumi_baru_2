package com.fidac.dumi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/*import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fidac.dumi.model.VolleySingleton;
import com.fidac.dumi.util.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;*/
import com.fidac.dumi.api.CekNipBknInterface;
import com.fidac.dumi.model.RetrofitClient;
import com.fidac.dumi.retrofit.NipResources;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

public class DaftarActivity extends AppCompatActivity {

    private EditText masukanNipEt;
    private CheckBox nipCheckBox;

    private TextView nipBaruTv, namaPnsTv, golonganTv, namaJabatanTv, tmtCpnsTv,
                     mkBulanTv, mkTahunTv, inskerNamaTv, tempatLahirTv, tglLhrPnsTv,
                     npwpNomorTv, noKtpTv, tmtPensiunTv, gajiPokokTv, jenisKelaminTv;

    private InputMethodManager imm;

    private LinearLayout dataNipLl;

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        masukanNipEt = findViewById(R.id.daftar_nip_et);
        nipCheckBox = findViewById(R.id.cek_nip);

        dataNipLl = findViewById(R.id.data_nip);

        // Keterangan Data Pribadi
        nipBaruTv = findViewById(R.id.nipBaru);
        namaPnsTv = findViewById(R.id.namaPns);
        golonganTv = findViewById(R.id.golongan);
        namaJabatanTv = findViewById(R.id.namaJabatan);
        tmtCpnsTv = findViewById(R.id.tmtCpns);
        mkBulanTv = findViewById(R.id.mkBulan);
        mkTahunTv = findViewById(R.id.mkTahun);
        inskerNamaTv = findViewById(R.id.inskerNama);
        tempatLahirTv = findViewById(R.id.tempatLahir);
        tglLhrPnsTv = findViewById(R.id.tglLhrPns);
        npwpNomorTv = findViewById(R.id.npwpNomor);
        noKtpTv = findViewById(R.id.noKtp);
        tmtPensiunTv = findViewById(R.id.tmtPensiun);
        gajiPokokTv = findViewById(R.id.gajiPokok);
        jenisKelaminTv = findViewById(R.id.jenis_kelamin);


        nipCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    masukanNipEt.setCursorVisible(false);
                    cekNip();
                }
            }
        });
        /*
        if (TextUtils.isEmpty(email)) {
            masukanEmailEt.setError("Kolom ini tidak boleh kosong..");
            masukanEmailEt.requestFocus();
            return;
        } else if (!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
            masukanEmailEt.setError("Email tidak valid");
            masukanEmailEt.requestFocus();
            return;
        } else {
            masukanEmailEt.setError(null);
            mEmail = true;
        }*/
    }

    public void cekNip() {
        String nip = masukanNipEt.getText().toString();
        if(TextUtils.isEmpty(nip)){
            masukanNipEt.setError("Kolom tidak boleh kosong...");
            masukanNipEt.requestFocus();
            nipCheckBox.setChecked(false);
            return;
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Mohon Menunggu...");
        pDialog.show();

        CekNipBknInterface cek = RetrofitClient.getClient().create(CekNipBknInterface.class);
        /*196404181984032001*/
        /*197301092000032001*/
        // Call with ResponseBody
        Call<ResponseBody> call = cek.cekBkn(nip);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Res", "onResponse: " +response.body());
                try {
                    JSONObject obj = new JSONObject(response.body().string());
//                    JSONObject jsonRESULTS = new JSONObject(response.body().string());
                    boolean status = obj.getBoolean("status");
                    if(status){
                        pDialog.dismiss();
                        dataNipLl.setVisibility(View.VISIBLE);
//                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        Toast.makeText(DaftarActivity.this, "Data ditemukan", Toast.LENGTH_SHORT).show();
                        Log.d("OBJECTRESPONSE", "onResponse124: " + obj);
                        /*196404181984032001*/

                        String dat = obj.getString("data");
                        JSONArray dataArray = new JSONArray(dat);
                        for (int i = 0; i < dataArray.length(); i++){
                            JSONObject dataObj = dataArray.getJSONObject(i);
                            String nipBaru = dataObj.getString("nipBaru");
                            String namaPns = dataObj.getString("namaPns");
                            String golongan = dataObj.getString("golongan");
                            String namaJabatan = dataObj.getString("namaJabatan");
                            String tmtCpns = dataObj.getString("tmtCpns");
                            String mkBulan = dataObj.getString("mkBulan");
                            String mkTahun = dataObj.getString("mkTahun");
                            String inskerNama = dataObj.getString("inskerNama");
                            String tempatLahir = dataObj.getString("tempatLahir");
                            String tglLhrPns = dataObj.getString("tglLhrPns");
                            String npwpNomor = dataObj.getString("npwpNomor");
                            String noKtp = dataObj.getString("noktp");
                            String tmtPensiun = dataObj.getString("tmtPensiun");
                            String gajiPokok = dataObj.getString("gajiPokok");
//                            String jenisKelamin = dataObj.getString("jenis_kelamin");

                            nipBaruTv.setText(nipBaru);
                            namaPnsTv.setText(namaPns);
                            golonganTv.setText(golongan);
                            namaJabatanTv.setText(namaJabatan);
                            tmtCpnsTv.setText(tmtCpns);
                            mkBulanTv.setText(mkBulan);
                            mkTahunTv.setText(mkTahun);
                            inskerNamaTv.setText(inskerNama);
                            tempatLahirTv.setText(tempatLahir);
                            tglLhrPnsTv.setText(tglLhrPns);
                            npwpNomorTv.setText(npwpNomor);
                            noKtpTv.setText(noKtp);
                            tmtPensiunTv.setText(tmtPensiun);
                            gajiPokokTv.setText(gajiPokok);
//                            jenisKelaminTv.setText(jenisKelamin);
                        }
                    }else{
//                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        Log.e("errorMessage", "onResponse: " + obj);
                        dataNipLl.setVisibility(View.GONE);
                        nipCheckBox.setChecked(false);
                        pDialog.dismiss();
                        Toast.makeText(DaftarActivity.this, "Mohon maaf NIP anda belum terdaftar", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
//                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    nipCheckBox.setChecked(false);
                    dataNipLl.setVisibility(View.GONE);
                    pDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dataNipLl.setVisibility(View.GONE);
//                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                Toast.makeText(DaftarActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                Log.d("Error", "onFailure: " + t.getMessage());
                nipCheckBox.setChecked(false);
                pDialog.dismiss();
                call.cancel();
            }
        });

    }
}







/*  Volley Library
    private void cekNipUser() {

        final String cekNip = masukanNipEt.getText().toString();
        if(TextUtils.isEmpty(cekNip)){
            masukanNipEt.setError("Silahkan masukan nip anda");
            masukanNipEt.requestFocus();
            return;
        }
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.URL_CEK_NIP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressBar.setVisibility(View.GONE);
                        Log.d("Response", response);

                        try {
                            //converting response to json object

                            //if no error in response
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("data");

                            if (obj.getBoolean("status")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();




                                */
/*User 1
                                nip: 123456789123456789;
                                pass: dumi123;

                                user 2
                                nip: 147258369147258369;
                                pass: dumi123;

                                user 3
                                nip: 369258147369258147
                                pass: dumi123;

                                user 4
                                nip: 321654987321654987
                                *//*


                                //storing the user in shared preferences
                                //starting the profile activity
                                Toast.makeText(DaftarActivity.this, "NIP Anda terdaftar", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                finish();
//                                Toast.makeText(DaftarActivity.this, "Registrasi berhasil!123", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(DaftarActivity.this, MasukActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                Toast.makeText(DaftarActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                pDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DaftarActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(DaftarActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nip", cekNip);
                return params;
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                String httpPostBody="grant_type=password&username=Alice&password=password123";
                // usually you'd have a field with some values you'd want to escape, you need to do it yourself if overriding getBody. here's how you do it
                try {
                    httpPostBody=httpPostBody+"&randomFieldFilledWithAwkwardCharacters="+ URLEncoder.encode("{{%stuffToBe Escaped/","UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    Log.e("ERROR", "exception", exception);
                    // return null and don't pass any POST string if you encounter encoding error
                    return null;
                }
                return httpPostBody.getBytes();
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    private void registerUser() {
        final String nip = masukanNipEt.getText().toString();
        final String email = masukanEmailEt.getText().toString();
        final String password = masukanPasswordEt.getText().toString();
        final String nama = masukkanNamaEt.getText().toString();

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressBar.setVisibility(View.GONE);
                        Log.d("Response", response);

                        try {
                            //converting response to json object

                            //if no error in response
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("data");
                            //if no error in response 12345678901234567890 123123123
                            if (obj.getBoolean("status")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                *//*User 1
                                nip: 123456789123456789;
                                pass: dumi123;

                                user 2
                                nip: 147258369147258369;
                                pass: dumi123;

                                user 3
                                nip: 369258147369258147
                                pass: dumi123;

                                user 4
                                nip: 321654987321654987
                                *//*



                            //storing the user in shared preferences
                                //starting the profile activity
                                pDialog.dismiss();
                                finish();
//                                Toast.makeText(DaftarActivity.this, "Registrasi berhasil!123", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DaftarActivity.this, MasukActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("email", email);
                params.put("nama", nama);
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }*/



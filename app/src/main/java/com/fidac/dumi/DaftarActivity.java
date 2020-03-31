package com.fidac.dumi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.fidac.dumi.api.CekUserExist;
import com.fidac.dumi.retrofit.RetrofitClient;

import org.json.JSONObject;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaftarActivity extends AppCompatActivity {

    private EditText masukanNipEt;
    private CheckBox nipCheckBox;
    private CheckBox showPassCheckBox;

    /*Hide Soft Keyboard*/
    private InputMethodManager imm;

    private LinearLayout dataNipLl;
    private LinearLayout cekNipLl;
    private LinearLayout emailPassLl;

    private Button lanjutButton;
    private EditText emailEt, passEt, ulangiPassEt;

    private String[] title = {"Tanpa Gelar","D-1","D-2","D-3", "D-4", "S-1", "S-2", "S-3"};
    private String[] agama = {"Islam", "Kristen", "Khatolik", "Budha", "Hindu", "Konghucu"};
    private String[] instansi = {"Pilih Mitra", "PNS", "Non-PNS"};

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    ProgressDialog pDialog;


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

        pref = getApplicationContext().getSharedPreferences("Daftar", 0); // 0 - for private mode
        editor = pref.edit();

        pDialog = new ProgressDialog(DaftarActivity.this);

        /*Linear Layout*/
        cekNipLl = findViewById(R.id.cek_nip_layout);
        emailPassLl = findViewById(R.id.email_password);

        /*Hide Soft Keyboard*/
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        masukanNipEt = findViewById(R.id.daftar_nip_et);
        nipCheckBox = findViewById(R.id.cek_nip);
        showPassCheckBox = findViewById(R.id.show_password);

        lanjutButton = findViewById(R.id.lanjut_button);
        emailEt = findViewById(R.id.email_daftar_et);
        passEt = findViewById(R.id.masuk_password_et);
        ulangiPassEt = findViewById(R.id.ulangi_password_et);

        nipCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                masukanNipEt.setCursorVisible(false);
                cekUser();
//                cekNip();
//                startActivity(new Intent(DaftarActivity.this, TakePicture.class));
            }
        });

        showPassCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ulangiPassEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ulangiPassEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        lanjutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nip = masukanNipEt.getText().toString();
                String email = emailEt.getText().toString();
                String pass = passEt.getText().toString();
                String ulangiPass = ulangiPassEt.getText().toString();

                /*Email Handling*/
                if (TextUtils.isEmpty(email)) {
                    emailEt.setError("Kolom ini tidak boleh kosong..");
                    emailEt.requestFocus();
                    return;
                } else if (!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
                    emailEt.setError("Email tidak valid");
                    emailEt.requestFocus();
                    return;
                } else {
                    emailEt.setError(null);
                }
                /*Password Handling*/
                if (TextUtils.isEmpty(pass)){
                    passEt.setError("Kolom ini tidak boleh kosong..");
                    passEt.requestFocus();
                    return;
                } else if(pass.length() < 6){
                    passEt.setError("Password minimal 6 karakter");
                    passEt.requestFocus();
                    return;
                } else {
                    passEt.setError(null);
                }
                /*Check is password match*/
                if (!ulangiPass.equals(pass)){
                    ulangiPassEt.setError("Password tidak sama..");
                    ulangiPassEt.requestFocus();
                    return;
                } else {
                    ulangiPassEt.setError(null);
                }

                editor.putString("nip", nip);
                editor.putString("email", email);
                editor.putString("pass", pass);
                editor.commit();

                startActivity(new Intent(DaftarActivity.this, OtpVerify.class));

            }
        });


    }

    public void cekUser(){
        String nip = masukanNipEt.getText().toString();
        if(TextUtils.isEmpty(nip)){
            masukanNipEt.setError("Kolom tidak boleh kosong...");
            masukanNipEt.requestFocus();
            nipCheckBox.setChecked(false);
            return;
        }

        pDialog.setMessage("Mohon Menunggu...");
        pDialog.show();
        CekUserExist cekUser = RetrofitClient.getClient().create(CekUserExist.class);
        Call<ResponseBody> call = cekUser.cekUser(nip);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject obj = new JSONObject(response.body().string());
//                    JSONObject jsonRESULTS = new JSONObject(response.body().string());
                    boolean status = obj.getBoolean("status");
                    if (!status) {
                        pDialog.dismiss();
                        Toast.makeText(DaftarActivity.this, "NIP anda sudah terdaftar", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DaftarActivity.this, MasukActivity.class));
                    } else {
                        cekNip();
                    }
                }catch (Exception e) {
//                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    nipCheckBox.setChecked(false);
                    emailPassLl.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void cekNip() {
        String nip = masukanNipEt.getText().toString();
        if(TextUtils.isEmpty(nip)){
            masukanNipEt.setError("Kolom tidak boleh kosong...");
            masukanNipEt.requestFocus();
            nipCheckBox.setChecked(false);
            return;
        }

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
                        emailPassLl.setVisibility(View.VISIBLE);
                        emailEt.requestFocus();
                        Toast.makeText(DaftarActivity.this, "NIP anda ditemukan..", Toast.LENGTH_SHORT).show();
                        Log.d("OBJECTRESPONSE", "onResponse124: " + obj);
                        /*196404181984032001*/
                        /*String dat = obj.getString("data");
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
                        }*/
                    }else{
//                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        Log.e("errorMessage", "onResponse: " + obj);
                        nipCheckBox.setChecked(false);
                        masukanNipEt.setCursorVisible(true);
                        pDialog.dismiss();
                        Toast.makeText(DaftarActivity.this, "Mohon maaf Instansi anda belum bekerja sama", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
//                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    nipCheckBox.setChecked(false);
                    emailPassLl.setVisibility(View.GONE);
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



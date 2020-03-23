package com.fidac.dumi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DaftarActivity extends AppCompatActivity {
    private EditText masukanNipEt;
    private EditText masukanEmailEt;
    private EditText masukanPasswordEt;
    private EditText cekPasswordEt;
    private EditText masukkanNamaEt;
    private Boolean isValid;

    private Session session;

    private CheckBox passwordCheckBox;
    private CheckBox nipCheckBox;

    private Button lanjutButton;

    private Switch syaratSwitch;

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

//        masukanNoTelp = findViewById(R.id.daftar_no_telp_et);
        masukanNipEt = findViewById(R.id.daftar_nip_et);
        masukanEmailEt = findViewById(R.id.daftar_email_et);
        masukanPasswordEt = findViewById(R.id.daftar_password_et);
        cekPasswordEt = findViewById(R.id.daftar_ulangi_password_et);

        masukkanNamaEt = findViewById(R.id.daftar_nama_et);

        nipCheckBox = findViewById(R.id.cek_nip);
        passwordCheckBox = findViewById(R.id.checkbox_password);
        lanjutButton = findViewById(R.id.daftar_lanjut_button);
        syaratSwitch = findViewById(R.id.switch_syarat);

        nipCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    /*cekNipUser();*/
                    cekNip();
                }
            }
        });

        passwordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /*Show Password*/
                    masukanPasswordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    cekPasswordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    /*Hide Password*/
                    masukanPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    cekPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        syaratSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lanjutButton.setEnabled(true);
                lanjutButton.setBackgroundResource(R.drawable.button_design_login_register);
                lanjutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nip = masukanNipEt.getText().toString();
                        String email = masukanEmailEt.getText().toString();
                        String password = masukanPasswordEt.getText().toString();
                        String cekPass = cekPasswordEt.getText().toString();
                        String nama = masukkanNamaEt.getText().toString();
//                        String number = masukanNoTelp.getText().toString();
                        boolean mNip = false;
                        boolean mEmail = false;
                        boolean mCekPass = false;
                        boolean mNama = false;

                        if (TextUtils.isEmpty(nip)) {
                            masukanNipEt.setError("Kolom ini tidak boleh kosong..");
                            masukanNipEt.requestFocus();
                        } else {
                            masukanNipEt.setError(null);
                            mNip = true;
                        }

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
                        }

                        if (TextUtils.isEmpty(nama)) {
                            masukkanNamaEt.setError("Kolom ini tidak boleh kosong..");
                            masukkanNamaEt.requestFocus();
                        } else {
                            masukkanNamaEt.setError(null);
                        }

                        if (TextUtils.isEmpty(password)) {
                            masukanPasswordEt.setError("Kolom ini tidak boleh kosong..");
                        } else if (password.length() < 6) {
                            masukanPasswordEt.setError("Minimal Password 6 Karakter");
                        } else {
                            masukanPasswordEt.setError(null);
                        }

                        if (TextUtils.isEmpty(cekPass)) {
                            cekPasswordEt.setError("Kolom ini tidak boleh kosong..");
                        } else if (!cekPass.equals(password)) {
                            cekPasswordEt.setError("Password Tidak Sama");

                        } else {
                            cekPasswordEt.setError(null);
                            mCekPass = true;
                        }

                        if (!mNip || !mEmail || !mCekPass) {

                        } else {
                            /*registerUser();*/
                            /*cekNip();*/
                        }

                    }
                });
                if (!isChecked) {
                    lanjutButton.setEnabled(false);
                    lanjutButton.setBackgroundResource(R.drawable.button_lanjut_design);
                }
            }
        });
    }

    public void cekNip() {
        String nip = masukanNipEt.getText().toString();
        if(TextUtils.isEmpty(nip)){
            masukanNipEt.setError("Kolom tidak boleh kosong...");
            masukanNipEt.requestFocus();
            return;
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("base_url")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CekNipBknInterface request = retrofit.create(CekNipBknInterface.class);
        Call<JsonObject> call = request.cekBkn(nip);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                pDialog.dismiss();
                String s = String.valueOf("status");
//                JsonArray jsonArray = response.getAsJaonArray("data");
                Toast.makeText(DaftarActivity.this, response.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
            }
        });

        /*Call<String> call = RetrofitClient
                .getmInstance()
                .getNip()
                .cekBkn(nip);*/

        /*call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String s = response.body();
                Log.d("Response", "onResponse: " + s);
                Toast.makeText(DaftarActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(DaftarActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });*/
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



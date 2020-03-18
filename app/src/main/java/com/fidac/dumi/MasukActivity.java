package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fidac.dumi.api.BaseApiService;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;
import com.fidac.dumi.model.VolleySingleton;
import com.fidac.dumi.util.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class MasukActivity extends AppCompatActivity {

    private Button masukButton;
    private EditText nipEt;
    private EditText passEt;

    Context mContext;
    BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masuk);

        nipEt = findViewById(R.id.masuk_nip_et);
        passEt = findViewById(R.id.masuk_password_et);

        masukButton = findViewById(R.id.masuk_user_button);
        masukButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(MasukActivity.this, HalamanDepanActivity.class));
    }

    /*private void requestLogin(){
        final String nip = nipEt.getText().toString();
        final String pass = passEt.getText().toString();
        mApiService.loginRequest(nip, pass)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if(response.isSuccessful()){

                            try {
                                JsonObject jsonResult = new JsonObject(response.body().string());
                                if (jsonResult.getString()) {
                                    // Jika login berhasil maka data nama yang ada di response API
                                    // akan diparsing ke activity selanjutnya.
                                    Toast.makeText(mContext, "BERHASIL LOGIN", Toast.LENGTH_SHORT).show();
//                                    String nama = jsonRESULTS.getJSONObject("user").getString("nama");
//                                    sharedPrefManager.saveSPString(SharedPrefManager.SP_NAMA, nama);
                                    // Shared Pref ini berfungsi untuk menjadi trigger session login
                                    startActivity(new Intent(mContext, MainActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                }
                                else {
                                    // Jika login gagal
                                    String error_message = jsonRESULTS.getString("error_msg");
                                    Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
//                            loading.dismiss();
                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        loading.dismiss();
                    }
                });
    }*/

    /*private void userLogin() {
        final String url = "http://app.ternak-burung.top/api/user/get";
        RequestQueue queue = Volley.newRequestQueue(this);

        //first getting the values
        final String nip = nipEt.getText().toString();
        final String password = passEt.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(nip)) {
            nipEt.setError("Masukkan NIP Anda");
            nipEt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passEt.setError("Masukkan Password Anda");
            passEt.requestFocus();
            return;
        }

        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressBar.setVisibility(View.GONE);

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getInt("id"),
                                        userJson.getString("username"),
                                        userJson.getString("email"),
                                        userJson.getString("gender")
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                //starting the profile activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void userLogin() {
        //first getting the values
        final String nip = nipEt.getText().toString();
        final String password = passEt.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(nip)) {
            nipEt.setError("Please enter your username");
            nipEt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passEt.setError("Please enter your password");
            passEt.requestFocus();
            return;
        }

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

                            //if no error in response 12345678901234567890 123123123
                            if (obj.getBoolean("status")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONArray userJson = obj.getJSONArray("data");

                                //creating a new user object
                                /*User user = new User(
                                        userJson.getInt("id"),
                                        userJson.getString("username"),
                                        userJson.getString("email"),
                                        userJson.getString("nama")
                                );*/

                                //storing the user in shared preferences
                                /*SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);*/

                                //starting the profile activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
    }
}

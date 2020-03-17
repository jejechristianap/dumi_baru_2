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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DaftarActivity extends AppCompatActivity {
    private EditText masukanNipEt;
    private EditText masukanEmailEt;
    private EditText masukanPasswordEt;
    private EditText cekPasswordEt;
    private EditText masukanNoTelp;
    private Boolean isValid;

    private Session session;

    private CheckBox passwordCheckBox;

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
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);





//        masukanNoTelp = findViewById(R.id.daftar_no_telp_et);
        masukanNipEt = findViewById(R.id.daftar_nip_et);
        masukanEmailEt = findViewById(R.id.daftar_email_et);
        masukanPasswordEt = findViewById(R.id.daftar_password_et);
        cekPasswordEt = findViewById(R.id.daftar_ulangi_password_et);

        passwordCheckBox = findViewById(R.id.checkbox_password);
        lanjutButton = findViewById(R.id.daftar_lanjut_button);
        syaratSwitch = findViewById(R.id.switch_syarat);

        masukanNipEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            String nip = masukanNipEt.getText().toString();
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                }
            }
        });

        passwordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
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
//                        String number = masukanNoTelp.getText().toString();
                        boolean mNip = false;
                        boolean mEmail = false;
                        boolean mCekPass = false;

                        if(TextUtils.isEmpty(nip)){
                            masukanNipEt.setError("Kolom ini tidak boleh kosong..");
                        } else {
                            masukanNipEt.setError(null);
                            mNip = true;
                        }

                        if (TextUtils.isEmpty(email)){
                            masukanEmailEt.setError("Kolom ini tidak boleh kosong..");
                        } else if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches()){
                            masukanEmailEt.setError("Email tidak valid");
                            masukanEmailEt.requestFocus();
                        } else {
                            masukanEmailEt.setError(null);
                            mEmail = true;
                        }

                        if (TextUtils.isEmpty(password)){
                            masukanPasswordEt.setError("Kolom ini tidak boleh kosong..");
                        } else if (password.length() < 6){
                            masukanPasswordEt.setError("Minimal Password 6 Karakter");
                        } else {
                            masukanPasswordEt.setError(null);
                        }

                        if(TextUtils.isEmpty(cekPass)){
                            cekPasswordEt.setError("Kolom ini tidak boleh kosong..");
                        } else if(!cekPass.equals(password)) {
                            cekPasswordEt.setError("Password Tidak Sama");
                            
                        } else {
                            cekPasswordEt.setError(null);
                            mCekPass = true;
                        }

                        if(!mNip || !mEmail || !mCekPass){

                        } else {
                            try{

                                // CALL GetText method to make post method call
                                Toast.makeText(DaftarActivity.this, "Run", Toast.LENGTH_SHORT).show();
                                postUsingVolley();
                            }
                            catch(Exception ex)
                            {
                                Toast.makeText(DaftarActivity.this, "error", Toast.LENGTH_SHORT).show();
                            }

                            startActivity(new Intent(DaftarActivity.this, OtpVerify.class));
                        }

                    }
                });
                if (!isChecked){
                    lanjutButton.setEnabled(false);
                    lanjutButton.setBackgroundResource(R.drawable.button_lanjut_design);
                }
            }
        });
    }

    /*public void makePostUsingVolley()
    {
        session = new SessionManager(DaftarActivity.this);
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();

        final String  token = user.get(SessionManager.KEY_NAME);

        //Toast.makeText(getActivity().getApplicationContext(),name, Toast.LENGTH_SHORT).show();

        final Map<String, String> params = new HashMap<String, String>();
        //params.put("Employees",name);
        String tag_json_obj = "json_obj_req";
        String url = "enter your url";

        final ProgressDialog pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest req = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    // final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    //"http://emservices.azurewebsites.net/Employee.asmx/CheckUserGet", new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(String response) {

                        JSONObject json;
                        // Toast.makeText(getActivity().getApplicationContext(),"dfgghfhfgjhgjghjuhj", Toast.LENGTH_SHORT).show();



                        //Toast.makeText(getActivity().getApplicationContext(),obb.length(), Toast.LENGTH_SHORT).show();



                        // JSONObject data=obj.getJSONObject("Employee_Name");
                        ObjectOutput out = null;
                        try {

                            json = new JSONObject(response);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                        pDialog.hide();
                        // Toast.makeText(getApplicationContext(),"hi", Toast.LENGTH_SHORT).show();
                        Log.d("", response);


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.hide();
                // hide the progress dialog

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username",name);
                params.put("password",password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_obj);
    }*/



    private void postUsingVolley() {
        final String url = "http://app.ternak-burung.top/api/user/insert";
        RequestQueue queue = Volley.newRequestQueue(this);

        final String nip = masukanNipEt.getText().toString();
        final String email = masukanEmailEt.getText().toString();
        final String password = masukanPasswordEt.getText().toString();

//        Toast.makeText(this, "nip: " + nip + ", email: " + email + ", pass: " + password, Toast.LENGTH_SHORT).show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("nip", nip);
                params.put("nama", "fidacholdings");
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        queue.add(postRequest);
    }


}

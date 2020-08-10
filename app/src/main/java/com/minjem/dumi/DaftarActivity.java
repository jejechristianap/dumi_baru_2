package com.minjem.dumi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.minjem.dumi.akun.KebijakanPrivasiActivity;
import com.minjem.dumi.api.CekNipBknInterface;
import com.minjem.dumi.api.CekUserExist;
import com.minjem.dumi.retrofit.RetrofitClient;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaftarActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101;
    private EditText masukanNipEt;
    private Button bCheckNip;
    private CheckBox showPassCheckBox;
    /*Hide Soft Keyboard*/
    private InputMethodManager imm;
    private LinearLayout dataNipLl;
    private LinearLayout cekNipLl;
    private LinearLayout emailPassLl;
    private Button lanjutButton, testButton;
    private EditText emailEt, passEt, ulangiPassEt;
    private String[] instansi = {"Pilih Mitra", "ASN AKTIF", "BUMN", "ASN PENSIUN"};
    private TextView syaratTv;
    private Switch setujuSwitch;
    private Spinner instansiSpinner;
    private ArrayAdapter<CharSequence> instansiAdapter;

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private ProgressBar progressBar;
    private Button getTokenButton;
    private TextView tokenTv;
    private String accessToken = "";
    private TextView tanggalLahir;
    private EditText namaPnsEt;
    private int countDown = 0;
    private Calendar myCalendar;
    private DatePicker dp;

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

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        pref = getApplicationContext().getSharedPreferences("DATA", 0); // 0 - for private mode
        editor = pref.edit();
        editor.clear();
        LinearLayout ll = findViewById(R.id.email_password);
        ll.setVisibility(View.GONE);


        syaratTv = findViewById(R.id.setuju_tv);
        syaratTv.setText(Html.fromHtml(getString(R.string.saya_setuju)));
        syaratTv.setOnClickListener(v -> startActivity(new Intent(this, KebijakanPrivasiActivity.class)));
        setujuSwitch = findViewById(R.id.setuju_switch);
        setujuSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                lanjutButton.setEnabled(true);
                lanjutButton.setBackgroundResource(R.drawable.button_design_login_register);
            } else {
                lanjutButton.setEnabled(false);
                lanjutButton.setBackgroundResource(R.drawable.button_lanjut_design) ;
            }
        });

        /*instansiSpinner = findViewById(R.id.daftar_sebagai_spinner);
        instansiAdapter = new ArrayAdapter<>(DaftarActivity.this, R.layout.spinner_text, instansi);
        instansiAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        instansiSpinner.setAdapter(instansiAdapter);
*/
        pDialog = new ProgressDialog(DaftarActivity.this);

        /*Linear Layout*/
        cekNipLl = findViewById(R.id.cek_nip_layout);
        emailPassLl = findViewById(R.id.email_password);
        tanggalLahir = findViewById(R.id.daftar_tgl_lahir);
        namaPnsEt = findViewById(R.id.daftar_nama_pns);

        /*Hide Soft Keyboard*/
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        masukanNipEt = findViewById(R.id.daftar_nip_et);
        bCheckNip = findViewById(R.id.cek_nip);
        showPassCheckBox = findViewById(R.id.show_password);

        lanjutButton = findViewById(R.id.lanjut_button);
        emailEt = findViewById(R.id.email_daftar_et);
        passEt = findViewById(R.id.masuk_password_et);
        ulangiPassEt = findViewById(R.id.ulangi_password_et);

        bCheckNip.setOnClickListener(v -> {
            cekUser();
        });

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };




        tanggalLahir.setInputType(InputType.TYPE_NULL);
        tanggalLahir.setOnClickListener(v -> {
            /*new DatePickerDialog(DaftarActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/
            datePicker();
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

        lanjutButton.setOnClickListener(v -> {
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
            editor.apply();

//            startActivity(new Intent(DaftarActivity.this, OtpVerify.class));
            doPhoneLogin();

        });
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        Locale localID = new Locale("in", "ID");
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, localID);
        tanggalLahir.setText(sdf.format(myCalendar.getTime()));
    }

    @SuppressLint("SetTextI18n")
    private void datePicker(){
        View dialogView = getLayoutInflater().inflate(R.layout.datepicker_dialog, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        Button bTutup = dialogView.findViewById(R.id.bPilih);
        DatePicker dp = dialogView.findViewById(R.id.dpTglLahir);
        dialog.setCancelable(false);
        dialog.setContentView(dialogView);
        dialog.show();

        dp.init(
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH),
                (view, year, monthOfYear, dayOfMonth) -> {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String myFormat = "dd-MM-yyyy"; //In which you need put here
                    Locale localID = new Locale("in", "ID");
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, localID);
//                    tanggalLahir.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                    tanggalLahir.setText(sdf.format(myCalendar.getTime()));

                });


        bTutup.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            Intent i = new Intent(getApplicationContext(), DataPribadiActivity.class);
            i.putExtra("namaKtp", namaPnsEt.getText().toString());
            startActivity(i);
//            FirebaseAuth.getInstance().createUserWithEmailAndPassword()
        }
    }

    /*private void getToken(){
        CekNipBknInterface token = RetrofitClient.getClient().create(CekNipBknInterface.class);
        Call<ResponseBody> call = token.getToken();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String data = jsonObject.getString("data");
                        JSONObject request = new JSONObject(data);
                        Log.d("Data", "onResponse: " + data);
                        if (jsonObject.getBoolean("status")){
//                            Toast.makeText(DaftarActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                            JSONObject data = jsonObject.getJSONObject("data");
                            accessToken = request.getString("access_token");
                            countDown = request.getInt("expires_in");

                            Log.d("Token", "onResponse: " + accessToken);

                            cekUser();
                        } else {
                            Toast.makeText(DaftarActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException | IOException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }*/

    /*FireBase OTP UI*/
    private void doPhoneLogin() {
            Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                    .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.PhoneBuilder().build()))
                    .setTheme(R.style.OtpTheme)
                    .setLogo(R.mipmap.ic_launcher_dumi)
                    .build();
            startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            } else {
                /**
                 *   Sign in failed. If response is null the user canceled the
                 *   sign-in flow using the back button. Otherwise check
                 *   response.getError().getErrorCode() and handle the error.
                 */
                Toast.makeText(getBaseContext(), "OTP Gagal", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*Cek nip sudah pernah mendaftar atau belum*/
    public void cekUser(){
        String nip = masukanNipEt.getText().toString();
        if(TextUtils.isEmpty(nip)){
            masukanNipEt.setError("Kolom tidak boleh kosong...");
            masukanNipEt.requestFocus();
            return;
        }
        if(nip.length() < 15){
            masukanNipEt.setError("Nomor NIP Salah!");
            masukanNipEt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(tanggalLahir.getText().toString())){
            Toast.makeText(this, "Tanggal lahir masih kosong!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(namaPnsEt.getText().toString())){
            namaPnsEt.setError("Harap isi nama anda!");
            namaPnsEt.requestFocus();
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
                        Toast.makeText(DaftarActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        cekNip();
                    }
                }catch (Exception e) {
//                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    emailPassLl.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    private void dialogGagal(String ins){
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        RelativeLayout rlGagalInstansi = dialogView.findViewById(R.id.rlDialogInstansi);
        TextView tvGagalInstansi = dialogView.findViewById(R.id.tvInstansi);
        Button bTutup = dialogView.findViewById(R.id.bTutup);
        dialog.setContentView(dialogView);
        dialog.show();
        rlGagalInstansi.setVisibility(View.VISIBLE);
        tvGagalInstansi.setText(ins);
        bTutup.setOnClickListener(v -> {
            rlGagalInstansi.setVisibility(View.GONE);
            dialog.dismiss();
        });
    }

    /*Cek keterangan Nip*/
    private void cekNip() {
        String nip = masukanNipEt.getText().toString();
        String tglLahir = tanggalLahir.getText().toString();
        String namaPns = namaPnsEt.getText().toString();
        if(TextUtils.isEmpty(nip)){
            masukanNipEt.setError("Kolom tidak boleh kosong...");
            masukanNipEt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(namaPns)){
            namaPnsEt.setError("Kolom tidak boleh kosong...");
            namaPnsEt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(tglLahir)){
            tanggalLahir.setError("Kolom tidak boleh kosong...");
            tanggalLahir.requestFocus();
            return;
        }



        CekNipBknInterface cek = RetrofitClient.getClient().create(CekNipBknInterface.class);
        Call<ResponseBody> call = cek.getBkn(nip, tglLahir, namaPns);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Res", "onResponse: " +response.body());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String data = jsonObject.getString("data");
                    JSONObject request = new JSONObject(jsonObject.getString("data"));
                    if(request.getInt("code") == 1){
                        pDialog.dismiss();
                        JSONObject jsonData = new JSONObject(request.getString("data"));
                        String inskerNama = jsonData.getString("instansiKerjaNama");
                        String namaPns = jsonData.getString("nama");
                        String tglLahir = jsonData.getString("tglLahir");
                        String tempatLahir = jsonData.getString("tempatLahir");
                        String jenisKelamin = jsonData.getString("jenisKelamin");
                        editor.putString("instansiKerjaNama", inskerNama);
                        editor.putString("namaPns", namaPns);
                        editor.putString("tglLahir", tglLahir);
                        editor.putString("tempatLahir", tempatLahir);
                        editor.putString("jenisKelamin", jenisKelamin);
                        editor.putString("dataBkn", data);
                        Log.d("Data", "unorNama: " + inskerNama  + "\nNama : " + namaPns + "\ntglLahir: " + tglLahir );

                        Toast.makeText(DaftarActivity.this, "NIP Instansi anda ditemukan..", Toast.LENGTH_SHORT).show();
                        emailPassLl.setVisibility(View.VISIBLE);
                        emailEt.requestFocus();
                    }else{
//                        Toast.makeText(DaftarActivity.this, request.getString("data"), Toast.LENGTH_SHORT).show();
                        dialogGagal(request.getString("data"));
                        emailPassLl.setVisibility(View.GONE);
                        masukanNipEt.setCursorVisible(true);
                        pDialog.dismiss();
                    }
                } catch (Exception e) {
                    emailPassLl.setVisibility(View.GONE);
                    pDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DaftarActivity.this, "Session Timeout", Toast.LENGTH_SHORT).show();
                Log.d("Error", "onFailure: " + t.getMessage());
                dialogGagal(t.getMessage());
                pDialog.dismiss();
                call.cancel();
            }
        });
    }
}


package com.fidac.dumi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class DaftarActivity extends AppCompatActivity {
    private EditText masukanNipEt;
    private EditText masukanEmailEt;
    private EditText masukanPasswordEt;
    private EditText cekPasswordEt;
    private EditText masukanNoTelp;
    private Boolean isValid;

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

                        if(!mNip && !mEmail && !mCekPass){
                            Toast.makeText(DaftarActivity.this, "Test", Toast.LENGTH_SHORT).show();
                        } else {
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


}

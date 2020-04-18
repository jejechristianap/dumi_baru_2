package com.fidac.dumi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.firebase.ui.auth.AuthUI;


import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class OtpVerify extends AppCompatActivity {

    private EditText kodeEt;
    private EditText nomorTelpEt;
    private Button kirimOtpButton;
    private Button verifikasiButton;
    private Button kirimUlangOtpButton;
    private String nomorTelp;
    private String kodeOtp;

    private LinearLayout noTelpLl;
    private LinearLayout verifikasiKodeLl;

    private FirebaseAuth mAuth;
    private String mVerifikasiId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private static final int RC_SIGN_IN = 101;

    private LinearLayout progressBar;

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        pref = getApplicationContext().getSharedPreferences("Daftar", 0); // 0 - for private mode
        editor = pref.edit();

//        mAuth = FirebaseAuth.getInstance();
        doPhoneLogin();

        /*kodeEt = findViewById(R.id.kodeOtp);
        nomorTelpEt = findViewById(R.id.daftar_no_telp_et);
        noTelpLl = findViewById(R.id.nomor_telp);
        verifikasiKodeLl = findViewById(R.id.verifikasi_kode);
        progressBar = findViewById(R.id.progress_bar);

        kirimOtpButton = findViewById(R.id.kirim_kode_otp_button);
        kirimOtpButton.setOnClickListener(v -> {
            nomorTelp = nomorTelpEt.getText().toString();
            if(TextUtils.isEmpty(nomorTelp)){
                nomorTelpEt.setError("Kolom ini tidak boleh kosong..");
            } else if(nomorTelp.length() < 9 || nomorTelp.length() > 12){
                nomorTelpEt.setError("Nomor tidak valid");
                nomorTelpEt.requestFocus();
            } else {
                nomorTelpEt.setError(null);
                sendVerificationCode(nomorTelp);
//                    progressBar.setVisibility(View.VISIBLE);
                noTelpLl.setVisibility(View.GONE);
                verifikasiKodeLl.setVisibility(View.VISIBLE);
                return;
            }
        });

        verifikasiButton = findViewById(R.id.verifikasi_button);
        verifikasiButton.setOnClickListener(v -> {
            kodeOtp = kodeEt.getText().toString();
            if(TextUtils.isEmpty(kodeOtp)){
                kodeEt.setError("Kolom ini tidak boleh kosong..");
            } else {
                kodeEt.setError(null);
                verifyVerificationCode(kodeOtp);
                verifikasiKodeLl.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                String noTelp = nomorTelpEt.getText().toString();
                editor.putString("no_telp", noTelp);
                editor.apply();
            }
        });

        kirimUlangOtpButton = findViewById(R.id.kirim_ulang_button);
        kirimUlangOtpButton.setOnClickListener(v -> resendVerificationCode(nomorTelp, mResendToken));
*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), LengkapiData.class));
        }
    }

    /*FireBase UI*/
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


    /*Firebase OTP custom*/
   /* private void sendVerificationCode(String no) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+62" + no,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+62" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            Log.d("", "onVerificationCompleted:" + phoneAuthCredential);

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                kodeEt.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpVerify.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.w("", "onVerificationFailed", e);
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                nomorTelpEt.setError("Invalid phone number.");
            } else if (e instanceof FirebaseTooManyRequestsException) {

            }
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            Log.d("", "onCodeSent:" + s);
            mVerifikasiId = s;
            mResendToken = forceResendingToken;

            //storing the verification id that is sent to the user

        }
    };
    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerifikasiId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpVerify.this, task -> {
                    if (task.isSuccessful()) {
                        //verification successful we will start the profile activity

                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(OtpVerify.this, LengkapiData.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {

                        //verification unsuccessful.. display an error message
                        progressBar.setVisibility(View.GONE);
                        String message = "Terjadi kesalahan mohon mencoba lagi";
                        Toast.makeText(OtpVerify.this, message, Toast.LENGTH_SHORT).show();
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "Kode tidak valid";
                            Toast.makeText(OtpVerify.this, message, Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }*/
}

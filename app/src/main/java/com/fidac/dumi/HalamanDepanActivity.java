package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fidac.dumi.api.PropinsiInterface;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.retrofit.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HalamanDepanActivity extends AppCompatActivity {
    private Button daftarButton;
    private Button masukButton;
    private TextView masukTv;

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_depan);

        SharedPrefManager.getInstance(getApplicationContext()).logout();
        pref = getApplicationContext().getSharedPreferences("Daftar", 0); // 0 - for private mode
        editor = pref.edit();
        editor.clear();

        daftarButton = findViewById(R.id.asn_akftif_button_daftar);
        daftarButton.setOnClickListener(v ->
                startActivity(new Intent(HalamanDepanActivity.this, DaftarActivity.class)));


        masukButton = findViewById(R.id.masuk_button);
        masukButton.setOnClickListener(v -> {
            startActivity(new Intent(HalamanDepanActivity.this, MasukActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}

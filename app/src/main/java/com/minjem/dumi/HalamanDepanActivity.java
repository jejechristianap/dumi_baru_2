package com.minjem.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.minjem.dumi.model.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;

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

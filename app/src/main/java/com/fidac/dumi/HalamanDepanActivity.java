package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HalamanDepanActivity extends AppCompatActivity {
    private Button daftarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_depan);

        daftarButton = findViewById(R.id.daftar_button);
        daftarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HalamanDepanActivity.this, DaftarActivity.class));
            }
        });
    }
}

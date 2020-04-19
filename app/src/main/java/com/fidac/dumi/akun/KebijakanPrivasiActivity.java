package com.fidac.dumi.akun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.fidac.dumi.R;

public class KebijakanPrivasiActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView syaratTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kebijakan_privasi);
        backButton = findViewById(R.id.back_syarat);
        backButton.setOnClickListener(v -> finish());

    }
}

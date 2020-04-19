package com.fidac.dumi.akun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.fidac.dumi.R;

public class DisclaimerActivity extends AppCompatActivity {
    private TextView disclaimerTv;
    private ImageView backIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        backIv = findViewById(R.id.back_disclaimer);
        backIv.setOnClickListener(v -> finish());

    }
}

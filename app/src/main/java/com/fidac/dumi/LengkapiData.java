package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LengkapiData extends AppCompatActivity {

    private Button lanjutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lengkapi_data);

        lanjutButton = findViewById(R.id.lanjut_button_lengkapi_data);

        lanjutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LengkapiData.this, TakePicture.class));
            }
        });
    }
}

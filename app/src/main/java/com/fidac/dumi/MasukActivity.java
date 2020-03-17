package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class MasukActivity extends AppCompatActivity {

    private String nipt = "123456789";
    private String passt = "123456";

    private Button masukButton;
    private EditText nipEt;
    private EditText passEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masuk);

        nipEt = findViewById(R.id.masuk_nip_et);
        passEt = findViewById(R.id.masuk_password_et);

        masukButton = findViewById(R.id.masuk_user_button);
        masukButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nip = nipEt.getText().toString();
                String pass = passEt.getText().toString();
                if (nip.equals(nipt) && pass.equals(passt)){
                    startActivity(new Intent(MasukActivity.this, MainActivity.class));
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(MasukActivity.this, HalamanDepanActivity.class));
    }
}

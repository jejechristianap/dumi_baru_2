package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fidac.dumi.api.PropinsiInterface;
import com.fidac.dumi.retrofit.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HalamanDepanActivity extends AppCompatActivity {
    private Button asnButton;
    private Button bumnButton;
    private Button pensiunButton;
    private TextView masukTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_depan);

        asnButton = findViewById(R.id.asn_akftif_button_daftar);
        asnButton.setOnClickListener(v ->
                startActivity(new Intent(HalamanDepanActivity.this, DaftarActivity.class)));

        bumnButton = findViewById(R.id.bumn_button_daftar);
        bumnButton.setOnClickListener(v ->
                startActivity(new Intent(HalamanDepanActivity.this, DaftarActivity.class)));

        pensiunButton = findViewById(R.id.pensiun_button_daftar);
        pensiunButton.setOnClickListener(v -> {
            startActivity(new Intent(HalamanDepanActivity.this, DaftarActivity.class));

        });

        masukTv = findViewById(R.id.masuk_tv);
        masukTv.setOnClickListener(v -> {
            startActivity(new Intent(HalamanDepanActivity.this, MasukActivity.class));

        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}

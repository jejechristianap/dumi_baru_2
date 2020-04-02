package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fidac.dumi.api.PropinsiInterface;
import com.fidac.dumi.retrofit.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HalamanDepanActivity extends AppCompatActivity {
    private Button daftarButton;
    private Button masukButton;
    private Button getPropinsiBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_depan);

        daftarButton = findViewById(R.id.daftar_button);
        daftarButton.setOnClickListener(v -> startActivity(new Intent(HalamanDepanActivity.this, DaftarActivity.class)));
        masukButton = findViewById(R.id.masuk_button);
        masukButton.setOnClickListener(v -> startActivity(new Intent(HalamanDepanActivity.this, MasukActivity.class)));
       /* getPropinsiBt = findViewById(R.id.get_propinsi);
        getPropinsiBt.setOnClickListener(v -> {
            cekPropinsi();
        })*/;

    }

    public void cekPropinsi() {
        PropinsiInterface prop = RetrofitClient.getPropinsi().create(PropinsiInterface.class);
        Call<ResponseBody> call = prop.getPropinsi();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(HalamanDepanActivity.this, "Response: " + response.body().toString(), Toast.LENGTH_SHORT).show();
                Log.d("Prop", "onResponse: " + response.body().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}

package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fidac.dumi.api.IsiPulsaInterface;
import com.fidac.dumi.model.DaftarHargaPulsa;
import com.fidac.dumi.model.RecyclerViewAdapter;
import com.fidac.dumi.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IsiPulsaActivity extends AppCompatActivity {
    private Button testButton;

    private LinearLayoutManager layoutManager;
    private List<DaftarHargaPulsa> daftarHargaPulsaList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_pulsa);

        testButton = findViewById(R.id.test_button);
        testButton.setOnClickListener(v -> {
            cekDaftarHarga();
        });

    }

    private void cekDaftarHarga() {
        IsiPulsaInterface cek = RetrofitClient.getPulsa().create(IsiPulsaInterface.class);

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Data...");
        pDialog.show();

        Call<List<DaftarHargaPulsa>> call = cek.getDaftarHargaPulsa("api_mmbc_fidac18", "Fi918_ahBmpl");
        call.enqueue(new Callback<List<DaftarHargaPulsa>>() {
            @Override
            public void onResponse(Call<List<DaftarHargaPulsa>> call, Response<List<DaftarHargaPulsa>> response) {
                Log.d("Response", "onResponse: " + response);
                //Log.i("onResponse", response.message());
                Log.i("autolog", "onResponse");

                daftarHargaPulsaList = response.body();
                RecyclerView recyclerView = findViewById(R.id.daftar_harga);
                layoutManager = new LinearLayoutManager(IsiPulsaActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                RecyclerViewAdapter recyclerViewAdapter =new RecyclerViewAdapter(getApplicationContext(), daftarHargaPulsaList);
                recyclerView.setAdapter(recyclerViewAdapter);
                pDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<DaftarHargaPulsa>> call, Throwable t) {
                Log.d("Fail", "onFailure: " + t.getMessage());
                pDialog.dismiss();
            }
        });
    }
}

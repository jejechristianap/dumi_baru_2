package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

        Call<List<DaftarHargaPulsa>> call = cek.getDaftarHargaPulsa("api_mmbc_fidac18", "Fi918_ahBmpl");
        call.enqueue(new Callback<List<DaftarHargaPulsa>>() {
            @Override
            public void onResponse(Call<List<DaftarHargaPulsa>> call, Response<List<DaftarHargaPulsa>> response) {
                Log.d("Response", "onResponse: " + response);
                //Log.i("onResponse", response.message());
                Log.i("autolog", "onResponse");

                daftarHargaPulsaList = response.body();
                Log.i("autolog", "List<User> userList = response.body();");

                RecyclerView recyclerView = findViewById(R.id.daftar_harga);
                Log.i("autolog", "RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler);");

                layoutManager = new LinearLayoutManager(IsiPulsaActivity.this);
                Log.i("autolog", "layoutManager = new LinearLayoutManager(MainActivity.this);");
                recyclerView.setLayoutManager(layoutManager);
                Log.i("autolog", "recyclerView.setLayoutManager(layoutManager);");

                RecyclerViewAdapter recyclerViewAdapter =new RecyclerViewAdapter(getApplicationContext(), daftarHargaPulsaList);
                Log.i("autolog", "RecyclerViewAdapter recyclerViewAdapter =new RecyclerViewAdapter(getApplicationContext(), userList);");
                recyclerView.setAdapter(recyclerViewAdapter);
                Log.i("autolog", "recyclerView.setAdapter(recyclerViewAdapter);");

            }

            @Override
            public void onFailure(Call<List<DaftarHargaPulsa>> call, Throwable t) {
                Log.d("Fail", "onFailure: " + t.getMessage());
            }
        });
    }
}

package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.fidac.dumi.api.IsiPulsaInterface;
import com.fidac.dumi.model.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IsiPulsaActivity extends AppCompatActivity {
    private Button testButton;

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
        Call<ResponseBody> call = cek.cekHargaPulsa("api_mmbc_fidac18", "Fi918_ahBmpl");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}

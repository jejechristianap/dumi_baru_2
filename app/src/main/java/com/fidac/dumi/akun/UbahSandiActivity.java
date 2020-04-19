package com.fidac.dumi.akun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fidac.dumi.R;
import com.fidac.dumi.api.UbahSandiInterface;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;
import com.fidac.dumi.retrofit.RetrofitClient;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UbahSandiActivity extends AppCompatActivity {

    private EditText sandiBaruEt, ulangiSandiEt;
    private CheckBox lihatSandi;
    private Button ubahSandi;
    private User pref;
    private ImageView backIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_sandi);

        sandiBaruEt = findViewById(R.id.masuk_password_ubah);
        ulangiSandiEt = findViewById(R.id.ulangi_password_ubah);
        lihatSandi = findViewById(R.id.show_password_ubah);
        ubahSandi = findViewById(R.id.ubah_sandi_button);
        backIv = findViewById(R.id.back_sandi);
        backIv.setOnClickListener(v -> finish());

        lihatSandi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                sandiBaruEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ulangiSandiEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                sandiBaruEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ulangiSandiEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        ubahSandi.setOnClickListener(v -> {
//            Toast.makeText(this, "touch", Toast.LENGTH_SHORT).show();
            sandiBaru();
        });
    }

    private void sandiBaru(){
        String sandi = sandiBaruEt.getText().toString();
        String ulangi = ulangiSandiEt.getText().toString();
        if (TextUtils.isEmpty(sandi)){
            sandiBaruEt.setError("Minimal sandi 6 karakter!");
            sandiBaruEt.requestFocus();
            return;
        }
        if (!ulangi.equals(sandi)){
            ulangiSandiEt.setError("Sandi tidak sama!");
            ulangiSandiEt.requestFocus();
            return;
        }
        pref = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String nip = pref.getNip();

        UbahSandiInterface ubah = RetrofitClient.getClient().create(UbahSandiInterface.class);
        Call<ResponseBody> call = ubah.ubahSandi(nip, ulangi);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    boolean status = obj.getBoolean("status");
                    if (status){
                        Toast.makeText(UbahSandiActivity.this, "Sandi berhasil diubah", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UbahSandiActivity.this, "Sandi gagal diubah", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
}

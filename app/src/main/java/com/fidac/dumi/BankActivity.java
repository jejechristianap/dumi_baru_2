package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fidac.dumi.api.PinjamanKilatInterface;
import com.fidac.dumi.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankActivity extends AppCompatActivity {

    private EditText namaBankEt, noRekEt, pemilikEt;
    private Button ajukanButton;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        pref = getApplicationContext().getSharedPreferences("ajukanPinjaman", 0);

        namaBankEt = findViewById(R.id.nama_bank_et);
        noRekEt = findViewById(R.id.no_rek_et);
        pemilikEt = findViewById(R.id.nama_pemilik_rek_et);
        ajukanButton = findViewById(R.id.ajukan_button_bank);

        ajukanButton.setOnClickListener(v -> {
            String namaBank = namaBankEt.getText().toString();
            String noRek = noRekEt.getText().toString();
            String pemilik = pemilikEt.getText().toString();

            if (TextUtils.isEmpty(namaBank)){
                namaBankEt.setError("Kolom ini tidak boleh kosong..");
                namaBankEt.requestFocus();
                return;
            } else {
                namaBankEt.setError(null);
            }

            if (TextUtils.isEmpty(noRek)){
                noRekEt.setError("Kolom ini tidak boleh kosong..");
                noRekEt.requestFocus();
                return;
            } else {
                noRekEt.setError(null);
            }

            if (TextUtils.isEmpty(pemilik)){
                pemilikEt.setError("Kolom ini tidak boleh kosong..");
                pemilikEt.requestFocus();
                return;
            } else {
                pemilikEt.setError(null);
            }

            String nip = pref.getString("nip", null);
            float pinjaman = pref.getFloat("pinjaman", 0.f);
            int plafond = pref.getInt("lamaPinjaman", 0);
            float bunga = pref.getFloat("bunga", 0.f);
            float admin = pref.getFloat("admin", 0.f);
            float angsuran = pref.getFloat("angsuran", 0.f);
            float diterima = pref.getFloat("diterima", 0.f);
            String tujuan = pref.getString("tujuan", null);
            String tglMulai = pref.getString("tglMulai", null);
            String tglAkhir = pref.getString("tglAkhir", null);
            float asuransi = pref.getFloat("asuransi", 0.f);

            Log.d("Pinjaman",
                    "nip: " + nip +
                            "\nPinjaman: " + pinjaman +
                            "\nPlafond: " + plafond +
                            "\nBunga: " + bunga +
                            "\nAdmin: " + admin +
                            "\nAngsuran: " + angsuran +
                            "\nTujuan: " + tujuan +
                            "\nTglPinjam: " + tglMulai +
                            "\nAkhirTgl: " + tglAkhir +
                            "\nSisa: " + diterima +
                            "\nasurans: " + asuransi +
                            "\nBank: " + namaBank +
                            "\nnoRek: " + noRek + "\nNama: " + pemilik);

            /*Progress Dialog*/
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Memuat Data...");
            pDialog.show();

            PinjamanKilatInterface pinjam = RetrofitClient.getClient().create(PinjamanKilatInterface.class);
            Call<ResponseBody> call = pinjam.ajukanPinjaman(nip, pinjaman, plafond, 0,
                    9.9f, bunga, admin, angsuran, 6500, tujuan,
                    tglMulai, tglAkhir, "", diterima, asuransi,namaBank, noRek, pemilik);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        boolean status = obj.getBoolean("status");
                        if(status){
                            pDialog.dismiss();
                            Toast.makeText(BankActivity.this, "Terima kasih, pengajuan anda akan kami proses.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(BankActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(BankActivity.this, "Mohon maaf terjadi kesalahan, silahkan coba beberapa saat lagi.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                    } catch (IOException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pDialog.dismiss();
                }
            });


        });

    }
}

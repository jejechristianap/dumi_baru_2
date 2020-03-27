package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class LengkapiData extends AppCompatActivity {

    private EditText noKtpEt, namaLengkapEt, ketTitleEt,
            rtEt, rwEt, kelurahanEt, kecamatanEt, kotaEt,
            alamatEt, kodePosEt;

    private Button lanjutButton;

    private Spinner jenisKelaminSpinner, agamaSpinner, titleSpinner;
    private ArrayAdapter<CharSequence> jenisKelaminAdapter, agamaAdapter, titleAdapter;

    private static final String KOLOM = "Kolom ini tidak boleh kosong";

    private String[] title = {"Tanpa Gelar","D-1","D-2","D-3", "D-4", "S-1", "S-2", "S-3"};
    private String[] agama = {"Islam", "Kristen", "Khatolik", "Budha", "Hindu", "Konghucu"};
    private String[] jenisKelamin = {"L", "P"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lengkapi_data);

        /*Jenis Kelamin*/
        jenisKelaminSpinner = findViewById(R.id.jenis_kelamin_spinner);
        jenisKelaminAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, jenisKelamin);
        jenisKelaminAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        jenisKelaminSpinner.setAdapter(jenisKelaminAdapter);

        /*Agama*/
        agamaSpinner = findViewById(R.id.agama_spinner);
        agamaAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, agama);
        agamaAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        agamaSpinner.setAdapter(agamaAdapter);

        /*Title*/
        titleSpinner = findViewById(R.id.title_spinner);
        titleAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, title);
        titleAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        titleSpinner.setAdapter(titleAdapter);

        /*EditText Init*/
        noKtpEt = findViewById(R.id.no_ktp);
        namaLengkapEt = findViewById(R.id.nama_lengkap);
        ketTitleEt = findViewById(R.id.ket_title);
        rtEt = findViewById(R.id.rt);
        rwEt = findViewById(R.id.rw);
        kelurahanEt = findViewById(R.id.kelurahan);
        kecamatanEt = findViewById(R.id.kecamatan);
        kotaEt = findViewById(R.id.kota);
        alamatEt = findViewById(R.id.alamat);
        kodePosEt = findViewById(R.id.kode_pos);


        lanjutButton = findViewById(R.id.lanjut_button_lengkapi_data);

        lanjutButton.setOnClickListener(v -> {
            String noKtp, namaLengkap, ketTitle, rt, rw, kelurahan, kecamatan,
                    kota, alamat, kodePos;
            noKtp = noKtpEt.getText().toString();
            namaLengkap = namaLengkapEt.getText().toString();
            ketTitle = ketTitleEt.getText().toString();
            rt = rtEt.getText().toString();
            rw = rwEt.getText().toString();
            kelurahan = kelurahanEt.getText().toString();
            kecamatan = kecamatanEt.getText().toString();
            kota = kotaEt.getText().toString();
            alamat = alamatEt.getText().toString();
            kodePos = kodePosEt.getText().toString();

            /*User Handling*/
            if (TextUtils.isEmpty(noKtp)){
                noKtpEt.setError(KOLOM);
                noKtpEt.requestFocus();
                return;
            } else if (noKtp.length() != 16){
                noKtpEt.setError("No KTP salah");
                noKtpEt.requestFocus();
                return;
            } else {
                noKtpEt.setError(null);
            }

            if(TextUtils.isEmpty(namaLengkap)){
                namaLengkapEt.setError(KOLOM);
                namaLengkapEt.requestFocus();
                return;
            } else {
                namaLengkapEt.setError(null);
            }




            startActivity(new Intent(LengkapiData.this, TakePicture.class));
        });
    }
}

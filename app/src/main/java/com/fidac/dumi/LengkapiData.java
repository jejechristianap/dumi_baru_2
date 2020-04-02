package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class LengkapiData extends AppCompatActivity {

    private EditText noKtpEt, namaLengkapEt, ketTitleEt,
            rtEt, rwEt, kelurahanEt, kecamatanEt, kotaEt,
            alamatEt, kodePosEt;

    private Button lanjutButton;

    private Spinner jenisKelaminSpinner, agamaSpinner, titleSpinner,
            statusKawinSpinner, statusRumahSpinner, statusHubunganSpinner;
    private ArrayAdapter<CharSequence> jenisKelaminAdapter, agamaAdapter, titleAdapter,
            statusKawinAdapter, statusRumahAdapter, statusHubunganAdapter;

    private static final String KOLOM = "Kolom ini tidak boleh kosong";

    private String[] title = {"Tanpa Gelar","D-1","D-2","D-3", "D-4", "S-1", "S-2", "S-3"};
    private String[] agama = {"Islam", "Kristen", "Khatolik", "Budha", "Hindu", "Konghucu"};
    private String[] jenisKelamin = {"L", "P"};
    private String[] statusKawin = {"BELUM MENIKAH","MENIKAH", "CERAI", "DUDA", "JANDA"};
    private String[] statusHubungan = {"ORANGTUA", "KAKAK/ADE/KERABAT", "SUAMI/ISTRI"};
    private String[] statusRumah = {"KONTRAK", "RUMAH SENDIRI", "RUMAH ORANGTUA"};
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lengkapi_data);
        pref = getApplicationContext().getSharedPreferences("Daftar", 0); // 0 - for private mode
        editor = pref.edit();

        /*Jenis Kelamin*/
        jenisKelaminSpinner = findViewById(R.id.jenis_kelamin_spinner);
        jenisKelaminAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, jenisKelamin);
        jenisKelaminAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        jenisKelaminSpinner.setAdapter(jenisKelaminAdapter);

        /*Status Kawin*/
        statusKawinSpinner = findViewById(R.id.status_kawin);
        statusKawinAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, statusKawin);
        statusKawinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        statusKawinSpinner.setAdapter(statusKawinAdapter);

        /*Status Rumah*/
        statusRumahSpinner = findViewById(R.id.status_rumah);
        statusRumahAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, statusRumah);
        statusRumahAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        statusRumahSpinner.setAdapter(statusRumahAdapter);

        /*Status Hubungan*/
        statusHubunganSpinner = findViewById(R.id.status_hubungan_spinner);
        statusHubunganAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, statusHubungan);
        statusHubunganAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        statusHubunganSpinner.setAdapter(statusHubunganAdapter);

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
            createUser();
        });
    }

    public void createUser(){
        String noKtp, namaLengkap, ketTitle, rt, rw, kelurahan, kecamatan,
                kota, alamat, kodePos, nipBaru, email, password, jenisKelamin, agama, title;
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
        nipBaru = pref.getString("nip", null);
        email = pref.getString("email", null);
        password = pref.getString("pass", null);
        jenisKelamin = jenisKelaminSpinner.getSelectedItem().toString();
        agama = agamaSpinner.getSelectedItem().toString();
        title = titleSpinner.getSelectedItem().toString();

        /*User Handling*/
        // No KTP
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
        // Nama Lengkap
        if(TextUtils.isEmpty(namaLengkap)){
            namaLengkapEt.setError(KOLOM);
            namaLengkapEt.requestFocus();
            return;
        } else {
            namaLengkapEt.setError(null);
        }
        // Keterangan Title
        if(TextUtils.isEmpty(ketTitle)){
            ketTitleEt.setError(KOLOM);
            ketTitleEt.requestFocus();
            return;
        } else {
            ketTitleEt.setError(null);
        }
        // RT
        if(TextUtils.isEmpty(rt)){
            rtEt.setError(KOLOM);
            rtEt.requestFocus();
            return;
        } else {
            rtEt.setError(null);
        }
        // RW
        if(TextUtils.isEmpty(rw)){
            rwEt.setError(KOLOM);
            rwEt.requestFocus();
            return;
        } else {
            rwEt.setError(null);
        }
        // Kelurahan
        if(TextUtils.isEmpty(kelurahan)){
            kelurahanEt.setError(KOLOM);
            kelurahanEt.requestFocus();
            return;
        } else {
            kelurahanEt.setError(null);
        }
        // Kecamatan
        if(TextUtils.isEmpty(kecamatan)){
            kecamatanEt.setError(KOLOM);
            kecamatanEt.requestFocus();
            return;
        } else {
            kecamatanEt.setError(null);
        }
        // Kota
        if(TextUtils.isEmpty(kota)){
            kotaEt.setError(KOLOM);
            kotaEt.requestFocus();
            return;
        } else {
            kotaEt.setError(null);
        }
        // Alamat
        if(TextUtils.isEmpty(alamat)){
            alamatEt.setError(KOLOM);
            alamatEt.requestFocus();
            return;
        } else {
            alamatEt.setError(null);
        }
        // Kode Pos
        if(TextUtils.isEmpty(kodePos)){
            kodePosEt.setError(KOLOM);
            kodePosEt.requestFocus();
            return;
        } else {
            kodePosEt.setError(null);
        }
        /*final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Mohon Menunggu...");
        pDialog.show();*/

        editor.putString("no_ktp", noKtp);
        editor.putString("nama_lengkap", namaLengkap);
        editor.putString("title", title);
        editor.putString("ket_title", ketTitle);
        editor.putString("rt", rt);
        editor.putString("rw", rw);
        editor.putString("kelurahan", kelurahan);
        editor.putString("kecamatan", kecamatan);
        editor.putString("kota", kota);
        editor.putString("alamat", alamat);
        editor.putString("kode_pos", kodePos);
        editor.putString("jensi_kelamin", jenisKelamin);
        editor.putString("agama", agama);
        editor.commit();
        startActivity(new Intent(LengkapiData.this, TakePicture.class));

    }
}

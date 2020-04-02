package com.fidac.dumi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.fidac.dumi.api.PropinsiInterface;
import com.fidac.dumi.retrofit.RetrofitClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LengkapiData extends AppCompatActivity {

    private EditText noKtpEt, namaLengkapEt, ketTitleEt,
            rtEt, rwEt, kelurahanEt, kecamatanEt, kotaEt,
            alamatEt, kodePosEt;

    private Button lanjutButton;

    private Spinner jenisKelaminSpinner, agamaSpinner, titleSpinner,
            statusKawinSpinner, statusRumahSpinner, statusHubunganSpinner, propinsiSpinner,
            kabSpinner, kecSpinner, kelSpinner, kodePosSpinner;
    private ArrayAdapter<CharSequence> jenisKelaminAdapter, agamaAdapter, titleAdapter,
            statusKawinAdapter, statusRumahAdapter, statusHubunganAdapter;
    private ArrayAdapter<String> propinsiAdapter, kabAdapter, kecAdapter, kelAdapter, kodePosAdapter;
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

//        propinsiSpinner = findViewById(R.id.propinsi_spinner);
//        propinsiAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, );

        /*EditText Init*/
        noKtpEt = findViewById(R.id.no_ktp);
        namaLengkapEt = findViewById(R.id.nama_lengkap);
        ketTitleEt = findViewById(R.id.ket_title);
        rtEt = findViewById(R.id.rt);
        rwEt = findViewById(R.id.rw);
//        kelurahanEt = findViewById(R.id.kelurahan);
//        kecamatanEt = findViewById(R.id.kecamatan);
//        kotaEt = findViewById(R.id.kota);
        alamatEt = findViewById(R.id.alamat);
//        kodePosEt = findViewById(R.id.kode_pos);


        lanjutButton = findViewById(R.id.lanjut_button_lengkapi_data);

        lanjutButton.setOnClickListener(v -> {
            createUser();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cekPropinsi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cekPropinsi();
    }

    public void cekPropinsi() {
        /*Spinner init*/
        propinsiSpinner = findViewById(R.id.propinsi_spinner);
        kabSpinner = findViewById(R.id.kabupaten_spinner);
        kecSpinner = findViewById(R.id.kecamatan_spinner);
        kelSpinner = findViewById(R.id.kelurahan_spinner);
        kodePosSpinner = findViewById(R.id.kode_pos_spinner);
//        kodePosEt = findViewById(R.id.kode_pos_et);


        /*Progress Dialog*/
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Memuat Data...");
        pDialog.show();

        /*API CALL*/
        PropinsiInterface prop = RetrofitClient.getPropinsi().create(PropinsiInterface.class);
        Call<ResponseBody> call = prop.getPropinsi();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Prop", "onResponse: " + response.body().toString());
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    boolean status = obj.getBoolean("status");
                    if (status) {

                        String data = obj.getString("data");
                        JSONObject prop = new JSONObject(data);
                        /*Wilayah*/
                        String propinsi = prop.getString("provinsi");
                        String kabupaten = prop.getString("kabupaten");
                        String kecamatan = prop.getString("kecamatan");
                        String desa = prop.getString("desa");

                        /*JSONArray*/
                        JSONArray objPropArray = new JSONArray(propinsi);
                        JSONArray objKabArray = new JSONArray(kabupaten);
                        JSONArray objKecArray = new JSONArray(kecamatan);
                        JSONArray ojbDesArray = new JSONArray(desa);

                        /*ArraList*/
                        ArrayList<String> propArray = new ArrayList<>();
                        ArrayList<String> kabArray = new ArrayList<>();
                        ArrayList<String> kecArray = new ArrayList<>();
                        ArrayList<String> desArray = new ArrayList<>();
                        ArrayList<String> kodePosArray = new ArrayList<>();

                        /*Propinsi*/
                        for (int i = 0; i < objPropArray.length(); i++) {
                            JSONObject provinsi = objPropArray.getJSONObject(i);
                            propArray.add(provinsi.getString("provinsi"));
                        }
                        propinsiAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, propArray);
                        propinsiAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                        propinsiSpinner.setAdapter(propinsiAdapter);
                        propinsiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                /*Kabupaten*/
                                String propPilih = propinsiSpinner.getSelectedItem().toString();
                                kabArray.clear();
                                for(int i = 0; i<objKabArray.length(); i++){
                                    JSONObject kabu = null;
                                    try {
                                        kabu = objKabArray.getJSONObject(i);
                                        String prop = kabu.getString("provinsi");
                                        if (prop.equals(propPilih)) {
                                            kabArray.add(kabu.getString("kabupaten"));
                                            kabAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, kabArray);
                                            kabAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                                            kabSpinner.setAdapter(kabAdapter);
                                            kabSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                    /*Kecamatan*/
                                                    kecArray.clear();
                                                    String kabuPilih = kabSpinner.getSelectedItem().toString();
                                                    for(int i = 0; i < objKecArray.length(); i++){
                                                        try {
                                                            JSONObject kec = objKecArray.getJSONObject(i);
                                                            String kab = kec.getString("kabupaten");
                                                            if(kab.equals(kabuPilih)){
                                                                kecArray.add(kec.getString("kecamatan"));
                                                                kecAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, kecArray);
                                                                kecAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                                                                kecSpinner.setAdapter(kecAdapter);
                                                                kecSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                    @Override
                                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                        /*Kelurahan*/
                                                                        desArray.clear();
                                                                        kodePosArray.clear();
                                                                        String kecPilih = kecSpinner.getSelectedItem().toString();
                                                                        for(int i=0; i<ojbDesArray.length(); i++){
                                                                            try {
                                                                                JSONObject kel = ojbDesArray.getJSONObject(i);
                                                                                String kec = kel.getString("kecamatan");
                                                                                if(kec.equals(kecPilih)){
                                                                                    desArray.add(kel.getString("desa"));
                                                                                    kelAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, desArray);
                                                                                    kelAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                                                                                    kelSpinner.setAdapter(kelAdapter);

                                                                                    kodePosArray.add(kel.getString("kodepos"));
                                                                                    kodePosAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, kodePosArray);
                                                                                    kodePosAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                                                                                    kodePosSpinner.setAdapter(kodePosAdapter);
                                                                                }

                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                                    }
                                                                });
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        pDialog.dismiss();
                    }
                }catch (JSONException e) {
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            //moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    protected void exitByBackKey() {
        // do something when the button is clicked
        // do something when the button is clicked
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Apa anda yakin ingin keluar?")
                .setPositiveButton("Ya", (arg0, arg1) -> {
                    finish();
                    startActivity(new Intent());
                    //close();
                })
                .setNegativeButton("Tidak", (arg0, arg1) -> {
                })
                .show();
    }
}

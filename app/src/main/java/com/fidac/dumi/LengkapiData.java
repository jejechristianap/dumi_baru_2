package com.fidac.dumi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fidac.dumi.api.PropinsiInterface;
import com.fidac.dumi.retrofit.RetrofitClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LengkapiData extends AppCompatActivity {

    private EditText noKtpEt, namaLengkapEt, ketTitleEt,
            rtEt, rwEt, kelurahanEt, kecamatanEt, kotaEt,
            alamatEt, kodePosEt, tempatLahirEt, tanggalLahirEt,
            jumlahTanggunganEt, inskerKerjaEt, namaPenanggungEt,
            noKtpPenanggungEt, namaIbuEt;

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

    private ImageView pickDate;
    private Calendar myCalendar;

    private ProgressDialog pDialog;
    private PropinsiInterface prop;

    /*Wilayah*/
    private String propPilih;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lengkapi_data);

        pDialog = new ProgressDialog(LengkapiData.this);
        pDialog.setMessage("Memuat data...");

        pref = getApplicationContext().getSharedPreferences("Daftar", 0); // 0 - for private mode
        editor = pref.edit();

        /*Calendar*/
        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

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

        /*Wilayah*/
        propinsiSpinner = findViewById(R.id.propinsi_spinner);
        kabSpinner = findViewById(R.id.kabupaten_spinner);
        kecSpinner = findViewById(R.id.kecamatan_spinner);
        kelSpinner = findViewById(R.id.kelurahan_spinner);
        kodePosSpinner = findViewById(R.id.kode_pos_spinner);


        propinsiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*Kabupaten*/
                propPilih = propinsiSpinner.getSelectedItem().toString();
                getKabupaten(propPilih);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        kabSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*Kecamatan*/
                String kabuPilih = kabSpinner.getSelectedItem().toString();
                getKecamatan(kabuPilih);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        kecSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*Kelurahan*/
                String kecPilih = kecSpinner.getSelectedItem().toString();
                getKelurahanPos(kecPilih);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /*EditText Init*/
        noKtpEt = findViewById(R.id.no_ktp);
        namaLengkapEt = findViewById(R.id.nama_lengkap);
        tempatLahirEt = findViewById(R.id.tempat_lahir_et);
        pickDate = findViewById(R.id.pick_date);
        tanggalLahirEt = findViewById(R.id.tanggal_lahir_et);
        tanggalLahirEt.setEnabled(false);
        jumlahTanggunganEt = findViewById(R.id.jumlah_tanggungan_et);
        ketTitleEt = findViewById(R.id.ket_title);
        inskerKerjaEt = findViewById(R.id.insker_nama_et);
        alamatEt = findViewById(R.id.alamat);
        rtEt = findViewById(R.id.rt);
        rwEt = findViewById(R.id.rw);
        namaPenanggungEt = findViewById(R.id.nama_lengkap_penanggung_et);
        noKtpPenanggungEt = findViewById(R.id.no_ktp_penanggung_et);
        namaIbuEt = findViewById(R.id.nama_gadis_ibu_et);



        pickDate.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(LengkapiData.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });


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

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tanggalLahirEt.setText(sdf.format(myCalendar.getTime()));
    }

    public void cekPropinsi() {

        /*Progress Dialog*/
        pDialog.show();

        /*API CALL*/
        prop = RetrofitClient.getPropinsi().create(PropinsiInterface.class);
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
                        JSONArray objPropArray = new JSONArray(data);
                        ArrayList<String> propArray = new ArrayList<>();
                        for (int i = 0; i < objPropArray.length(); i++) {
                            JSONObject provinsi = objPropArray.getJSONObject(i);
                            propArray.add(provinsi.getString("provinsi"));
                        }
                        pDialog.dismiss();
                        Collections.sort(propArray);
                        propinsiAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, propArray);
                        propinsiAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                        propinsiSpinner.setAdapter(propinsiAdapter);
                        pDialog.dismiss();
                    }
                }catch (JSONException e) {
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
    }

    /*Get Kabupaten from Propinsi*/
    private void getKabupaten(String propPilih){
        pDialog.setMessage("Memuat Kabupaten...");
        pDialog.show();
        Call<ResponseBody> call = prop.getKabupaten(propPilih);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ArrayList<String> kabArray = new ArrayList<>();
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    String data = obj.getString("data");
                    JSONArray objKabArray = new JSONArray(data);
                    kabArray.clear();
                    pDialog.dismiss();
                    for(int i = 0; i<objKabArray.length(); i++){
                        JSONObject kabu = objKabArray.getJSONObject(i);
                        try {
                            String propinsi = kabu.getString("provinsi");
                            if (propinsi.equals(propPilih)) {
                                kabArray.add(kabu.getString("kabupaten"));
                                Collections.sort(kabArray);
                                kabAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, kabArray);
                                kabAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                                kabSpinner.setAdapter(kabAdapter);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException | IOException e) {
                    pDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
            }
        });

    }

    private void getKecamatan(String kabuPilih){
        pDialog.setMessage("Memuat Kecamatan...");
        pDialog.show();
        Call<ResponseBody> call = prop.getKecamatan(kabuPilih);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ArrayList<String> kecArray= new ArrayList<>();
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    String data = obj.getString("data");
                    JSONArray objKecArray = new JSONArray(data);
                    kecArray.clear();
                    pDialog.dismiss();
                    for(int i = 0; i < objKecArray.length(); i++){
                        try {
                            JSONObject kec = objKecArray.getJSONObject(i);
                            String kab = kec.getString("kabupaten");
                            if(kab.equals(kabuPilih)){
                                kecArray.add(kec.getString("kecamatan"));
                                Collections.sort(kecArray);
                                kecAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, kecArray);
                                kecAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                                kecSpinner.setAdapter(kecAdapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
            }
        });
    }

    private void getKelurahanPos(String kecPilih){
        pDialog.setMessage("Memuat Kelurahan...");
        pDialog.show();
        Call<ResponseBody> call = prop.getDesa(kecPilih);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ArrayList<String> desArray = new ArrayList<>();
                ArrayList<String> kodePosArray = new ArrayList<>();
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    String data = obj.getString("data");
                    JSONArray ojbDesArray = new JSONArray(data);
                    pDialog.dismiss();
                    desArray.clear();
                    kodePosArray.clear();
                    for(int i=0; i<ojbDesArray.length(); i++){
                        try {
                            JSONObject kel = ojbDesArray.getJSONObject(i);
                            String kec = kel.getString("kecamatan");
                            if(kec.equals(kecPilih)){
                                desArray.add(kel.getString("desa"));
                                kodePosArray.add(kel.getString("kodepos"));
                                /*
                                kelAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, desArray);
                                kelAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                                kelSpinner.setAdapter(kelAdapter);
                                kelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        HashSet<String> set = new HashSet<>(kodePosArray);
                                        kodePosArray.clear();
                                        kodePosArray.addAll(set);
                                        Collections.sort(kodePosArray); // Sorting array list using Collections.sort

                                        kodePosAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, kodePosArray);
                                        kodePosAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                                        kodePosSpinner.setAdapter(kodePosAdapter);

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });*/
                        }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    kelAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, desArray);
                    kelAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                    kelSpinner.setAdapter(kelAdapter);
                    kelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String kelPilih = kelSpinner.getSelectedItem().toString();
                            /*for(int i=0; i<ojbDesArray.length(); i++){
                                try {
                                    JSONObject kel = ojbDesArray.getJSONObject(i);
                                    int idPos = kel.getInt("id");
                                    String kec = kel.getString("kecamatan");
                                    if(kec.equals(kecPilih)){
                                        desArray.add(kel.getString("desa"));
                                        kodePosArray.add(kel.getString("kodepos"));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }*/

                            HashSet<String> set = new HashSet<>(kodePosArray);
                            kodePosArray.clear();
                            kodePosArray.addAll(set);
                            Collections.sort(kodePosArray); // Sorting array list using Collections.sort

                            kodePosAdapter = new ArrayAdapter<>(LengkapiData.this, R.layout.spinner_text, kodePosArray);
                            kodePosAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                            kodePosSpinner.setAdapter(kodePosAdapter);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
            }
        });
    }

   /* private void getPos(String desaPilih){
        Call<ResponseBody> call = prop.getPos(desaPilih);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }*/

    public void createUser(){
        String noKtp, namaLengkap, tempatLahir, tanggalLahir, statusKawin,
                jumlahTanggungan, title, ketTitle, inskerKerja,
                statusRumah, rt, rw, propinsi, kelurahan, kecamatan,
                kota, alamat, kodePos, jenisKelamin, agama,
                statusHubungan, namaPenanggung, noKtpPenanggung, namaIbu;

        // Data Pribadi
        noKtp = noKtpEt.getText().toString();
        namaLengkap = namaLengkapEt.getText().toString();
        agama = agamaSpinner.getSelectedItem().toString();
        jenisKelamin = jenisKelaminSpinner.getSelectedItem().toString();
        tempatLahir = tempatLahirEt.getText().toString();
        tanggalLahir = tanggalLahirEt.getText().toString();
        statusKawin = statusKawinSpinner.getSelectedItem().toString();
        jumlahTanggungan = jumlahTanggunganEt.getText().toString();
        title = titleSpinner.getSelectedItem().toString();
        ketTitle = ketTitleEt.getText().toString();
        inskerKerja = inskerKerjaEt.getText().toString();
        statusRumah = statusRumahSpinner.getSelectedItem().toString();
        alamat = alamatEt.getText().toString();
        rt = rtEt.getText().toString();
        rw = rwEt.getText().toString();
        /*Data Keluarga*/
        statusHubungan = statusHubunganSpinner.getSelectedItem().toString();
        namaPenanggung = namaPenanggungEt.getText().toString();
        noKtpPenanggung = noKtpPenanggungEt.getText().toString();
        namaIbu = namaIbuEt.getText().toString();

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

        // Alamat
        if(TextUtils.isEmpty(alamat)){
            alamatEt.setError(KOLOM);
            alamatEt.requestFocus();
            return;
        } else {
            alamatEt.setError(null);
        }

        if (TextUtils.isEmpty(noKtpPenanggung)){
            noKtpPenanggungEt.setError(KOLOM);
            noKtpPenanggungEt.requestFocus();
            return;
        } else if (noKtpPenanggung.length() != 16){
            noKtpPenanggungEt.setError("No KTP salah");
            noKtpPenanggungEt.requestFocus();
            return;
        } else {
            noKtpPenanggungEt.setError(null);
        }

        /*Wilayah*/
        propinsi = propinsiSpinner.getSelectedItem().toString();
        kota = kabSpinner.getSelectedItem().toString();
        kecamatan  = kecSpinner.getSelectedItem().toString();
        kelurahan = kelSpinner.getSelectedItem().toString();
        kodePos = kodePosSpinner.getSelectedItem().toString();

        Log.d("Input", "createUser: " + "\nKTP: " + noKtp + "\nNama: " + namaLengkap +
                "\nAgama: " + agama + "\nJK: " + jenisKelamin + "\n TempatLahir: " + tempatLahir +
                "\nTanggalLahir: " + tanggalLahir + "\nStatusKawin: " + statusKawin +
                "\nTanggungan: " + jumlahTanggungan + "\nGelar: " + title + "\nKetTit: " + ketTitle +
                "\nInskerKerja: " + inskerKerja + "\nStatusRumah: " + statusRumah + "\nAlamat: " + alamat +
                "\nRt: " + rt + "\nRw: " + rw + "\nPropinsi: " + propinsi + "\nKota: " + kota +
                "\nKecamatan: " + kecamatan + "\nKelurahan: " + kelurahan + "\nPos: " + kodePos +
                "\nStatus Hubungan: " + statusHubungan + "\nNama Penanggung: " + namaPenanggung +
                "\nKtp Penanggung: " + noKtpPenanggung + "\nNama Ibu: " + namaIbu);

        editor.putString("no_ktp", noKtp);
        editor.putString("nama_lengkap", namaLengkap);
        editor.putString("agama", agama);
        editor.putString("jensi_kelamin", jenisKelamin);
        editor.putString("tempat_lahir", tempatLahir);
        editor.putString("tanggal_lahir", tanggalLahir);
        editor.putString("status_kawin", statusKawin);
        editor.putString("jumlah_tanggungan", jumlahTanggungan);
        editor.putString("title", title);
        editor.putString("ket_title", ketTitle);
        editor.putString("insker", inskerKerja);
        editor.putString("status_rumah", statusRumah);
        editor.putString("alamat", alamat);
        editor.putString("rt", rt);
        editor.putString("rw", rw);
        editor.putString("propinsi", propinsi);
        editor.putString("kota", kota);
        editor.putString("kecamatan", kecamatan);
        editor.putString("kelurahan", kelurahan);
        editor.putString("kode_pos", kodePos);
        editor.putString("status_hubungan", statusHubungan);
        editor.putString("nama_penanggung", namaPenanggung);
        editor.putString("no_ktp_penanggung", noKtpPenanggung);
        editor.putString("nama_ibu", namaIbu);
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
                .setMessage("Apa anda yakin ingin kembali ke Halaman Depan?")
                .setPositiveButton("Ya", (arg0, arg1) -> {
                    finish();
                    startActivity(new Intent(LengkapiData.this, HalamanDepanActivity.class));
                    //close();
                })
                .setNegativeButton("Tidak", (arg0, arg1) -> {
                })
                .show();
    }
}

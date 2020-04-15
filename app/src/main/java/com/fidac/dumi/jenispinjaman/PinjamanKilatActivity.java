package com.fidac.dumi.jenispinjaman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.fidac.dumi.BankActivity;
import com.fidac.dumi.LengkapiData;
import com.fidac.dumi.MainActivity;
import com.fidac.dumi.R;
import com.fidac.dumi.api.GetBungaInterface;
import com.fidac.dumi.api.PinjamanKilatInterface;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;
import com.fidac.dumi.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PinjamanKilatActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView jumlah;
    private ImageView back;
    private int pinjamanUang;

    private TextView angsuranTv;
    private TextView biayaAdminTv;
    private TextView biayaAsuransiTv;
    private TextView biayaTransferTv;
    private TextView jumlahTerimaTv;

    private Button bulan3;
    private Button bulan6;
    private Button bulan12;
    private Button ajukanButton;

    private Spinner tujuanSpinner;
    private ArrayAdapter<CharSequence> tujuanAdapter;
    private String[] tujuanPinjaman = {"Renovasi Rumah", "Biaya Pendidikan", "Biaya Rumah Sakit",
            "Jalan-jalan", "Biaya Pernikahan", "Modal Usaha", "Lainnya"};

    private final int PEMBAYARAN_3_BULAN = 3;
    private final int PEMBAYARAN_6_BULAN = 6;
    private final int PEMBAYARAN_12_BULAN = 12;
    private final int JUMLAH_BULAN_1_TAHUN = 12;
    private final int JUMLAH_PINJAMAN_DEFAULT = 1000000;
    private final float BIAYA_ADMIN = (float) 0.01;
    private final float BIAYA_ASURANSI = (float) 0.01;
    private final float BIAYA_TRANSFER = 6500;
    private final float BUNGA_PERBULAN = (float) 0.099;
    private float bunga, admin, angsuran, asuransi, sisa;
    private int plafond;

    private float getBunga, getAdmin, getAsur12, getAsur24, getAsur36;

    private Locale localID;
    private NumberFormat formatRp;

    public static final String DATE_FORMAT_2 = "yyyy-MM-dd";

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinjaman_kilat);

        pref = getApplicationContext().getSharedPreferences("ajukanPinjaman", 0); // 0 - for private mode
        editor = pref.edit();

        tujuanSpinner = findViewById(R.id.tujuan_pinjaman_spinner);
        tujuanAdapter = new ArrayAdapter<>(PinjamanKilatActivity.this, R.layout.spinner_text, tujuanPinjaman);
        tujuanAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        tujuanSpinner.setAdapter(tujuanAdapter);

        angsuranTv = findViewById(R.id.angsuran_perbulan_kilat);
        biayaAdminTv = findViewById(R.id.biaya_administrasi_kilat);
        biayaAsuransiTv = findViewById(R.id.biaya_asuransi_kilat);
        biayaTransferTv = findViewById(R.id.biaya_transfer_antar_bank_kilat);
        jumlahTerimaTv = findViewById(R.id.jumlah_yang_diterima_kilat);
        jumlah = findViewById(R.id.rp_1jt_kilat);

        back = findViewById(R.id.back_kilat);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(PinjamanKilatActivity.this, MainActivity.class);
            startActivity(intent);
        });

        pinjamanUang = JUMLAH_PINJAMAN_DEFAULT;
        plafond = 0;
        bunga = 0;
        admin = 0;
        angsuran = 0;
        asuransi = 0;
        sisa = 0;

        localID = new Locale("in", "ID");
        formatRp = NumberFormat.getCurrencyInstance(localID);
        seekBar = findViewById(R.id.seekbar_kilat);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
                bulan3.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
                bulan6.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
                bulan12.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
                bulan3.setBackgroundResource(R.drawable.rect_normal);
                bulan6.setBackgroundResource(R.drawable.rect_normal);
                bulan12.setBackgroundResource(R.drawable.rect_normal);

                angsuranTv.setText("");
                biayaAdminTv.setText("");
                biayaAsuransiTv.setText("");
                biayaTransferTv.setText("");
                jumlahTerimaTv.setText("");

                switch (progressValue) {
                    case 0:
                        pinjamanUang = 1000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;

                    case 1:
                        pinjamanUang = 1500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;

                    case 2:
                        pinjamanUang = 2000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 3:
                        pinjamanUang = 2500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 4:
                        pinjamanUang = 3000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 5:
                        pinjamanUang = 3500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 6:
                        pinjamanUang = 4000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 7:
                        pinjamanUang = 4500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 8:
                        pinjamanUang = 5000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 9:
                        pinjamanUang = 5500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 10:
                        pinjamanUang = 6000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 11:
                        pinjamanUang = 6500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 12:
                        pinjamanUang = 7000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 13:
                        pinjamanUang = 7500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 14:
                        pinjamanUang = 8000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 15:
                        pinjamanUang = 8500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 16:
                        pinjamanUang = 9000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 17:
                        pinjamanUang = 9500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 18:
                        pinjamanUang = 10000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 19:
                        pinjamanUang = 10500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 20:
                        pinjamanUang = 11000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 21:
                        pinjamanUang = 11500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 22:
                        pinjamanUang = 12000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 23:
                        pinjamanUang = 12500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 24:
                        pinjamanUang = 13000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 25:
                        pinjamanUang = 13500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 26:
                        pinjamanUang = 14000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 27:
                        pinjamanUang = 14500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 28:
                        pinjamanUang = 15000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                switch (progressValue) {
                    case 0:
                        pinjamanUang = 1000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;

                    case 1:
                        pinjamanUang = 1500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;

                    case 2:
                        pinjamanUang = 2000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 3:
                        pinjamanUang = 2500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 4:
                        pinjamanUang = 3000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 5:
                        pinjamanUang = 3500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 6:
                        pinjamanUang = 4000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 7:
                        pinjamanUang = 4500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 8:
                        pinjamanUang = 5000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 9:
                        pinjamanUang = 5500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 10:
                        pinjamanUang = 6000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 11:
                        pinjamanUang = 6500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 12:
                        pinjamanUang = 7000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 13:
                        pinjamanUang = 7500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 14:
                        pinjamanUang = 8000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 15:
                        pinjamanUang = 8500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 16:
                        pinjamanUang = 9000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 17:
                        pinjamanUang = 9500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 18:
                        pinjamanUang = 10000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 19:
                        pinjamanUang = 10500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 20:
                        pinjamanUang = 11000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 21:
                        pinjamanUang = 11500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 22:
                        pinjamanUang = 12000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 23:
                        pinjamanUang = 12500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 24:
                        pinjamanUang = 13000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 25:
                        pinjamanUang = 13500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 26:
                        pinjamanUang = 14000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 27:
                        pinjamanUang = 14500000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 28:
                        pinjamanUang = 15000000;
                        jumlah.setText(formatRp.format((double)pinjamanUang));
                        return;
                }
            }
        });

        bulan3 = findViewById(R.id.bulan_3_kilat);
        bulan6 = findViewById(R.id.bulan_6_kilat);
        bulan12 = findViewById(R.id.bulan_12_kilat);

        bulan3.setOnClickListener(v -> {
            bulan3.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorWhite));
            bulan6.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
            bulan12.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
            bulan3.setBackgroundResource(R.drawable.rect_pressed);
            bulan6.setBackgroundResource(R.drawable.rect_normal);
            bulan12.setBackgroundResource(R.drawable.rect_normal);

            plafond = PEMBAYARAN_3_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * getBunga) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * getAdmin;
            asuransi = pinjamanUang * getAsur12;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranTv.setText(formatRp.format(angsuran));
            biayaAdminTv.setText(formatRp.format(admin));
            biayaAsuransiTv.setText(formatRp.format(asuransi));
            biayaTransferTv.setText(formatRp.format(BIAYA_TRANSFER));
            jumlahTerimaTv.setText(formatRp.format(sisa));
        });

        bulan6.setOnClickListener(v -> {
            bulan3.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
            bulan6.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorWhite));
            bulan12.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
            bulan6.setBackgroundResource(R.drawable.rect_pressed);
            bulan3.setBackgroundResource(R.drawable.rect_normal);
            bulan12.setBackgroundResource(R.drawable.rect_normal);

            plafond = PEMBAYARAN_6_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * getBunga) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * getAdmin;
            asuransi = pinjamanUang * getAsur12;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranTv.setText(formatRp.format(angsuran));
            biayaAdminTv.setText(formatRp.format(admin));
            biayaAsuransiTv.setText(formatRp.format(asuransi));
            biayaTransferTv.setText(formatRp.format(BIAYA_TRANSFER));
            jumlahTerimaTv.setText(formatRp.format(sisa));
        });

        bulan12.setOnClickListener(v -> {
            bulan3.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
            bulan6.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
            bulan12.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorWhite));
            bulan12.setBackgroundResource(R.drawable.rect_pressed);
            bulan3.setBackgroundResource(R.drawable.rect_normal);
            bulan6.setBackgroundResource(R.drawable.rect_normal);

            plafond = PEMBAYARAN_12_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * getBunga) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * getAdmin;
            asuransi = pinjamanUang * getAsur12;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranTv.setText(formatRp.format(angsuran));
            biayaAdminTv.setText(formatRp.format(admin));
            biayaAsuransiTv.setText(formatRp.format(asuransi));
            biayaTransferTv.setText(formatRp.format(BIAYA_TRANSFER));
            jumlahTerimaTv.setText(formatRp.format(sisa));
        });

        ajukanButton = findViewById(R.id.lanjut_button_kilat);
        ajukanButton.setOnClickListener(v -> {
            ajukanPinjaman();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getBunga = 0f;
        getAdmin = 0f;
        getAsur12 = 0f;
        getAsur24 = 0f;
        getAsur36 = 0f;
        getBunga();

    }

    private void getBunga() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Memuat Data...");
        pDialog.show();

        GetBungaInterface bunga = RetrofitClient.getClient().create(GetBungaInterface.class);
        Call<ResponseBody> call = bunga.getBunga();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    boolean status = obj.getBoolean("status");
                    Toast.makeText(PinjamanKilatActivity.this, "status: " + status, Toast.LENGTH_SHORT).show();
                    if (status){
                        String data = obj.getString("data");
                        JSONArray bungaObj = new JSONArray(data);

                        for (int i = 0; i<bungaObj.length(); i++){
                            JSONObject bung = bungaObj.getJSONObject(i);
                            int id = bung.getInt("id_bunga");
                            Toast.makeText(PinjamanKilatActivity.this, "instansi: " + id, Toast.LENGTH_SHORT).show();
                            if(id == 1){
                                Toast.makeText(PinjamanKilatActivity.this, "Get bunga", Toast.LENGTH_SHORT).show();
                                double bunga = bung.getDouble("bunga");
                                double biayaAdmin = bung.getDouble("biaya_admin");
                                double biayaAsuransi12 = bung.getDouble("biaya_asuransi_12");
                                double biayaAsuransi24 = bung.getDouble("biaya_asuransi_24");
                                double biayaAsuransi36 = bung.getDouble("biaya_asuransi_36");
                                getBunga = (float) bunga;
                                getAdmin = (float) biayaAdmin;
                                getAsur12 = (float) biayaAsuransi12;
                                getAsur24 = (float) biayaAsuransi24;
                                getAsur36 = (float) biayaAsuransi36;
                                Log.d("Biaya", "onResponse: " + "\nBunga: " + getBunga + "\nAdmin: " + getAdmin + "\nAsur12: " + getAsur12 +
                                        "\nAsur24: " + getAsur24 + "\nAsur36: " + getAsur36);
                            }
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

    private void ajukanPinjaman() {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String nip = user.getNip();
        String tujuan = tujuanSpinner.getSelectedItem().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_2);
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        String tglPinjam = dateFormat.format(today);

        c.add(Calendar.MONTH, plafond);
        final Date dueDate = c.getTime();
        String tglAkhirPinjam = dateFormat.format(dueDate);

        /*Log.d("Pinjaman",
                "nip: " + nip +
                        "\nPinjaman: " + pinjamanUang +
                        "\nPlafond: " + plafond +
                        "\nbunga: " + BUNGA_PERBULAN +
                        "\nBunga: " + bunga +
                        "\nAdmin: " + admin +
                        "\nAngsuran: " + angsuran +
                        "\nTrfBank: " + BIAYA_TRANSFER +
                        "\nTujuan: " + tujuan +
                        "\nTglPinjam: " + tglPinjam +
                        "\nAkhirTgl: " + tglAkhirPinjam +
                        "\nSisa: " + sisa +
                        "\nasurans: " + asuransi);*/

        editor.putString("nip", nip);
        editor.putFloat("pinjaman", pinjamanUang);
        editor.putInt("lamaPinjaman", plafond);
        editor.putFloat("bunga", bunga);
        editor.putFloat("admin", admin);
        editor.putFloat("angsuran", angsuran);
        editor.putFloat("diterima", sisa);
        editor.putString("tujuan", tujuan);
        editor.putString("tglMulai", tglPinjam);
        editor.putString("tglAkhir", tglAkhirPinjam);
        editor.putFloat("asuransi", asuransi);
        editor.commit();

        startActivity(new Intent(PinjamanKilatActivity.this, BankActivity.class));
        /*PinjamanKilatInterface pinjam = RetrofitClient.getClient().create(PinjamanKilatInterface.class);
        Call<ResponseBody> call = pinjam.ajukanPinjaman(nip, pinjamanUang, plafond, 0, BUNGA_PERBULAN,
                bunga, admin, angsuran, BIAYA_TRANSFER, tujuan, tglPinjam, tglAkhirPinjam, "", sisa, asuransi, );*/
    }
}

package com.fidac.dumi.jenispinjaman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.fidac.dumi.MainActivity;
import com.fidac.dumi.PelengkapanRegularActivity;
import com.fidac.dumi.R;
import com.fidac.dumi.api.GetBungaInterface;
import com.fidac.dumi.api.StatusPinjamanInterface;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;
import com.fidac.dumi.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class PinjamanRegularActivity extends AppCompatActivity {

    private ImageView backRegular;
    private TextView jumlahRegular;
    private SeekBar seekbarRegular;
    private int pinjamanUang;

    private Button bulan12;
    private Button bulan18;
    private Button bulan24;
    private Button bulan30;
    private Button bulan36;

    private TextView angsuranRegularTv;
    private TextView biayaAdminRegularTv;
    private TextView asuransiDiatas12;
    private TextView biayaAsuransiRegularTv;
    private TextView biayaTransferRegularTv;
    private TextView jumlahTerimaRegularTv;


    private final int PEMBAYARAN_12_BULAN = 12;
    private final int PEMBAYARAN_18_BULAN = 18;
    private final int PEMBAYARAN_24_BULAN = 24;
    private final int PEMBAYARAN_30_BULAN = 30;
    private final int PEMBAYARAN_36_BULAN = 36;
    private final int JUMLAH_BULAN_1_TAHUN = 12;
    private final int JUMLAH_PINJAMAN_DEFAULT = 16000000;
    private final float BIAYA_ADMIN = 0.01f;
    private final float BIAYA_ASURANSI = 0.01f;
    private final float BIAYA_ASURANSI_SD_24 = 0.016f;
    private final float BIAYA_ASURANSI_SD_36 = 0.021f;

    private final float BIAYA_TRANSFER = 6500;
    private final float BUNGA_PERBULAN = 0.099f;

    private float getBunga, getAdmin, getAsur12, getAsur24, getAsur36;

    private Locale localID;
    private NumberFormat formatRp;

    private Spinner tujuanSpinner;
    private ArrayAdapter<CharSequence> tujuanAdapter;
    private String[] tujuanPinjaman = {"Renovasi Rumah", "Biaya Pendidikan", "Biaya Rumah Sakit",
            "Jalan-jalan", "Biaya Pernikahan", "Modal Usaha", "Lainnya"};

    private Button lanjutButton;

    float bunga, admin, angsuran, asuransi, sisa;
    int plafond;

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private TextView adminTv, asurTv;
    private String getAsuransi = "";
    private String getAdministrasi = "";
    private boolean bkn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinjmana_reguler);

        /*SharedPref ajukanPinjaman*/
        pref = getApplicationContext().getSharedPreferences("ajukanPinjaman", 0); // 0 - for private mode
        editor = pref.edit();

        plafond = 0;
        bunga = 0;
        admin = 0;
        angsuran = 0;
        asuransi = 0;
        sisa = 0;

        backRegular = findViewById(R.id.back_regular);
        backRegular.setOnClickListener(v -> {
            Intent intent = new Intent(PinjamanRegularActivity.this, MainActivity.class);
            startActivity(intent);
        });

        /*Tujuan Pinjaman*/
        tujuanSpinner = findViewById(R.id.tujuan_pinjaman_reguler_spinner);
        tujuanAdapter = new ArrayAdapter<>(PinjamanRegularActivity.this, R.layout.spinner_text, tujuanPinjaman);
        tujuanAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        tujuanSpinner.setAdapter(tujuanAdapter);

        localID = new Locale("in", "ID");
        formatRp = NumberFormat.getCurrencyInstance(localID);

        angsuranRegularTv = findViewById(R.id.angsuran_perbulan_regular);
        biayaAdminRegularTv = findViewById(R.id.biaya_administrasi_regular);
        asuransiDiatas12 = findViewById(R.id.asuransi_12_36_bulan);
        biayaAsuransiRegularTv = findViewById(R.id.biaya_asuransi_regular);
        biayaTransferRegularTv = findViewById(R.id.biaya_transfer_antar_bank_regular);
        jumlahTerimaRegularTv = findViewById(R.id.jumlah_yang_diterima_regular);

        adminTv = findViewById(R.id.tvAdminReg);
        asurTv = findViewById(R.id.tvAsurReg);

        /*Jumlah yang akan dipinjam*/
        pinjamanUang = JUMLAH_PINJAMAN_DEFAULT;
        jumlahRegular = findViewById(R.id.rp_16jt_regular);
        seekbarRegular = findViewById(R.id.seekbar_regular);
        seekbarRegular.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
                bulan12.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
                bulan18.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
                bulan24.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
                bulan30.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
                bulan36.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
                bulan12.setBackgroundResource(R.drawable.rect_normal);
                bulan18.setBackgroundResource(R.drawable.rect_normal);
                bulan24.setBackgroundResource(R.drawable.rect_normal);
                bulan30.setBackgroundResource(R.drawable.rect_normal);
                bulan36.setBackgroundResource(R.drawable.rect_normal);

                angsuranRegularTv.setText("");
                biayaAdminRegularTv.setText("");
//                asuransiDiatas12.setText("Biaya Asuransi");
                biayaAsuransiRegularTv.setText("");
                biayaTransferRegularTv.setText("");

                switch (progressValue) {
                    case 0:
                        pinjamanUang = JUMLAH_PINJAMAN_DEFAULT;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;

                    case 1:
                        pinjamanUang = 18000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;

                    case 2:
                        pinjamanUang = 20000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 3:
                        pinjamanUang = 22000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 4:
                        pinjamanUang = 24000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 5:
                        pinjamanUang = 26000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 6:
                        pinjamanUang = 28000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 7:
                        pinjamanUang = 30000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 8:
                        pinjamanUang = 32000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 9:
                        pinjamanUang = 34000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 10:
                        pinjamanUang = 36000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 11:
                        pinjamanUang = 38000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 12:
                        pinjamanUang = 40000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 13:
                        pinjamanUang = 42000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 14:
                        pinjamanUang = 44000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 15:
                        pinjamanUang = 46000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 16:
                        pinjamanUang = 48000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 17:
                        pinjamanUang = 50000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
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
                        pinjamanUang = 16000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;

                    case 1:
                        pinjamanUang = 18000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;

                    case 2:
                        pinjamanUang = 20000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 3:
                        pinjamanUang = 22000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 4:
                        pinjamanUang = 24000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 5:
                        pinjamanUang = 26000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 6:
                        pinjamanUang = 28000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 7:
                        pinjamanUang = 30000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 8:
                        pinjamanUang = 32000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 9:
                        pinjamanUang = 34000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 10:
                        pinjamanUang = 36000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 11:
                        pinjamanUang = 38000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 12:
                        pinjamanUang = 40000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 13:
                        pinjamanUang = 42000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 14:
                        pinjamanUang = 44000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 15:
                        pinjamanUang = 46000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 16:
                        pinjamanUang = 48000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                    case 17:
                        pinjamanUang = 50000000;
                        jumlahRegular.setText(formatRp.format((double)pinjamanUang));
                        return;
                }
            }
        });

        bulan12 = findViewById(R.id.bulan_12_regular);
        bulan18 = findViewById(R.id.bulan_18_regular);
        bulan24 = findViewById(R.id.bulan_24_regular);
        bulan30 = findViewById(R.id.bulan_30_regular);
        bulan36 = findViewById(R.id.bulan_36_regular);

        bulan12.setOnClickListener(v -> {
            /*Button Text Color Pressed*/
            bulan12.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorWhite));
            bulan18.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan24.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan30.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan36.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            /*Button Background Press*/
            bulan12.setBackgroundResource(R.drawable.rect_pressed);
            bulan18.setBackgroundResource(R.drawable.rect_normal);
            bulan24.setBackgroundResource(R.drawable.rect_normal);
            bulan30.setBackgroundResource(R.drawable.rect_normal);
            bulan36.setBackgroundResource(R.drawable.rect_normal);

            if(bkn){
                getAsuransi = "Biaya Asuransi 1%";
                getAdministrasi = "Biaya Administrasi 1%";
            } else {
                getAsuransi = "Biaya Asuransi 2%";
                getAdministrasi = "Biaya Administrasi 2%";
            }
            adminTv.setText(getAdministrasi);
            asurTv.setText(getAsuransi);
            /*Loan Calculation*/
            plafond = PEMBAYARAN_12_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * getBunga) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * getAdmin;
            asuransi = pinjamanUang * getAsur12;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;


            angsuranRegularTv.setText(formatRp.format((double)angsuran));
            biayaAdminRegularTv.setText(formatRp.format((double)admin));
            biayaAsuransiRegularTv.setText(formatRp.format((double)asuransi));
            biayaTransferRegularTv.setText(formatRp.format((double)BIAYA_TRANSFER));
            jumlahTerimaRegularTv.setText(formatRp.format((double)sisa));
        });

        bulan18.setOnClickListener(v -> {
            /*Button Text Color Pressed*/
            bulan12.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan18.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorWhite));
            bulan24.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan30.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan36.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            /*Button Background Press*/
            bulan12.setBackgroundResource(R.drawable.rect_normal);
            bulan18.setBackgroundResource(R.drawable.rect_pressed);
            bulan24.setBackgroundResource(R.drawable.rect_normal);
            bulan30.setBackgroundResource(R.drawable.rect_normal);
            bulan36.setBackgroundResource(R.drawable.rect_normal);

            if(bkn){
                getAsuransi = "Biaya Asuransi 1.6%";
                getAdministrasi = "Biaya Administrasi 1%";
            } else {
                getAsuransi = "Biaya Asuransi 2%";
                getAdministrasi = "Biaya Administrasi 2%";
            }
            adminTv.setText(getAdministrasi);
            asurTv.setText(getAsuransi);

            /*Loan Calculation*/
            plafond = PEMBAYARAN_18_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * getBunga) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * getAdmin;
            asuransi = pinjamanUang * getAsur24;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranRegularTv.setText(formatRp.format((double)angsuran));
            biayaAdminRegularTv.setText(formatRp.format((double)admin));
            biayaAsuransiRegularTv.setText(formatRp.format((double)asuransi));
            biayaTransferRegularTv.setText(formatRp.format((double)BIAYA_TRANSFER));
            jumlahTerimaRegularTv.setText(formatRp.format((double)sisa));
        });

        bulan24.setOnClickListener(v -> {
            /*Button Text Color Pressed*/
            bulan12.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan18.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan24.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorWhite));
            bulan30.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan36.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            /*Button Background Press*/
            bulan12.setBackgroundResource(R.drawable.rect_normal);
            bulan18.setBackgroundResource(R.drawable.rect_normal);
            bulan24.setBackgroundResource(R.drawable.rect_pressed);
            bulan30.setBackgroundResource(R.drawable.rect_normal);
            bulan36.setBackgroundResource(R.drawable.rect_normal);

            if(bkn){
                getAsuransi = "Biaya Asuransi 1.6%";
                getAdministrasi = "Biaya Administrasi 1%";
            } else {
                getAsuransi = "Biaya Asuransi 2%";
                getAdministrasi = "Biaya Administrasi 2%";
            }
            adminTv.setText(getAdministrasi);
            asurTv.setText(getAsuransi);

            /*Loan Calculation 24*/
            plafond = PEMBAYARAN_24_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * getBunga) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * getAdmin;
            asuransi = pinjamanUang * getAsur24;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranRegularTv.setText(formatRp.format((double)angsuran));
            biayaAdminRegularTv.setText(formatRp.format((double)admin));
            biayaAsuransiRegularTv.setText(formatRp.format((double)asuransi));
            biayaTransferRegularTv.setText(formatRp.format((double)BIAYA_TRANSFER));
            jumlahTerimaRegularTv.setText(formatRp.format((double)sisa));
        });

        bulan30.setOnClickListener(v -> {
            /*Button Text Color Pressed*/
            bulan12.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan18.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan24.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan30.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorWhite));
            bulan36.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            /*Button Background Press*/
            bulan12.setBackgroundResource(R.drawable.rect_normal);
            bulan18.setBackgroundResource(R.drawable.rect_normal);
            bulan24.setBackgroundResource(R.drawable.rect_normal);
            bulan30.setBackgroundResource(R.drawable.rect_pressed);
            bulan36.setBackgroundResource(R.drawable.rect_normal);

            if(bkn){
                getAsuransi = "Biaya Asuransi 2.1%";
                getAdministrasi = "Biaya Administrasi 1%";
            } else {
                getAsuransi = "Biaya Asuransi 2%";
                getAdministrasi = "Biaya Administrasi 2%";
            }
            adminTv.setText(getAdministrasi);
            asurTv.setText(getAsuransi);

            /*Loan Calculation 30*/
            plafond = PEMBAYARAN_30_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * getBunga) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * getAdmin;
            asuransi = pinjamanUang * getAsur36;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranRegularTv.setText(formatRp.format(angsuran));
            biayaAdminRegularTv.setText(formatRp.format(admin));
            biayaAsuransiRegularTv.setText(formatRp.format(asuransi));
            biayaTransferRegularTv.setText(formatRp.format(BIAYA_TRANSFER));
            jumlahTerimaRegularTv.setText(formatRp.format(sisa));
        });

        bulan36.setOnClickListener(v -> {
            /*Button Text Color Pressed*/
            bulan12.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan18.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan24.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan30.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorBlue));
            bulan36.setTextColor(PinjamanRegularActivity.this.getColor(R.color.colorWhite));
            /*Button Background Press*/
            bulan12.setBackgroundResource(R.drawable.rect_normal);
            bulan18.setBackgroundResource(R.drawable.rect_normal);
            bulan24.setBackgroundResource(R.drawable.rect_normal);
            bulan30.setBackgroundResource(R.drawable.rect_normal);
            bulan36.setBackgroundResource(R.drawable.rect_pressed);
            if(bkn){
                getAsuransi = "Biaya Asuransi 2.1%";
                getAdministrasi = "Biaya Administrasi 1%";
            } else {
                getAsuransi = "Biaya Asuransi 2%";
                getAdministrasi = "Biaya Administrasi 2%";
            }
            adminTv.setText(getAdministrasi);
            asurTv.setText(getAsuransi);

            /*Loan Calculation 36*/
            plafond = PEMBAYARAN_36_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * getBunga) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * getAdmin;
            asuransi = pinjamanUang * getAsur36;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranRegularTv.setText(formatRp.format(angsuran));
            biayaAdminRegularTv.setText(formatRp.format(admin));
            biayaAsuransiRegularTv.setText(formatRp.format(asuransi));
            biayaTransferRegularTv.setText(formatRp.format(BIAYA_TRANSFER));
            jumlahTerimaRegularTv.setText(formatRp.format(sisa));
        });

        lanjutButton = findViewById(R.id.lanjut_button_reguler);
        lanjutButton.setOnClickListener(v -> {
            String angs = angsuranRegularTv.getText().toString();
            if (TextUtils.isEmpty(angs)){
                Toast.makeText(this, "Tentukan Lama Pembayaran", Toast.LENGTH_SHORT).show();
                return;
            }
//            ajukanPinjaman();
            getPinjaman();
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
//        bkn = false;

    }

    private void getBunga() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Memuat Data...");
        pDialog.show();
        User pref = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String insker = pref.getInskerKerja();

        GetBungaInterface bunga = RetrofitClient.getClient().create(GetBungaInterface.class);
        Call<ResponseBody> call = bunga.getBunga();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    boolean status = obj.getBoolean("status");
//                    Toast.makeText(PinjamanKilatActivity.this, "status: " + status, Toast.LENGTH_SHORT).show();
                    if (status){
                        String data = obj.getString("data");
                        JSONArray bungaObj = new JSONArray(data);
                        for (int i = 0; i<bungaObj.length(); i++){
                            JSONObject bung = bungaObj.getJSONObject(i);
                            int id = bung.getInt("id_bunga");
                            if(id == 1 && insker.contains("Badan Kepegawaian Negara")){
                                bkn = true;
                                adminTv.setText("Biaya Administrasi 1%");
                                asurTv.setText("Biaya Asuransi 1%");
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
                            } else if (id == 2 && !insker.contains("Badan Kepegawaian Negara")){
//                                Toast.makeText(PinjamanKilatActivity.this, "Non BKN", Toast.LENGTH_SHORT).show();
                                bkn = false;
                                adminTv.setText("Biaya Administrasi 2%");
                                asurTv.setText("Biaya Asuransi 2%");
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

    private void getPinjaman(){
        User pref = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String nip = pref.getNip();
        StatusPinjamanInterface status = RetrofitClient.getClient().create(StatusPinjamanInterface.class);
        Call<ResponseBody> call = status.getPinjaman(nip);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    boolean cek = obj.getBoolean("status");
                    if (cek){
                        String data = obj.getString("data");
                        JSONArray jsonArray = new JSONArray(data);
                        ArrayList<Integer> statusPinjaman = new ArrayList<>();
                        if (!jsonArray.isNull(0)){
                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                statusPinjaman.add(jsonObject.getInt("status"));
                            }
                            if(statusPinjaman.contains(1)){
                                Toast.makeText(PinjamanRegularActivity.this, "Anda sudah mengajukan pinjaman. Mohon menunggu info dari kami.", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (statusPinjaman.contains(2)){
                                Toast.makeText(PinjamanRegularActivity.this, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (statusPinjaman.contains(4)){
                                Toast.makeText(PinjamanRegularActivity.this, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (statusPinjaman.contains(5)){
                                Toast.makeText(PinjamanRegularActivity.this, "Masih ada tagihan yang belum selesai, terima kasih." , Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                /*if(statusId == 1){
                                    Toast.makeText(PinjamanKilatActivity.this, "Anda sudah mengajukan pinjaman. Mohon menunggu info dari kami.", Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (statusId == 2){
                                    Toast.makeText(PinjamanKilatActivity.this, "Masih ada tagihan yang belum selesai, terima kasih." + statusId, Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (statusId == 3){
                                    ajukanPinjaman();
                                } else if (statusId == 4){
                                    Toast.makeText(PinjamanKilatActivity.this, "Masih ada tagihan yang belum selesai, terima kasih." + statusId, Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (statusId == 5){
                                    Toast.makeText(PinjamanKilatActivity.this, "Masih ada tagihan yang belum selesai, terima kasih." + statusId, Toast.LENGTH_SHORT).show();
                                    return;
                                } else if(statusId == 6){

                                }*/
                                ajukanPinjaman();
                            }
                        }else {
                            ajukanPinjaman();
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

            }
        });

    }

    private void ajukanPinjaman() {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String nip = user.getNip();
        String tujuan = tujuanSpinner.getSelectedItem().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        String tglPinjam = dateFormat.format(today);

        c.add(Calendar.MONTH, plafond);
        final Date dueDate = c.getTime();
        String tglAkhirPinjam = dateFormat.format(dueDate);

        Log.d("Pinjaman",
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
                        "\nasurans: " + asuransi);

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

        startActivity(new Intent(PinjamanRegularActivity.this, PelengkapanRegularActivity.class));

        /*PinjamanKilatInterface pinjam = RetrofitClient.getClient().create(PinjamanKilatInterface.class);
        Call<ResponseBody> call = pinjam.ajukanPinjaman(nip, pinjamanUang, plafond, 0, BUNGA_PERBULAN,
                bunga, admin, angsuran, BIAYA_TRANSFER, tujuan, tglPinjam, tglAkhirPinjam, "", sisa, asuransi, );*/
    }
}

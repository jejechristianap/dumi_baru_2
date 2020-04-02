package com.fidac.dumi.jenispinjaman;

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

import androidx.appcompat.app.AppCompatActivity;


import com.fidac.dumi.BankActivity;
import com.fidac.dumi.MainActivity;
import com.fidac.dumi.R;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        backRegular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PinjamanRegularActivity.this, MainActivity.class);
                startActivity(intent);
            }
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
                asuransiDiatas12.setText("Biaya Asuransi");
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

            /*Loan Calculation*/
            plafond = PEMBAYARAN_12_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * BIAYA_ADMIN;
            asuransi = pinjamanUang * BIAYA_ASURANSI;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranRegularTv.setText(formatRp.format((double)angsuran));
            biayaAdminRegularTv.setText(formatRp.format((double)admin));
            asuransiDiatas12.setText("Biaya Asuransi 1%");
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

            /*Loan Calculation*/
            plafond = PEMBAYARAN_18_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * BIAYA_ADMIN;
            asuransi = pinjamanUang * BIAYA_ASURANSI_SD_24;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranRegularTv.setText(formatRp.format((double)angsuran));
            biayaAdminRegularTv.setText(formatRp.format((double)admin));
            asuransiDiatas12.setText("Biaya Asuransi 1.6%");
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

            /*Loan Calculation 24*/
            plafond = PEMBAYARAN_24_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * BIAYA_ADMIN;
            asuransi = pinjamanUang * BIAYA_ASURANSI_SD_24;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranRegularTv.setText(formatRp.format((double)angsuran));
            biayaAdminRegularTv.setText(formatRp.format((double)admin));
            asuransiDiatas12.setText("Biaya Asuransi 1.6%");
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

            /*Loan Calculation 30*/
            plafond = PEMBAYARAN_30_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * BIAYA_ADMIN;
            asuransi = pinjamanUang * BIAYA_ASURANSI_SD_36;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranRegularTv.setText(formatRp.format((double)angsuran));
            biayaAdminRegularTv.setText(formatRp.format((double)admin));
            asuransiDiatas12.setText("Biaya Asuransi 2.1%");
            biayaAsuransiRegularTv.setText(formatRp.format((double)asuransi));
            biayaTransferRegularTv.setText(formatRp.format((double)BIAYA_TRANSFER));
            jumlahTerimaRegularTv.setText(formatRp.format((double)sisa));
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

            /*Loan Calculation 30*/
            plafond = PEMBAYARAN_36_BULAN;
            float pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * BIAYA_ADMIN;
            asuransi = pinjamanUang * BIAYA_ASURANSI_SD_36;
            float totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            sisa = pinjamanUang - totalPengurangan;

            angsuranRegularTv.setText(formatRp.format((double)angsuran));
            biayaAdminRegularTv.setText(formatRp.format((double)admin));
            asuransiDiatas12.setText("Biaya Asuransi 2.1%");
            biayaAsuransiRegularTv.setText(formatRp.format((double)asuransi));
            biayaTransferRegularTv.setText(formatRp.format((double)BIAYA_TRANSFER));
            jumlahTerimaRegularTv.setText(formatRp.format((double)sisa));
        });

        lanjutButton = findViewById(R.id.lanjut_button_reguler);
        lanjutButton.setOnClickListener(v -> {
            ajukanPinjaman();
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

        startActivity(new Intent(PinjamanRegularActivity.this, BankActivity.class));

        /*PinjamanKilatInterface pinjam = RetrofitClient.getClient().create(PinjamanKilatInterface.class);
        Call<ResponseBody> call = pinjam.ajukanPinjaman(nip, pinjamanUang, plafond, 0, BUNGA_PERBULAN,
                bunga, admin, angsuran, BIAYA_TRANSFER, tujuan, tglPinjam, tglAkhirPinjam, "", sisa, asuransi, );*/
    }
}

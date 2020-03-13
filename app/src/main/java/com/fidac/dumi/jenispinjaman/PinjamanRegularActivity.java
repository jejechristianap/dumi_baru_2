package com.fidac.dumi.jenispinjaman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.fidac.dumi.MainActivity;
import com.fidac.dumi.R;

import java.text.NumberFormat;
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
    private final double BIAYA_ADMIN = 0.01;
    private final double BIAYA_ASURANSI = 0.01;
    private final double BIAYA_ASURANSI_SD_24 = 0.016;
    private final double BIAYA_ASURANSI_SD_36 = 0.021;

    private final double BIAYA_TRANSFER = 6500;
    private final double BUNGA_PERBULAN = 0.099;

    private Locale localID;
    private NumberFormat formatRp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinjmana_reguler);
        backRegular = findViewById(R.id.back_regular);
        backRegular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PinjamanRegularActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

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

        bulan12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                double pokok = pinjamanUang / PEMBAYARAN_12_BULAN;
                double bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
                double angsuran = pokok + bunga;
                double admin = pinjamanUang * BIAYA_ADMIN;
                double asuransi = pinjamanUang * BIAYA_ASURANSI;
                double totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
                double sisa = pinjamanUang - totalPengurangan;

                angsuranRegularTv.setText(formatRp.format((double)angsuran));
                biayaAdminRegularTv.setText(formatRp.format((double)admin));
                asuransiDiatas12.setText("Biaya Asuransi 1%");
                biayaAsuransiRegularTv.setText(formatRp.format((double)asuransi));
                biayaTransferRegularTv.setText(formatRp.format((double)BIAYA_TRANSFER));
                jumlahTerimaRegularTv.setText(formatRp.format((double)sisa));
            }
        });

        bulan18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                double pokok = pinjamanUang / PEMBAYARAN_18_BULAN;
                double bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
                double angsuran = pokok + bunga;
                double admin = pinjamanUang * BIAYA_ADMIN;
                double asuransi = pinjamanUang * BIAYA_ASURANSI_SD_24;
                double totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
                double sisa = pinjamanUang - totalPengurangan;

                angsuranRegularTv.setText(formatRp.format((double)angsuran));
                biayaAdminRegularTv.setText(formatRp.format((double)admin));
                asuransiDiatas12.setText("Biaya Asuransi 1.6%");
                biayaAsuransiRegularTv.setText(formatRp.format((double)asuransi));
                biayaTransferRegularTv.setText(formatRp.format((double)BIAYA_TRANSFER));
                jumlahTerimaRegularTv.setText(formatRp.format((double)sisa));
            }
        });

        bulan24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                /*Loan Calculation*/
                double pokok = pinjamanUang / PEMBAYARAN_24_BULAN;
                double bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
                double angsuran = pokok + bunga;
                double admin = pinjamanUang * BIAYA_ADMIN;
                double asuransi = pinjamanUang * BIAYA_ASURANSI_SD_24;
                double totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
                double sisa = pinjamanUang - totalPengurangan;

                angsuranRegularTv.setText(formatRp.format((double)angsuran));
                biayaAdminRegularTv.setText(formatRp.format((double)admin));
                asuransiDiatas12.setText("Biaya Asuransi 1.6%");
                biayaAsuransiRegularTv.setText(formatRp.format((double)asuransi));
                biayaTransferRegularTv.setText(formatRp.format((double)BIAYA_TRANSFER));
                jumlahTerimaRegularTv.setText(formatRp.format((double)sisa));
            }
        });

        bulan30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                /*Loan Calculation*/
                double pokok = pinjamanUang / PEMBAYARAN_30_BULAN;
                double bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
                double angsuran = pokok + bunga;
                double admin = pinjamanUang * BIAYA_ADMIN;
                double asuransi = pinjamanUang * BIAYA_ASURANSI_SD_36;
                double totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
                double sisa = pinjamanUang - totalPengurangan;

                angsuranRegularTv.setText(formatRp.format((double)angsuran));
                biayaAdminRegularTv.setText(formatRp.format((double)admin));
                asuransiDiatas12.setText("Biaya Asuransi 2.1%");
                biayaAsuransiRegularTv.setText(formatRp.format((double)asuransi));
                biayaTransferRegularTv.setText(formatRp.format((double)BIAYA_TRANSFER));
                jumlahTerimaRegularTv.setText(formatRp.format((double)sisa));
            }
        });

        bulan36.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                /*Loan Calculation*/
                double pokok = pinjamanUang / PEMBAYARAN_36_BULAN;
                double bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
                double angsuran = pokok + bunga;
                double admin = pinjamanUang * BIAYA_ADMIN;
                double asuransi = pinjamanUang * BIAYA_ASURANSI_SD_36;
                double totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
                double sisa = pinjamanUang - totalPengurangan;

                angsuranRegularTv.setText(formatRp.format((double)angsuran));
                biayaAdminRegularTv.setText(formatRp.format((double)admin));
                asuransiDiatas12.setText("Biaya Asuransi 2.1%");
                biayaAsuransiRegularTv.setText(formatRp.format((double)asuransi));
                biayaTransferRegularTv.setText(formatRp.format((double)BIAYA_TRANSFER));
                jumlahTerimaRegularTv.setText(formatRp.format((double)sisa));
            }
        });
    }
}

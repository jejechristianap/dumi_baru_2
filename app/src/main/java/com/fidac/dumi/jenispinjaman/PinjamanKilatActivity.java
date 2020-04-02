package com.fidac.dumi.jenispinjaman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.fidac.dumi.LengkapiData;
import com.fidac.dumi.MainActivity;
import com.fidac.dumi.R;
import com.fidac.dumi.api.PinjamanKilatInterface;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;
import com.fidac.dumi.retrofit.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;

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
    private final double BIAYA_ADMIN = 0.01;
    private final double BIAYA_ASURANSI = 0.01;
    private final double BIAYA_TRANSFER = 6500;
    private final double BUNGA_PERBULAN = 0.099;
    double plafond, bunga, admin, angsuran, asuransi;

    private Locale localID;
    private NumberFormat formatRp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinjaman_kilat);

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
            double pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * BIAYA_ADMIN;
            asuransi = pinjamanUang * BIAYA_ASURANSI;
            double totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            double sisa = pinjamanUang - totalPengurangan;

            angsuranTv.setText(formatRp.format(angsuran));
            biayaAdminTv.setText(formatRp.format((double)admin));
            biayaAsuransiTv.setText(formatRp.format((double)asuransi));
            biayaTransferTv.setText(formatRp.format((double)BIAYA_TRANSFER));
            jumlahTerimaTv.setText(formatRp.format((double)sisa));
        });

        bulan6.setOnClickListener(v -> {
            bulan3.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
            bulan6.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorWhite));
            bulan12.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
            bulan6.setBackgroundResource(R.drawable.rect_pressed);
            bulan3.setBackgroundResource(R.drawable.rect_normal);
            bulan12.setBackgroundResource(R.drawable.rect_normal);

            plafond = PEMBAYARAN_6_BULAN;
            double pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * BIAYA_ADMIN;
            asuransi = pinjamanUang * BIAYA_ASURANSI;
            double totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            double sisa = pinjamanUang - totalPengurangan;

            angsuranTv.setText(formatRp.format((double)angsuran));
            biayaAdminTv.setText(formatRp.format((double)admin));
            biayaAsuransiTv.setText(formatRp.format((double)asuransi));
            biayaTransferTv.setText(formatRp.format((double)BIAYA_TRANSFER));
            jumlahTerimaTv.setText(formatRp.format((double)sisa));
        });

        bulan12.setOnClickListener(v -> {
            bulan3.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
            bulan6.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorBlue));
            bulan12.setTextColor(PinjamanKilatActivity.this.getColor(R.color.colorWhite));
            bulan12.setBackgroundResource(R.drawable.rect_pressed);
            bulan3.setBackgroundResource(R.drawable.rect_normal);
            bulan6.setBackgroundResource(R.drawable.rect_normal);

            plafond = PEMBAYARAN_12_BULAN;
            double pokok = pinjamanUang / plafond;
            bunga = (pinjamanUang * BUNGA_PERBULAN) / JUMLAH_BULAN_1_TAHUN;
            angsuran = pokok + bunga;
            admin = pinjamanUang * BIAYA_ADMIN;
            asuransi = pinjamanUang * BIAYA_ASURANSI;
            double totalPengurangan = admin + asuransi + BIAYA_TRANSFER;
            double sisa = pinjamanUang - totalPengurangan;

            angsuranTv.setText(formatRp.format((double)angsuran));
            biayaAdminTv.setText(formatRp.format((double)admin));
            biayaAsuransiTv.setText(formatRp.format((double)asuransi));
            biayaTransferTv.setText(formatRp.format((double)BIAYA_TRANSFER));
            jumlahTerimaTv.setText(formatRp.format((double)sisa));
        });

        ajukanButton = findViewById(R.id.ajukan_button);
        ajukanButton.setOnClickListener(v -> {
            ajukanPinjaman();
        });

    }

    private void ajukanPinjaman() {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String nip = user.getNip();
        String tujuan = tujuanSpinner.getSelectedItem().toString();

        PinjamanKilatInterface pinjam = RetrofitClient.getClient().create(PinjamanKilatInterface.class);
        /*Call<ResponseBody> call = pinjam.ajukanPinjaman(nip, pinjamanUang, plafond, 0, BUNGA_PERBULAN,
                bunga, admin, angsuran, asuransi, tujuan, );*/
    }
}

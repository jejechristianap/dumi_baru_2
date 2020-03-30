package com.fidac.dumi.akun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.fidac.dumi.R;
import com.fidac.dumi.fragment.AkunFragment;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;

public class RincianAkunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rincian_akun);

        EditText noKtpEt, namaEt, agamaEt, titleEt, rtEt, rwEt, kelurahanEt, kecamatanEt,
                kotaEt, alamatEt, kodePosEt, noTelpEt;

//        Button kembaliButton = findViewById(R.id.kembali_button);


        User prefManager = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String noKtp = prefManager.getNoKtp();
        String nama = prefManager.getNamaLengkap();
        String agama = prefManager.getAgama();
        String title = prefManager.getTitle() + " (" + prefManager.getKetTitle() + ")";
        String rt = prefManager.getRt();
        String rw = prefManager.getRw();
        String kelurahan = prefManager.getKelurahan();
        String kecamatan = prefManager.getKecamatan();
        String kota = prefManager.getKota();
        String alamat = prefManager.getAlamat();
        String kodePos = prefManager.getKodePos();
        String noTelp = prefManager.getNoTelp();

        noKtpEt = findViewById(R.id.no_ktp_rincian);
        namaEt = findViewById(R.id.nama_lengkap_rincian);
        agamaEt = findViewById(R.id.agama_rincian);
        titleEt = findViewById(R.id.title_rincian);
        rtEt = findViewById(R.id.rt_rincian);
        rwEt = findViewById(R.id.rw_rincian);
        kelurahanEt = findViewById(R.id.kelurahan_rincian);
        kecamatanEt = findViewById(R.id.kecamatan_rincian);
        kotaEt = findViewById(R.id.kota_rincian);
        alamatEt = findViewById(R.id.alamat_rincian);
        kodePosEt = findViewById(R.id.kode_pos_rincian);
        noTelpEt = findViewById(R.id.no_telp_rincian);

        noKtpEt.setEnabled(false);
        namaEt.setEnabled(false);
        agamaEt.setEnabled(false);
        titleEt.setEnabled(false);
        rtEt.setEnabled(false);
        rwEt.setEnabled(false);
        kelurahanEt.setEnabled(false);
        kecamatanEt.setEnabled(false);
        kotaEt.setEnabled(false);
        alamatEt.setEnabled(false);
        kodePosEt.setEnabled(false);
        noTelpEt.setEnabled(false);

        noKtpEt.setText(noKtp);
        namaEt.setText(nama);
        agamaEt.setText(agama);
        titleEt.setText(title);
        rtEt.setText(rt);
        rwEt.setText(rw);
        kelurahanEt.setText(kelurahan);
        kecamatanEt.setText(kecamatan);
        kotaEt.setText(kota);
        alamatEt.setText(alamat);
        kodePosEt.setText(kodePos);
        noTelpEt.setText(noTelp);


    }
}

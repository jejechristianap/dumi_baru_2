package com.minjem.dumi.akun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.minjem.dumi.R;

public class PusatBantuanActivity extends AppCompatActivity {

    private LinearLayout pilihanSatuLl, pilihanDuaLl, pilihanTigaLl, pilihanEmpatLl, pilihanLimaLl;
    private LinearLayout deskripsiSatuLl, deskripsiDuaLl, deskripsiTigaLl, deskripsiEmpatLl, deskripsiLimaLl;
    private ImageView arrow1, arrow2, arrow3, arrow4, arrow5, backBantuanIv;

    private boolean satu, dua, tiga, empat, lima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pusat_bantuan);

        backBantuanIv = findViewById(R.id.back_bantuan);

        /*Iinit Pilihan*/
        pilihanSatuLl = findViewById(R.id.pilihan_satu_ll);
        pilihanDuaLl = findViewById(R.id.pilihan_dua_ll);
        pilihanTigaLl = findViewById(R.id.pilihan_tiga_ll);
        pilihanEmpatLl = findViewById(R.id.pilihan_empat_ll);
        pilihanLimaLl = findViewById(R.id.pilihan_lima_ll);

        /*Init Deskripsi Pilihan*/
        deskripsiSatuLl = findViewById(R.id.deskripsi_satu_ll);
        deskripsiDuaLl = findViewById(R.id.deskripsi_dua_ll);
        deskripsiTigaLl = findViewById(R.id.deskripsi_tiga_ll);
        deskripsiEmpatLl = findViewById(R.id.deskripsi_empat_ll);
        deskripsiLimaLl = findViewById(R.id.deskripsi_lima_ll);

        /*Init Arrow*/
        arrow1 = findViewById(R.id.arrow1);
        arrow2 = findViewById(R.id.arrow2);
        arrow3 = findViewById(R.id.arrow3);
        arrow4 = findViewById(R.id.arrow4);
        arrow5 = findViewById(R.id.arrow5);

        satu = false;
        dua = false;
        tiga = false;
        empat = false;
        lima = false;

        /*LinearLayout onClick*/
        pilihanSatuLl.setOnClickListener(v ->{
            if(!satu){
                deskripsiSatuLl.setVisibility(View.VISIBLE);
                arrow1.setImageResource(R.drawable.ic_keyboard_arrow_up);
                satu = true;
            } else {
                deskripsiSatuLl.setVisibility(View.GONE);
                arrow1.setImageResource(R.drawable.ic_arrow_down24dp);
                satu = false;
            }
        });

        pilihanDuaLl.setOnClickListener(v -> {
            if(!dua){
                deskripsiDuaLl.setVisibility(View.VISIBLE);
                arrow2.setImageResource(R.drawable.ic_keyboard_arrow_up);
                dua = true;
            } else {
                deskripsiDuaLl.setVisibility(View.GONE);
                arrow2.setImageResource(R.drawable.ic_arrow_down24dp);
                dua = false;
            }
        });

        pilihanTigaLl.setOnClickListener(v -> {
            if(!tiga){
                deskripsiTigaLl.setVisibility(View.VISIBLE);
                arrow3.setImageResource(R.drawable.ic_keyboard_arrow_up);
                tiga = true;
            } else {
                deskripsiTigaLl.setVisibility(View.GONE);
                arrow3.setImageResource(R.drawable.ic_arrow_down24dp);
                tiga = false;
            }
        });

        pilihanEmpatLl.setOnClickListener(v -> {
            if(!empat){
                deskripsiEmpatLl.setVisibility(View.VISIBLE);
                arrow4.setImageResource(R.drawable.ic_keyboard_arrow_up);
                empat = true;
            } else {
                deskripsiEmpatLl.setVisibility(View.GONE);
                arrow4.setImageResource(R.drawable.ic_arrow_down24dp);
                empat = false;
            }
        });

        pilihanLimaLl.setOnClickListener(v -> {
            if(!lima){
                deskripsiLimaLl.setVisibility(View.VISIBLE);
                arrow5.setImageResource(R.drawable.ic_keyboard_arrow_up);
                lima = true;
            } else {
                deskripsiLimaLl.setVisibility(View.GONE);
                arrow5.setImageResource(R.drawable.ic_arrow_down24dp);
                lima = false;
            }
        });

        backBantuanIv.setOnClickListener(v -> finish());

    }
}

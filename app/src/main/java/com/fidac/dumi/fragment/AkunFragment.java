package com.fidac.dumi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fidac.dumi.HalamanDepanActivity;
import com.fidac.dumi.R;
import com.fidac.dumi.akun.DisclaimerActivity;
import com.fidac.dumi.akun.KebijakanPrivasiActivity;
import com.fidac.dumi.akun.PusatBantuanActivity;
import com.fidac.dumi.akun.RincianAkunActivity;


public class AkunFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_akun, container, false);

        LinearLayout rincianAkunLl = view.findViewById(R.id.rincian_akun_ll);
        LinearLayout pusatBantuanLl = view.findViewById(R.id.pusat_bantua_ll);
        LinearLayout disclaimerLl = view.findViewById(R.id.disclaimer_ll);
        LinearLayout kebijakanPrivasiLl = view.findViewById(R.id.kebijakan_privasi_ll);
        Button keluarButton = view.findViewById(R.id.keluar_button);

        /*Rincian Akun*/
        rincianAkunLl.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RincianAkunActivity.class));
        });

        /*Kebijakan privasi*/
        kebijakanPrivasiLl.setOnClickListener(v -> startActivity(new Intent(getActivity(), KebijakanPrivasiActivity.class)));

        /*Pusat Bantuan*/
        pusatBantuanLl.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PusatBantuanActivity.class));
        });

        /*Disclaimer*/
        disclaimerLl.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DisclaimerActivity.class));
        });

        /*Logout Button*/
        keluarButton.setOnClickListener(v -> {
            getActivity().finish();
            startActivity(new Intent(getActivity(), HalamanDepanActivity.class));
        });

        return view;
    }
}

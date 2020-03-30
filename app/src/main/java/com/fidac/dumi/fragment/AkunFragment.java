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
import com.fidac.dumi.akun.RincianAkunActivity;


public class AkunFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_akun, container, false);

        LinearLayout rincianAkunLl = view.findViewById(R.id.rincian_akun_ll);
        Button keluarButton = view.findViewById(R.id.keluar_button);

        rincianAkunLl.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RincianAkunActivity.class));
        });

        keluarButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), HalamanDepanActivity.class));
        });

        return view;
    }
}

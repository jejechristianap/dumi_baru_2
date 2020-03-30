package com.fidac.dumi.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fidac.dumi.LihatSemuaActivity;
import com.fidac.dumi.R;
import com.fidac.dumi.jenispinjaman.PinjamanKilatActivity;
import com.fidac.dumi.jenispinjaman.PinjamanRegularActivity;
import com.fidac.dumi.model.SharedPrefManager;
import com.fidac.dumi.model.User;


public class BerandaFragment extends Fragment {

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

//        final LinearLayout topLayout = view.findViewById(R.id.top_background);
        TextView lihatSemuaTV = view.findViewById(R.id.lihat_semua);
        Button dumiKilatButton = view.findViewById(R.id.dumi_kilat_button);
        Button dumiRegularButton = view.findViewById(R.id.dumi_regular_button);
        Button dumiPensiunButton = view.findViewById(R.id.dumi_pensiun_button);
        Button dumiBumnButton = view.findViewById(R.id.dumi_bumn_button);

        /*TextView userIdTv = view.findViewById(R.id.user_id);
        TextView userNipTv = view.findViewById(R.id.user_nip);
        TextView userEmailTv = view.findViewById(R.id.user_email);
        TextView userNama = view.findViewById(R.id.user_nama);*/

        Toast toastPensiun= Toast.makeText(getActivity(),
                "Mohon maaf ini pinjaman ini khusus untuk pengguna Pensiun", Toast.LENGTH_SHORT);
        toastPensiun.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        View toastV = toastPensiun.getView();
        TextView text = toastV.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        toastV.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_IN);

        Toast toastBumn= Toast.makeText(getActivity(),
                "Mohon maaf ini pinjaman ini khusus untuk pengguna BUMN", Toast.LENGTH_SHORT);
        toastBumn.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        View vB = toastBumn.getView();
        TextView text2 = vB.findViewById(android.R.id.message);
        text2.setTextColor(Color.WHITE);
        vB.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);



        lihatSemuaTV.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LihatSemuaActivity.class);
            startActivity(intent);
        });


        dumiKilatButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), PinjamanKilatActivity.class)));

        dumiRegularButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), PinjamanRegularActivity.class)));

        dumiPensiunButton.setOnClickListener(v -> {
            toastPensiun.show();
        });

        dumiBumnButton.setOnClickListener(v -> {
            toastBumn.show();
        });

        return view;
    }

}

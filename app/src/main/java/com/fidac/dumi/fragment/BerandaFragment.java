package com.fidac.dumi.fragment;

import android.content.Intent;
import android.os.Bundle;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

//        final LinearLayout topLayout = view.findViewById(R.id.top_background);
        final TextView lihatSemuaTV = view.findViewById(R.id.lihat_semua);
        final Button dumiKilatButton = view.findViewById(R.id.dumi_kilat_button);
        final Button dumiRegularButton = view.findViewById(R.id.dumi_regular_button);

        TextView userIdTv = view.findViewById(R.id.user_id);
        TextView userNipTv = view.findViewById(R.id.user_nip);
        TextView userEmailTv = view.findViewById(R.id.user_email);
        TextView userNama = view.findViewById(R.id.user_nama);

        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        userIdTv.setText(user.getId());
        userNipTv.setText(user.getNip());
        userEmailTv.setText(user.getEmail());
        userNama.setText(user.getNama());


        lihatSemuaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LihatSemuaActivity.class);
                startActivity(intent);
            }
        });


        dumiKilatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PinjamanKilatActivity.class));
            }
        });

        dumiRegularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PinjamanRegularActivity.class));
            }
        });

        return view;
    }

}

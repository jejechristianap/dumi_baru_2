package com.minjem.dumi.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.minjem.dumi.HalamanDepanActivity;
import com.minjem.dumi.R;
import com.minjem.dumi.akun.DisclaimerActivity;
import com.minjem.dumi.akun.KebijakanPrivasiActivity;
import com.minjem.dumi.akun.PusatBantuanActivity;
import com.minjem.dumi.akun.RincianAkunActivity;
import com.minjem.dumi.akun.UbahSandiActivity;
import com.minjem.dumi.model.SharedPrefManager;
import com.minjem.dumi.model.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class AkunFragment extends Fragment {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String photoPath, apiPhotoPath;
    private ImageView photoIv;
    private User prefManager;
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_akun, container, false);

        LinearLayout rincianAkunLl = view.findViewById(R.id.rincian_akun_ll);
        LinearLayout ubahSandiLl = view.findViewById(R.id.ubah_sandi_ll);
        LinearLayout pusatBantuanLl = view.findViewById(R.id.pusat_bantua_ll);
        LinearLayout disclaimerLl = view.findViewById(R.id.disclaimer_ll);
        LinearLayout kebijakanPrivasiLl = view.findViewById(R.id.kebijakan_privasi_ll);
        Button keluarButton = view.findViewById(R.id.keluar_button);
        photoIv = view.findViewById(R.id.photo_profile);

        photoIv.setOnClickListener(v -> {
//            photoIv.setScaleType(ImageView.ScaleType.FIT_XY);

        });

        /*Rincian Akun*/
        rincianAkunLl.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RincianAkunActivity.class));
        });

        ubahSandiLl.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UbahSandiActivity.class));
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

    @Override
    public void onStart() {
        super.onStart();
        prefManager = SharedPrefManager.getInstance(Objects.requireNonNull(getActivity()).getApplicationContext()).getUser();
        pref = Objects.requireNonNull(getActivity()).getApplicationContext().getSharedPreferences("Profile", 0); // 0 - for private mode
        photoPath = pref.getString("image_profile", null);
        apiPhotoPath = prefManager.getImageProfile();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
        ImageLoader imageLoader = ImageLoader.getInstance();

        Glide.with(this)
                .load(apiPhotoPath)
                .error(R.drawable.ic_profil)
                .transform(new RoundedCornersTransformation(30, 10))
                .into(photoIv);

        /*if (photoPath != null){
            photoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            photoIv.setImageURI(Uri.parse(photoPath));
        } else if(apiPhotoPath != null){
            photoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageLoader.displayImage(apiPhotoPath, photoIv);
        }
        ImageLoader.getInstance().destroy();*/
    }
}

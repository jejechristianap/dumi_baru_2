package com.minjem.dumi.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.minjem.dumi.LihatSemuaActivity;
import com.minjem.dumi.R;
import com.minjem.dumi.ecommerce.CallbackListener;
import com.minjem.dumi.ecommerce.ECommerceActivity;
import com.minjem.dumi.ecommerce.api.BaseApiService;
import com.minjem.dumi.ecommerce.api.HttpRetrofitClient;
import com.minjem.dumi.ecommerce.transaction.RiwayatView;
import com.minjem.dumi.jenispinjaman.PinjamanKilatActivity;
import com.minjem.dumi.jenispinjaman.PinjamanRegularActivity;
import com.minjem.dumi.model.SharedPrefManager;
import com.minjem.dumi.retrofit.RetrofitClient;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.minjem.dumi.ecommerce.Helper.HelperKt.PASSWORD;
import static com.minjem.dumi.ecommerce.Helper.HelperKt.USERNAME;

public class BerandaFragment extends Fragment {
    private int saldoUser = 0;
    private TextView saldoTv, pulsaTv, plnTv, gopayTv, ovoTv, hotelTv, pesawatTv, keretaTv, semuaTv;
    private Locale localID;
    private NumberFormat formatRp;
    private ImageView kilatIv, regularIv, pulsaIv, plnIv, gopayIv, ovoIv, hotelIv, pesawatIv, keretaIv, semuaIv;
    private Button kilatB, regularB, tkb, tkbt,riwayatButton;
    private boolean touch = false;
    private View view;
    private String go;


    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_beranda, container, false);

        initView();
        initOnTouch();

        return view;
    }


    private void initView(){
        kilatIv = view.findViewById(R.id.cardKilat);
        regularIv = view.findViewById(R.id.cardRegular);
        kilatB = view.findViewById(R.id.kilatButton);
        regularB = view.findViewById(R.id.regularButton);
        tkb = view.findViewById(R.id.tkb);
        tkbt = view.findViewById(R.id.tkbText);
        saldoTv = view.findViewById(R.id.saldoPayLater);
        riwayatButton = view.findViewById(R.id.riwayatButton);
        pulsaIv = view.findViewById(R.id.icPulsa);
        pulsaTv = view.findViewById(R.id.textPulsa);
        plnIv = view.findViewById(R.id.icPln);
        plnTv = view.findViewById(R.id.textPln);
        gopayIv = view.findViewById(R.id.icGopay);
        gopayTv = view.findViewById(R.id.textGopay);
        ovoIv = view.findViewById(R.id.icOvo);
        ovoTv = view.findViewById(R.id.textOvo);
        hotelIv = view.findViewById(R.id.icHotel);
        hotelTv = view.findViewById(R.id.textHotel);
        pesawatIv = view.findViewById(R.id.icPesawat);
        pesawatTv = view.findViewById(R.id.textPesawat);
        keretaIv = view.findViewById(R.id.icKereta);
        keretaTv = view.findViewById(R.id.textKereta);
        semuaIv = view.findViewById(R.id.icSemua);
        semuaTv = view.findViewById(R.id.textSemua);

    }

    private void initOnTouch(){
        kilatIv.setOnClickListener(v -> goTo("kilat"));
        kilatB.setOnClickListener(v -> goTo("kilat"));
        regularIv.setOnClickListener(v -> goTo("regular"));
        regularB.setOnClickListener(v -> goTo("regular"));
        riwayatButton.setOnClickListener(v -> goTo("riwayat"));
        pulsaIv.setOnClickListener(v -> goTo("pulsa"));
        pulsaTv.setOnClickListener(v -> goTo("pulsa"));
        plnIv.setOnClickListener(v -> goTo("pln"));
        plnTv.setOnClickListener(v -> goTo("pln"));
        gopayIv.setOnClickListener(v -> goTo("na"));
        gopayTv.setOnClickListener(v -> goTo("na"));
        ovoIv.setOnClickListener(v -> goTo("na"));
        ovoTv.setOnClickListener(v -> goTo("na"));
        hotelIv.setOnClickListener(v -> goTo("na"));
        hotelTv.setOnClickListener(v -> goTo("na"));
        pesawatIv.setOnClickListener(v -> goTo("na"));
        pesawatTv.setOnClickListener(v -> goTo("na"));
        keretaIv.setOnClickListener(v -> goTo("na"));
        keretaTv.setOnClickListener(v -> goTo("na"));
        semuaIv.setOnClickListener(v -> goTo("semua"));
        semuaTv.setOnClickListener(v -> goTo("semua"));

        tkb.setOnClickListener(v -> {
            if (touch){
                tkbt.setVisibility(View.GONE);
                touch = false;
            } else {
                tkbt.setVisibility(View.VISIBLE);
                touch = true;
            }
        });
    }

    private void goTo(String item){
        Intent intent;
        switch (item){
            case "kilat":
                startActivity(new Intent(getActivity(), PinjamanKilatActivity.class));
                break;
            case "regular":
                startActivity(new Intent(getActivity(), PinjamanRegularActivity.class));
                break;
            case "riwayat":
                startActivity(new Intent(getActivity(), RiwayatView.class));
                break;
            case "pulsa":
                intent = new Intent(getActivity(), ECommerceActivity.class);
                intent.putExtra("fragment", "pulsa");
                startActivity(intent);
                break;
            case "pln":
                intent = new Intent(getActivity(), ECommerceActivity.class);
                intent.putExtra("fragment", "pln");
                startActivity(intent);
                break;
            case "semua":
                startActivity(new Intent(getActivity(), LihatSemuaActivity.class));
                break;
            default:
                Toast.makeText(getActivity(), "Tunggu update kami selanjutanya...", Toast.LENGTH_SHORT).show();
                break;

        }
    }
/*
    ImageListener imageListener = (position, imageView) -> {
        imageView.setImageResource(sampleImages[position]);
        imageView.setOnClickListener(v -> {
            Log.d("Position", "Pos: " + position);
        });
    };*/

    @Override
    public void onStart() {
        super.onStart();
        getSaldo();
    }

    @Override
    public void onResume() {
        super.onResume();
        getSaldo();
    }

    private void getSaldo(){
        int idUser = SharedPrefManager.getInstance(getActivity()).getUser().getId();
        String nipBaru = SharedPrefManager.getInstance(getActivity()).getUser().getNip();
        BaseApiService api = RetrofitClient.getClient().create(BaseApiService.class);
        Call<ResponseBody> call = api.getSaldo(idUser, nipBaru, USERNAME, PASSWORD);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("status")){
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            for (int i = 0; i<jsonArray.length(); i++){
                                saldoUser = jsonArray.getJSONObject(i).optInt("saldo", 0);
                            }
                            Log.d("SaldoUser", "Saldo: " + saldoUser);
                            localID = new Locale("in", "ID");
                            formatRp = NumberFormat.getCurrencyInstance(localID);
                            saldoTv.setText(formatRp.format(saldoUser));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}

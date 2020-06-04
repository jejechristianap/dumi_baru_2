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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private TextView saldoTv;
    Locale localID;
    NumberFormat formatRp;

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

//        final LinearLayout topLayout = view.findViewById(R.id.top_background);
        TextView lihatSemuaTV = view.findViewById(R.id.lihat_semua);
        saldoTv = view.findViewById(R.id.saldoPayLater);
        Button dumiKilatButton = view.findViewById(R.id.dumi_kilat_button);
        Button dumiRegularButton = view.findViewById(R.id.dumi_regular_button);
        Button dumiPensiunButton = view.findViewById(R.id.dumi_pensiun_button);
        Button dumiBumnButton = view.findViewById(R.id.dumi_bumn_button);
        LinearLayout isiPulsaLl = view.findViewById(R.id.isi_pulsa_ll);
        LinearLayout plnTokenLl = view.findViewById(R.id.token_ll);
        LinearLayout riwayatLl = view.findViewById(R.id.riwayatLl);

        riwayatLl.setOnClickListener(v -> {
            startActivity(new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), RiwayatView.class));
        });




        Toast toastPensiun  = Toast.makeText(getActivity(),
                "Mohon maaf ini pinjaman ini khusus untuk pengguna Pensiun", Toast.LENGTH_SHORT);
        toastPensiun.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        View toastV = toastPensiun.getView();
        TextView text = toastV.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        toastV.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);

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

        /*E-Commerce*/
        isiPulsaLl.setOnClickListener(v -> {
//            startActivity(new Intent(getActivity(), IsiPulsaActivity.class));
//            Toast.makeText(getActivity(), "Tunggu update kami selanjutnya", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), ECommerceActivity.class);
            intent.putExtra("fragment", "pulsa");
            startActivity(intent);
        });

        plnTokenLl.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ECommerceActivity.class);
            intent.putExtra("fragment", "pln");
            startActivity(intent);
        });
        dumiKilatButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), PinjamanKilatActivity.class)));
        dumiRegularButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), PinjamanRegularActivity.class)));
        dumiPensiunButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), PinjamanKilatActivity.class)));
        dumiBumnButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), PinjamanRegularActivity.class)));

        return view;
    }

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
                                saldoUser = jsonArray.getJSONObject(i).getInt("saldo");
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

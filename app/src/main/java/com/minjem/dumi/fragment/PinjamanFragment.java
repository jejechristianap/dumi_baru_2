package com.minjem.dumi.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.minjem.dumi.PerjanjianKreditView;
import com.minjem.dumi.R;
import com.minjem.dumi.api.StatusPinjamanInterface;
import com.minjem.dumi.model.SharedPrefManager;
import com.minjem.dumi.model.User;
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


public class PinjamanFragment extends Fragment {
    private TextView statusPinjamanTv, pinjamanTv, tenorPinjamanTv, bungaTv, angsuranPerbulanTv, asuransiTv,
                        adminTv, transferBankTv, jumlahTerimaTv, tglPengajuanTv, tvGagal;
    private User prefManager;
    private Locale localID;
    private NumberFormat formatRp;
    private Button pkButton;
    private String tglPengajuan = "";
    private String tujuan = "";
    private double bungaRupiah = 0.0;
    private double angsuran = 0.0;
    private Context mContext;
    private ScrollView svTagihan;
    private RelativeLayout rlGagal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pinjaman, container, false);
        mContext = Objects.requireNonNull(getActivity()).getApplicationContext();

        prefManager = SharedPrefManager.getInstance(mContext).getUser();
        localID = new Locale("in", "ID");
        formatRp = NumberFormat.getCurrencyInstance(localID);

        statusPinjamanTv = view.findViewById(R.id.status_pinjaman);
        pinjamanTv = view.findViewById(R.id.pinjaman);
        tenorPinjamanTv = view.findViewById(R.id.tenor_pinjaman_bulan);
        bungaTv = view.findViewById(R.id.bunga_pinjaman);
        angsuranPerbulanTv = view.findViewById(R.id.angsuran_pinjaman);
        asuransiTv = view.findViewById(R.id.asuransi_pinjaman);
        adminTv = view.findViewById(R.id.administrasi_pinjaman);
        transferBankTv = view.findViewById(R.id.transfer_bank_pinjaman);
        jumlahTerimaTv = view.findViewById(R.id.jumlah_terima_pinjaman);
        tglPengajuanTv = view.findViewById(R.id.tanggal_pengajuan_pinjaman);
        svTagihan = view.findViewById(R.id.svTagihan);
        rlGagal = view.findViewById(R.id.rlGagalTagihan);
        tvGagal = view.findViewById(R.id.tvGagalTagihan);

        pkButton = view.findViewById(R.id.pkButton);
        pkButton.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), PerjanjianKreditView.class);
            i.putExtra("tanggal", tglPengajuan);
            i.putExtra("pinjaman", pinjamanTv.getText().toString());
            i.putExtra("bunga", bungaRupiah);
            i.putExtra("angsuran", angsuran);
            i.putExtra("tujuan", tujuan);
            i.putExtra("tenor", tenorPinjamanTv.getText().toString());
            startActivity(i);
        });

        getPinjaman();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPinjaman();
    }

    private void getPinjaman(){
        String nip = prefManager.getNip();
        StatusPinjamanInterface status = RetrofitClient.getClient().create(StatusPinjamanInterface.class);
        Call<ResponseBody> call = status.getPinjaman(nip);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        boolean cek = obj.getBoolean("status");
                        if (cek){
                            String data = obj.getString("data");
                            JSONArray jsonArray = new JSONArray(data);
                            if (jsonArray.length() == 0){
                                rlGagal.setVisibility(View.VISIBLE);
                                svTagihan.setVisibility(View.GONE);
                                tvGagal.setText("Mohon maaf, belum ada tagihan.");
                            } else {
                                svTagihan.setVisibility(View.VISIBLE);
                                rlGagal.setVisibility(View.GONE);
                                for (int i = 0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int statusId = jsonObject.getInt("status");
                                    double pinjaman = jsonObject.getDouble("pinjaman");
                                    String lamaPinjaman = jsonObject.getString("lamaPinjaman");
                                    bungaRupiah = jsonObject.getDouble("bungaRupiah");
                                    angsuran = jsonObject.getDouble("angsuranPerbulan");
                                    double asuransi = jsonObject.getDouble("asuransiRupiah");
                                    double adminRupiah = jsonObject.getDouble("administrasiRupiah");
                                    double diterima = jsonObject.getDouble("diterimaRupiah");
                                    tglPengajuan = jsonObject.getString("tglPengajuan");
                                    tujuan = jsonObject.getString("tujuanPinjaman");
                                    if(statusId == 1){
                                        statusPinjamanTv.setText("Pengajuan");
                                        pkButton.setVisibility(View.GONE);
                                    } else if (statusId == 2){
                                        statusPinjamanTv.setText("Disetujui");
                                        pkButton.setVisibility(View.VISIBLE);
                                    } else if (statusId == 3){
                                        statusPinjamanTv.setText("Pengajuan ditolak");
                                        pkButton.setVisibility(View.GONE);
                                    } else if (statusId == 4){
                                        statusPinjamanTv.setText("Telah ditransfer");
                                        pkButton.setVisibility(View.GONE);
                                    } else if (statusId == 5){
                                        statusPinjamanTv.setText("Kredit berjalan");
                                        pkButton.setVisibility(View.GONE);
                                    } else if(statusId == 6){
                                        statusPinjamanTv.setText("Kredit Lunas");
                                        pkButton.setVisibility(View.GONE);
                                    } else {
                                        statusPinjamanTv.setText("!!Dalam Proses Pengembangan!!");
                                        pkButton.setVisibility(View.GONE);
                                    }
                                    lamaPinjaman += " Bulan";
                                    pinjamanTv.setText(formatRp.format(pinjaman));
                                    tenorPinjamanTv.setText(lamaPinjaman);
                                    bungaTv.setText(formatRp.format(bungaRupiah));
                                    angsuranPerbulanTv.setText(formatRp.format(angsuran));
                                    asuransiTv.setText(formatRp.format(asuransi));
                                    adminTv.setText(formatRp.format(adminRupiah));
                                    jumlahTerimaTv.setText(formatRp.format(diterima));
                                    transferBankTv.setText(formatRp.format(6500));
                                    tglPengajuanTv.setText(tglPengajuan);
                                }
                            }

                        }
                    } catch (JSONException | IOException e) {
                        rlGagal.setVisibility(View.VISIBLE);
                        svTagihan.setVisibility(View.GONE);
                        tvGagal.setText("Mohon maaf, koneksi gagal.");
                        e.printStackTrace();
                    }
                } else {
                    rlGagal.setVisibility(View.VISIBLE);
                    svTagihan.setVisibility(View.GONE);
                    tvGagal.setText("Mohon maaf koneksi gagal, silahkan cek koneksi anda");
                    Toast.makeText(mContext, "Mohon maaf server tidak terjangkau", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                rlGagal.setVisibility(View.VISIBLE);
                svTagihan.setVisibility(View.GONE);
                tvGagal.setText("Mohon maaf koneksi gagal, silahkan cek koneksi anda");
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

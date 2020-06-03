package com.minjem.dumi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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


public class MitraFragment extends Fragment {
    private TextView statusPinjamanTv, pinjamanTv, tenorPinjamanTv, bungaTv, angsuranPerbulanTv, asuransiTv,
                        adminTv, transferBankTv, jumlahTerimaTv, tglPengajuanTv;
    private User prefManager;
    private Locale localID;
    private NumberFormat formatRp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pinjaman, container, false);

        prefManager = SharedPrefManager.getInstance(Objects.requireNonNull(getActivity()).getApplicationContext()).getUser();
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
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    boolean cek = obj.getBoolean("status");
                    if (cek){
                        String data = obj.getString("data");
                        JSONArray jsonArray = new JSONArray(data);
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int statusId = jsonObject.getInt("status");
                            double pinjaman = jsonObject.getDouble("pinjaman");
                            String lamaPinjaman = jsonObject.getString("lamaPinjaman");
                            double bungaRupiah = jsonObject.getDouble("bungaRupiah");
                            double angsuran = jsonObject.getDouble("angsuranPerbulan");
                            double asuransi = jsonObject.getDouble("asuransiRupiah");
                            double adminRupiah = jsonObject.getDouble("administrasiRupiah");
                            double diterima = jsonObject.getDouble("diterimaRupiah");
                            String tglPengajuan = jsonObject.getString("tglPengajuan");
                            if(statusId == 1){
                                statusPinjamanTv.setText("Pengajuan");
                            } else if (statusId == 2){
                                statusPinjamanTv.setText("Disetujui");
                            } else if (statusId == 3){
                                statusPinjamanTv.setText("Pengajuan ditolak");
                            } else if (statusId == 4){
                                statusPinjamanTv.setText("Telah ditransfer");
                            } else if (statusId == 5){
                                statusPinjamanTv.setText("Kredit berjalan");
                            } else if(statusId == 6){
                                statusPinjamanTv.setText("Kredit Lunas");
                            } else {
                                statusPinjamanTv.setText("!!Dalam Proses Pengembangan!!");
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
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
}

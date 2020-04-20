/*
package com.fidac.dumi.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fidac.dumi.R;
import com.fidac.dumi.model.NotifikasiAdapterOld;
import com.fidac.dumi.model.NotifikasiDataOld;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class InboxFragmentOld extends Fragment {
    private LinearLayoutManager layoutManager;
    private List<NotifikasiDataOld> notifikasiData = null;
    private List<NotifikasiDataOld> data;

    NotifikasiAdapterOld notifikasiAdapter;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        recyclerView = view.findViewById(R.id.notifikasi_rv);
        */
/*layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        notifikasiAdapter = new NotifikasiAdapter(getActivity(), data);
        recyclerView.setAdapter(notifikasiAdapter);*//*


//        getNotif();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        data = new ArrayList<>();
//        getNotif();
    }

    */
/*private void getNotif(){
        StatusPinjamanInterface cek = RetrofitClient.getClient().create(StatusPinjamanInterface.class);

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading Data...");
        pDialog.show();
        User prefManager = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();
        String nip = prefManager.getNip();
//        Toast.makeText(getActivity(), nip, Toast.LENGTH_SHORT).show();

        Call<NotifikasiData> call = cek.getNotif(nip);
        call.enqueue(new Callback<NotifikasiData>() {
            @Override
            public void onResponse(Call<NotifikasiData> call, Response<NotifikasiData> response) {


                    notifikasiData = (List<NotifikasiData>) response.body();
                    layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    notifikasiAdapter = new NotifikasiAdapter(getActivity(), notifikasiData);
                    recyclerView.setAdapter(notifikasiAdapter);
                    *//*
*/
/*Toast.makeText(getContext(), dataS, Toast.LENGTH_SHORT).show();
                    JSONObject obj = new JSONObject(response.body().string());
                    boolean status = obj.getBoolean("status");*//*
*/
/*
                    *//*
*/
/*if (status){
                        String dataS = obj.getString("data");
                        JSONArray jsonArray = new JSONArray(dataS);
                        int len = jsonArray.length();

                        *//*
*/
/**//*
*/
/*for (int i = 0; i<len; i++){
                            JSONObject json = jsonArray.getJSONObject(i);
//                            data.add(jsonArray.get(i).toString());
                            int id = json.getInt("id");
                            String token = json.getString("token");
                            String judul = json.getString("judul");
                            String isi = json.getString("isi");
                            int statusI = json.getInt("status");
                            String waktu = json.getString("waktu");
                            String idNasabah = json.getString("id_nasabah");

                            notifikasiData
//                            data.add(i);

                            *//*
*/
/**//*
*/
/**//*
*/
/**//*
*/
/*data.add(new NotifikasiData(json.getInt("id"),
                                    json.getString("token"),
                                    json.getString("judul"),
                                    json.getString("isi"),
                                    json.getInt("status"),
                                    json.getString("waktu"),
                                    json.getString("id_nasabah")
                                    ));*//*
*/
/**//*
*/
/**//*
*/
/**//*
*/
/*
                        }*//*
*/
/**//*
*/
/*


//                        data =  dataS;


                    }*//*
*/
/*
                    pDialog.dismiss();
                }*//*
*/
/* catch (JSONException | IOException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                }*//*
*/
/*

            @Override
            public void onFailure(Call<NotifikasiData> call, Throwable t) {
                Log.d("Fail", "onFailure: " + t.getMessage());
                pDialog.dismiss();
            }
        });
    }*//*


    private void writeRecycler(String response){
        try {
            JSONObject obj = new JSONObject(response);
            if(obj.optString("status").equals(true)){
                ArrayList<NotifikasiDataOld> notifikasiDataList = new ArrayList<>();
                JSONArray jsonArray = obj.getJSONArray("data");
                for (int i = 0; i<jsonArray.length(); i++){
                    NotifikasiDataOld notif = new NotifikasiDataOld();
                    JSONObject notifObj = jsonArray.getJSONObject(i);
                    notif.setId(notifObj.getInt("id"));
                    notif.setToken(notifObj.getString("token"));
                    notif.setJudul(notifObj.getString("judul"));
                    notif.setIsi(notifObj.getString("isi"));
                    notif.setStatus(notifObj.getInt("status"));
                    notif.setWaktu(notifObj.getString("waktu"));
                    notif.setIdNasabah(notifObj.getString("id_nasabah"));
                    notifikasiDataList.add(notif);
                    Log.d("RecyclerView", "writeRecycler: "+notif);
                }
//                Toast.makeText(getActivity(), "Get data", Toast.LENGTH_SHORT).show();
                notifikasiAdapter = new NotifikasiAdapterOld(getActivity(), notifikasiDataList);
                recyclerView.setAdapter(notifikasiAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
*/

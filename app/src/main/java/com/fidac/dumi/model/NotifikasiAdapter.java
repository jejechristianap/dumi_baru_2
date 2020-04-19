package com.fidac.dumi.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.fidac.dumi.R;

import java.util.ArrayList;
import java.util.List;

public class NotifikasiAdapter extends RecyclerView.Adapter<NotifikasiAdapter.NotifHolder>{
    private List<NotifikasiData> notifList = null;
//    private Context context;
    private LayoutInflater inflater;

    public NotifikasiAdapter(Context context, List<NotifikasiData> notifList){
        inflater = LayoutInflater.from(context);
        this.notifList = notifList;
//        this.context = context;
    }


    @NonNull
    @Override
    public NotifHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_notifikasi, parent, false);
//        NotifHolder holder = new NotifHolder(view);

        return new NotifHolder(view);
    }

    @Override
    public void onBindViewHolder(@Nullable NotifHolder holder, int position) {
        if (holder != null) {
            holder.idTv.setText(notifList.get(position).getId());
            holder.token.setText(notifList.get(position).getToken());
            holder.judul.setText(notifList.get(position).getJudul());
            holder.isi.setText(notifList.get(position).getIsi());
            holder.statusTv.setText(notifList.get(position).getStatus());
            holder.waktu.setText(notifList.get(position).getWaktu());
            holder.idNasabah.setText(notifList.get(position).getIdNasabah());
        }

    }

    @Override
    public int getItemCount() {
        return notifList == null ? 0 : notifList.size();
//        notifList == null ? 0 :
    }

    public class NotifHolder extends RecyclerView.ViewHolder {
        TextView idTv, token, judul, isi, statusTv, waktu, idNasabah;

        public NotifHolder(View itemView) {
            super(itemView);
            Log.i("autolog", "ViewHolder");

            idTv = itemView.findViewById(R.id.id_notif);
            token = itemView.findViewById(R.id.token_notif);
            judul = itemView.findViewById(R.id.judul_notif);
            isi = itemView.findViewById(R.id.isi_notif);
            statusTv = itemView.findViewById(R.id.statusTv_notif);
            waktu = itemView.findViewById(R.id.waktu_notif);
            idNasabah = itemView.findViewById(R.id.id_nasabah_notif);
        }
    }

}

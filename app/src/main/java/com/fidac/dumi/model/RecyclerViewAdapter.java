package com.fidac.dumi.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fidac.dumi.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<DaftarHargaPulsa> daftarHargaPulsa;
    private Context context;

    public RecyclerViewAdapter(Context context, List<DaftarHargaPulsa> daftarHargaPulsa ) {
        Log.i("autolog", "RecyclerViewAdapter");
        this.daftarHargaPulsa = daftarHargaPulsa;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("autolog", "onCreateViewHolder");
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_daftarharga, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i("autolog", "onBindViewHolder");
        holder.tipe.setText(daftarHargaPulsa.get(position).getTipe());
        holder.operator.setText(daftarHargaPulsa.get(position).getOperator());
        holder.kodeoperator.setText(daftarHargaPulsa.get(position).getKodeoperator());
        holder.keterangan.setText(daftarHargaPulsa.get(position).getKeterangan());
        holder.nominal.setText(daftarHargaPulsa.get(position).getNominal());
        holder.harga.setText(daftarHargaPulsa.get(position).getHarga());
        holder.status.setText(daftarHargaPulsa.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        Log.i("autolog", "getItemCount");
        return daftarHargaPulsa.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tipe, operator, kodeoperator, keterangan, nominal, harga, status;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.i("autolog", "ViewHolder");

            tipe = itemView.findViewById(R.id.tipe);
            operator = itemView.findViewById(R.id.operator);
            kodeoperator = itemView.findViewById(R.id.kodeoperator);
            keterangan = itemView.findViewById(R.id.keterangan);
            nominal = itemView.findViewById(R.id.nominal);
            harga = itemView.findViewById(R.id.harga);
            status = itemView.findViewById(R.id.status);


        }
    }
}

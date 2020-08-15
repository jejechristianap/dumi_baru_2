package com.minjem.dumi.ecommerce.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.response.RiwayatPPOBData
import kotlinx.android.synthetic.main.recycler_view_riwayat.view.*
import java.text.DecimalFormat

class RiwayatECommAdapter(private var mContext: Context, internal var list: List<RiwayatPPOBData>)
    : RecyclerView.Adapter<RiwayatECommAdapter.RiwayatHolder>() {

    inner class RiwayatHolder(view: View) : RecyclerView.ViewHolder(view){
        @SuppressLint("SetTextI18n")
        fun data(item: RiwayatPPOBData){
            val rpFromat = DecimalFormat("#,###")
            val tipe = item.tipe
            if (tipe == "PLN"){
                itemView.iconRiwayatIv.setImageResource(R.drawable.pln_logo)
                itemView.tipeTv.text = "Token"
                itemView.hargaTv.text = "Rp${rpFromat.format(item.ppob_totalbayar!!.toInt())}"
                itemView.waktuTv.text = item.created_at
                itemView.noTokenTv.text = item.ppob_stroomtoken
            } else if(tipe == "PULSA"){
                itemView.iconRiwayatIv.setImageResource(R.drawable.ic_pulsa)
                itemView.tipeTv.text = item.tipe
                itemView.hargaTv.text = "Rp${rpFromat.format(item.harga!!.toInt())}"
                itemView.waktuTv.text = item.created_at
                itemView.noTokenTv.text = item.no_tujuan
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatHolder {
        val riwayatView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_riwayat, parent, false)

        return RiwayatHolder(riwayatView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RiwayatHolder, position: Int) {
        holder.data(list[position])
    }

    fun filter (new: MutableList<RiwayatPPOBData>){
        list = new.sortedByDescending { it.created_at.toString() }
        notifyDataSetChanged()
    }

}
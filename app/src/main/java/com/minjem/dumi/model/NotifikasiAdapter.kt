package com.minjem.dumi.model


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.minjem.dumi.R
import kotlinx.android.synthetic.main.recycler_view_notifikasi.view.*

class NotifikasiAdapter (internal var mContext : Context,
                         internal var list : MutableList<NotifikasiData>) :
        RecyclerView.Adapter<NotifikasiAdapter.HolderList>(){
    inner class HolderList (view : View) : RecyclerView.ViewHolder(view) {
        fun Data(item: NotifikasiData, position: Int) {
            itemView.judul_notif.text = item.judul
            itemView.isi_notif.text = item.isi
            itemView.waktu_notif.text = item.waktu
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifikasiAdapter.HolderList {
        val v = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_notifikasi,parent,false)

        return HolderList(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NotifikasiAdapter.HolderList, position: Int) {
        holder.Data(list[position],position)
    }

}
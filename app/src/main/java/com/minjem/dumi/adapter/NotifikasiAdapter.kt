package com.minjem.dumi.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.minjem.dumi.R
import com.minjem.dumi.dataclass.NotifikasiData
import kotlinx.android.synthetic.main.recycler_view_notifikasi.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class NotifikasiAdapter (internal var mContext : Context, internal var list : List<NotifikasiData>) :
        RecyclerView.Adapter<NotifikasiAdapter.HolderList>(){
    private var notif = list
    class HolderList (view : View) : RecyclerView.ViewHolder(view) {
        var headerText: TextView = itemView.tvHeaderNotif
        fun data(item: NotifikasiData) {
            itemView.judul_notif.text = item.judul
            itemView.isi_notif.text = item.isi
            itemView.tvHeaderNotif.text = item.waktu
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderList {
        val v = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_notifikasi,parent,false)

        return HolderList(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HolderList, position: Int) {
        holder.data(list[position])
        val waktu = notif[position].waktu!!
        val local = Locale("in", "ID")
        Log.d("NOTIFIKASI ===================> ", "onBindViewHolder: $waktu")
        val sdf = SimpleDateFormat("MM/dd/yyyy", local)
        val d = sdf.parse(waktu)
        sdf.applyPattern("dd MMMM yyyy")
        val t = sdf.format(d!!)
        try {
            holder.headerText.text = t.substring(0, 9)
            if (position > 0 && notif[position - 1].waktu!!.substring(0, 9) == waktu.substring(0,9)){
                holder.headerText.visibility = View.GONE
            } else {
                holder.headerText.text = sdf.format(d!!)
                holder.headerText.visibility = View.VISIBLE
            }
        } catch (e: Exception){
            Log.d("ERROR", "onBindViewHolder: $e")
        }

    }

    fun filter(new : MutableList<NotifikasiData>){
        list = new.sortedByDescending { it.id.toString() }
        notifyDataSetChanged()
    }

}
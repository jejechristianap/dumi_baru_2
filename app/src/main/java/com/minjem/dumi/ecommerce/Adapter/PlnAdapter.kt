package com.minjem.dumi.ecommerce.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.response.PlnData
import kotlinx.android.synthetic.main.customlist_pulsa.view.*
import java.text.DecimalFormat

class PlnAdapter(internal var mContext : Context, internal var list : List<PlnData>) : RecyclerView.Adapter<PlnAdapter.pln_holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): pln_holder {
        val a = LayoutInflater.from(mContext).inflate(R.layout.customlist_pulsa,parent,false)

        return pln_holder(a)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: pln_holder, position: Int) {
        holder.data(list[position])
    }

    inner class pln_holder (view : View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun data(item: PlnData) {

            val df = DecimalFormat("#,###")
            var fo = df.format(item.ppob_nominal!!.toInt())
            val no = df.format(item.ppob_admin!!.toInt())
            fo += no

            itemView.id_jumlah_pulsa_cp.text = no
            itemView.id_harga_pulsa_cp.text = "Rp$fo"

        }
    }

    fun filter (new : MutableList<PlnData>){

        list = new.sortedBy { it.ppob_nominal!!.toInt() }
        notifyDataSetChanged()
    }
}
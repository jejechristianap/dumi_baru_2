package com.minjem.dumi.ecommerce.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hendi.pulsa.response.G_Pulsa
import com.minjem.dumi.R

import kotlinx.android.synthetic.main.customlist_pulsa.view.*
import java.text.DecimalFormat

class A_pulsa(internal var mContext : Context, internal var list : List<G_Pulsa>) :
        RecyclerView.Adapter<A_pulsa.pulsa_holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): pulsa_holder {
        val a = LayoutInflater.from(mContext).inflate(R.layout.customlist_pulsa,parent,false)

        return pulsa_holder(a)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: pulsa_holder, position: Int) {
        holder.data(list[position])
    }

    inner class pulsa_holder (view : View) : RecyclerView.ViewHolder(view) {
        fun data(item: G_Pulsa) {

            val df = DecimalFormat("#,###")
            val fo = df.format(item.harga!!.toInt())
            val no = df.format(item.nominal!!.toInt())

            itemView.id_jumlah_pulsa_cp.text = no
            itemView.id_harga_pulsa_cp.text = "Rp " + fo

        }
    }

    fun filter (new : MutableList<G_Pulsa>){

        list = new.sortedBy { it.nominal!!.toInt() }
        notifyDataSetChanged()
    }
}
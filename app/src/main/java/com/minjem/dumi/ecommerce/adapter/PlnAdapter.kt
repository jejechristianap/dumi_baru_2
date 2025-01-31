package com.minjem.dumi.ecommerce.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.response.PDAMData
import kotlinx.android.synthetic.main.rv_listharga.view.*
import java.text.DecimalFormat

class PlnAdapter(private var mContext : Context, internal var list : List<PDAMData>)
    : RecyclerView.Adapter<PlnAdapter.pln_holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): pln_holder {
        val a = LayoutInflater.from(mContext).inflate(R.layout.rv_listharga,parent,false)
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
        fun data(item: PDAMData) {
            item.ppob_nominal = item.ppob_nominal?.replace(".", "")
            item.ppob_admin = item.ppob_admin?.replace(".", "")
            val df = DecimalFormat("#,###")
//            val local = Locale("in", "ID")
//            val formatUang = NumberFormat.getCurrencyInstance(local)
            val fo = df.format(item.ppob_nominal!!.toInt())
            val total = item.ppob_nominal!!.toInt() + item.ppob_admin!!.toInt()
            val no = df.format(total)
//            fo += no
            itemView.harga.text = fo
            itemView.totalHarga.text = "Rp$no"
        }
    }

    fun filter (new : MutableList<PDAMData>){
        /*val item = PDAMData()
        item.ppob_nominal = item.ppob_nominal?.replace(".","")*/
        list = new.sortedBy {
            it.ppob_nominal!!.toInt()
        }
        notifyDataSetChanged()
    }
}
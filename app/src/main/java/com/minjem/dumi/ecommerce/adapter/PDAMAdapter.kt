package com.minjem.dumi.ecommerce.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.response.PDAMData
import kotlinx.android.synthetic.main.recycler_view_pdam.view.*

class PDAMAdapter(private var mContext: Context, internal var list: List<PDAMData>)
    : RecyclerView.Adapter<PDAMAdapter.PdamHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdamHolder {
        val a = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_pdam,parent,false)
        return PdamHolder(a)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PdamHolder, position: Int) {
        holder.data(list[position])
    }


    inner class PdamHolder(view: View) : RecyclerView.ViewHolder(view){
        fun data(item: PDAMData){
            val produk = item.ppob_produk?.replace("PDAM ", "")
            val aetra = "PDAM Aetra"
            val palyja = "PDAM PALYJA"
            val jkt = "DKI Jakarta"

            when(item.ppob_kodeproduk){
                "PDAM AETRA/TPJ" -> {
                    itemView.tvProduk.text = item.ppob_kodeproduk
                    itemView.tvKota.text = jkt
                }
                "PDAM PALYJA" -> {
                    itemView.tvProduk.text = item.ppob_kodeproduk
                    itemView.tvKota.text = jkt
                }
                else ->{
                    itemView.tvProduk.text = item.ppob_kodeproduk
                    itemView.tvKota.text = produk
                }
            }



        }
    }

    fun filter(new: MutableList<PDAMData>){
        list = new.sortedBy { it.ppob_produk.toString() }
        notifyDataSetChanged()
    }

}
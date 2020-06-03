package com.minjem.dumi.ecommerce.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.minjem.dumi.R
import com.minjem.dumi.ecommerce.response.BandaraData
import kotlinx.android.synthetic.main.recycler_view_bandara.view.*
import java.util.*
import kotlin.collections.ArrayList

class BandaraKotaAdapter (private var mContext : Context, internal var list : List<BandaraData>)
    : RecyclerView.Adapter<BandaraKotaAdapter.BandaraHolder>() {

    inner class BandaraHolder (view : View) :RecyclerView.ViewHolder(view){
        fun data(item : BandaraData){
            val city = "${item.city.toString()} (${item.code.toString()})"
            itemView.kotaTv.text = city
            itemView.bandaraTv.text = item.airport
        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BandaraHolder {
        val bandaraListView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_bandara, parent, false)

        return BandaraHolder(bandaraListView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BandaraHolder, position: Int) {
        holder.data(list[position])
    }

   fun filter (new : MutableList<BandaraData>){
       list = new.sortedBy {
           it.city.toString()
       }
       notifyDataSetChanged()
   }


}
package com.minjem.dumi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minjem.dumi.R
import com.minjem.dumi.dataclass.PlafondKreditData
import com.minjem.dumi.ecommerce.response.PDAMData
import kotlinx.android.synthetic.main.recycler_view_min_pinjaman_tenor.view.*
import java.text.NumberFormat
import java.util.*

class MinPinTenAdapter (private var c: Context, internal var list: List<PlafondKreditData>)
    : RecyclerView.Adapter<MinPinTenAdapter.MinPinTenHolder>(){

    inner class MinPinTenHolder(view: View): RecyclerView.ViewHolder(view){
        fun data(item: PlafondKreditData){
            val localID = Locale("in", "ID")
            val rp = NumberFormat.getCurrencyInstance(localID)
            var plafond = ""
            var min = ""
            plafond = item.plafond.toString()
            min = rp.format(item.minimal_pinjaman!!)
            itemView.tvJangkaWaktu.text = plafond
            itemView.tvMinimalPinjaman.text = min
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MinPinTenAdapter.MinPinTenHolder {
        val a = LayoutInflater.from(c).inflate(R.layout.recycler_view_min_pinjaman_tenor,parent,false)
        return MinPinTenHolder(a)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MinPinTenAdapter.MinPinTenHolder, position: Int) {
        holder.data(list[position])
    }

    fun filter (new : MutableList<PlafondKreditData>){
        /*val item = PDAMData()
        item.ppob_nominal = item.ppob_nominal?.replace(".","")*/
        list = new.sortedBy {
            it.plafond
        }
        notifyDataSetChanged()
    }

}
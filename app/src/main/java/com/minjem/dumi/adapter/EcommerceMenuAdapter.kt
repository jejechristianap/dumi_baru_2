package com.minjem.dumi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minjem.dumi.R
import com.minjem.dumi.dataclass.DataEcommerce
import kotlinx.android.synthetic.main.gridview_menu.view.*

class EcommerceMenuAdapter (mContext: Context, internal var list : List<DataEcommerce>)
    : RecyclerView.Adapter<EcommerceMenuAdapter.EcomHolder>(){
    val c = mContext
    inner class EcomHolder (view : View) :RecyclerView.ViewHolder(view){

        fun data(item : DataEcommerce){
//            Glide.with(c).load(item.image).into(itemView.ivMenu)
            itemView.ivMenu.setImageResource(item.image!!)
            itemView.tvMenu.text = item.title
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EcommerceMenuAdapter.EcomHolder {
        val eView = LayoutInflater.from(parent.context).inflate(R.layout.gridview_menu, parent, false)
        return EcomHolder(eView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: EcommerceMenuAdapter.EcomHolder, position: Int) {
        holder.data(list[position])
    }

}
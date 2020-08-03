package com.minjem.dumi.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.minjem.dumi.MainActivity
import com.minjem.dumi.R
import com.minjem.dumi.SemuaEcommerceActivity
import com.minjem.dumi.ecommerce.ECommerceActivity
import com.minjem.dumi.ecommerce.Helper.mToast
import kotlinx.android.synthetic.main.gridview_menu.view.*
import kotlin.contracts.contract

class MenuBaseAdapter(context: String): BaseAdapter() {
    private val list = when(context){
        "beranda" -> {
            menuUtama()
        }
        "topup" -> {
            menuTopup()
        }
        "bulanan" -> {
            menuBulanan()
        }
        else -> {
            menuTravel()
        }
    }


    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = parent?.context?.
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.gridview_menu,null)

        val activity  = parent.context as Activity
        val context = parent.context
        Glide.with(context).load(list[position].second).override(100,100).into(v.ivMenu)
        v.tvMenu.text = list[position].first

        v.ivMenu.setOnClickListener {

            var i = Intent(context, ECommerceActivity::class.java)
            i.putExtra("fragment", list[position].first)
            when (list[position].first){
                "PULSA" -> {
                    activity.startActivity(i)
                }
                "PLN" -> {
                    activity.startActivity(i)
                }
                "PDAM" -> {
                    activity.startActivity(i)
                }
                "GOPAY" -> {
                    i = Intent(context, MainActivity::class.java)
                    i.putExtra("fragment", "pinjaman")
                    activity.startActivity(i)
                }
                "SEMUA" -> {
                    activity.startActivity(Intent(context, SemuaEcommerceActivity::class.java))
                }
                else -> {
                    mToast(context, "Tunggu update kami selanjutnya..")
                }
            }

        }

        return v
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    private fun menuUtama():List<Pair<String,Int>>{
        return listOf(
                Pair("PULSA", R.drawable.ic_pulsa),
                Pair("PLN", R.drawable.ic_token_pln),
                Pair("GOPAY", R.drawable.ic_gopay),
                Pair("OVO", R.drawable.ic_ovo),
                Pair("HOTEL", R.drawable.ic_hotel),
                Pair("PESAWAT", R.drawable.ic_pesawat),
                Pair("KERETA", R.drawable.ic_kereta),
                Pair("SEMUA", R.drawable.ic_all)
        )
    }

    private fun menuBulanan():List<Pair<String,Int>>{
        return listOf(
                Pair("PDAM", R.drawable.ic_drop),
                Pair("PLN", R.drawable.ic_token_pln),
                Pair("BPJS", R.drawable.ic_bpjs),
                Pair("TELKOM", R.drawable.ic_telkom),
                Pair("GAS PGN", R.drawable.ic_pgn_gas),
                Pair("PASCABAYAR", R.drawable.ic_pulsa),
                Pair("TV KABEL", R.drawable.ic_tv_kabel)

        )
    }
    private fun menuTopup():List<Pair<String,Int>>{
        return listOf(
                Pair("PULSA", R.drawable.ic_pulsa),
                Pair("PLN", R.drawable.ic_token_pln),
                Pair("INTERNET", R.drawable.ic_internet),
                Pair("OVO", R.drawable.ic_ovo),
                Pair("GOPAY", R.drawable.ic_gopay),
                Pair("EMONEY", R.drawable.ic_emoney),
                Pair("BNI", R.drawable.ic_bni),
                Pair("LINK AJA", R.drawable.ic_linkaja),
                Pair("DANA", R.drawable.ic_dana),
                Pair("GRAB", R.drawable.ic_grab),
                Pair("VOUCHER GAME", R.drawable.ic_voucher_game)
        )
    }

    private fun menuTravel():List<Pair<String,Int>>{
        return listOf(
                Pair("PESAWAT", R.drawable.ic_pesawat),
                Pair("KERETA", R.drawable.ic_kereta),
                Pair("HOTEL", R.drawable.ic_hotel),
                Pair("PELNI", R.drawable.ic_pelni),
                Pair("FERRY", R.drawable.ic_ferry),
                Pair("BUS", R.drawable.ic_bus_travel)

        )
    }

}
package com.minjem.dumi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.minjem.dumi.R
import com.minjem.dumi.dataclass.HistoryData
import kotlinx.android.synthetic.main.recycler_view_history_pinjaman.view.*
import java.text.DecimalFormat

class HistoryPinjamanAdapter (mContext: Context, internal var list : List<HistoryData>)
    : RecyclerView.Adapter<HistoryPinjamanAdapter.PinjamanHolder>() {
    private var mExpandedPosition = -1
    val c = mContext

    inner class PinjamanHolder (view : View) :RecyclerView.ViewHolder(view){
        @SuppressLint("SetTextI18n")
        fun data(item : HistoryData){
            val df = DecimalFormat("#,###")
            val bulan = "${item.lamaPinjaman} Bulan"
            val jumlah = "Rp${df.format(item.pinjaman)}"

            val status = when(item.status){
                1 -> "Pengajuan"
                2 -> "Disetujui"
                3 -> "Ditolak"
                4 -> "Ditransfer"
                5 -> "Kredit berjalan"
                6 -> "Kredit Lunas"
                else -> "!!Dalam Proses Pengembangan!!"
            }

            when(item.status){
                1 -> itemView.tvStatusPinjaman.setTextColor(ResourcesCompat.getColor(c.resources, R.color.yellow, null))
                2 -> itemView.tvStatusPinjaman.setTextColor(ResourcesCompat.getColor(c.resources, R.color.green, null))
                3 -> itemView.tvStatusPinjaman.setTextColor(ResourcesCompat.getColor(c.resources, R.color.orange, null))
                4 -> itemView.tvStatusPinjaman.setTextColor(ResourcesCompat.getColor(c.resources, R.color.green, null))
                5  -> itemView.tvStatusPinjaman.setTextColor(ResourcesCompat.getColor(c.resources, R.color.green, null))
                6 -> itemView.tvStatusPinjaman.setTextColor(ResourcesCompat.getColor(c.resources, R.color.green, null))
                else -> "!!Dalam Proses Pengembangan!!"
            }
            itemView.tvTujuan.text = item.tujuanPinjaman
            itemView.tvTanggalPinjaman.text = item.tglPengajuan
            itemView.tvJumlahPinjaman.text = jumlah
            itemView.tvStatusPinjaman.text = status
            itemView.tvTenor.text = bulan
            itemView.tvBunga.text = "Rp${df.format(item.bungaRupiah)}"
            itemView.tvAngsuran.text = "Rp${df.format(item.angsuranPerbulan)}"
            itemView.tvAsuransi.text = "Rp${df.format(item.asuransiRupiah)}"
            itemView.tvAdministrasi.text = "Rp${df.format(item.administrasiRupiah)}"
            itemView.tvTrf.text = "Rp${df.format(item.transferRupiah)}"
            itemView.tvJumlahTerima.text = "Rp${df.format(item.diterimaRupiah)}"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryPinjamanAdapter.PinjamanHolder {
        val pinjamanView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_history_pinjaman, parent, false)
        return PinjamanHolder(pinjamanView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    override fun onBindViewHolder(holder: HistoryPinjamanAdapter.PinjamanHolder, position: Int) {
        holder.data(list[position])
        val isExpanded = position === mExpandedPosition
        holder.itemView.details.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        holder.itemView.setOnClickListener {
            mExpandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(position)
        }
    }

    fun filter (new : MutableList<HistoryData>){
        list = new.sortedByDescending { it.id?.toString() }
        notifyDataSetChanged()
    }
}
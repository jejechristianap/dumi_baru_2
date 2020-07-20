package com.minjem.dumi.model

data class DataPinjaman(
        var id: Int? = null,
        var nipBaru: String? = null,
        var pinjaman: Int? = null,
        var lamaPinjaman: Int? = null,
        var bungaPertahun: Int? = null,
        var bungaPersen: Double? = null,
        var bungaRupiah: Int? = null,
        var administrasiRupiah: Int? = null,
        var angsuranPerbulan: Double? = null,
        var asuransiRupiah: Int? = null,
        var transferRupiah: Int? = null,
        var diterimaRupiah: Int? = null,
        var status: String? = null,
        var tglPengajuan: String? = null,
        var tujuanPinjaman: String? = null,
        var tgl_mulai_pinjaman: String? = null,
        var tgl_akhir_pinjaman: String? = null,
        var tgl_lunas: String? = null,
        var nopk: String? = null
)
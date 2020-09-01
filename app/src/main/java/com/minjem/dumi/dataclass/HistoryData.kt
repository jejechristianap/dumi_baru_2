package com.minjem.dumi.dataclass

data class HistoryData(
        var id: Int? = null,
        var nipBaru: String? = null,
        var pinjaman: Int? = null,
        var lamaPinjaman: Int? = null,
        var bungaPertahun: Int? = null,
        var bungaPersen: Double? = null,
        var bungaRupiah: Int? = null,
        var administrasiRupiah: Int? = null,
        var angsuranPerbulan: Double? = null,
        var asuransiRupiah: Double? = null,
        var transferRupiah: Int? = null,
        var diterimaRupiah: Int? = null,
        var status: Int? = null,
        var tglPengajuan: String? = null,
        var tujuanPinjaman: String? = null,
        var tgl_mulai_pinjaman: String? = null,
        var tgl_akhir_pinjaman: String? = null,
        var tgl_lunas: String? = null,
        var nopk: String? = null,
        var registrasi: String? = null,
        var notif_registrasi: String? = null,
        var info_registrasi: String? = null,
        var activation: String? = null,
        var notif_activation: String? = null,
        var pdf_send: String? = null,
        var result_document: String? = null,
        var sign_document: String? = null,
        var status_document: String? = null

)
package com.minjem.dumi.ecommerce.api

import com.minjem.dumi.response.RUser
import com.minjem.dumi.response.RDigisign
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface BaseApiService {

    @FormUrlEncoded
    @POST("mmbc/saldo/get")
    fun getSaldo(@Field("id") id : Int,
                 @Field("nipBaru") nipBaru : String,
                 @Field("username") username : String,
                 @Field("password") password : String) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("mmbc/pulsa/get")
    fun getPulsa(@Field("username") username : String,
                 @Field("password") password : String) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("mmbc/pulsa/isi")
    fun isiPulsa(@Field("id_nasabah") id_nasabah : Int,
                 @Field("saldo") saldo : Int,
                 @Field("itrx") itrx : Int,
                 @Field("kodeoperator") kodeoperator : String,
                 @Field("nomortujuan") nomortujuan : String,
                 @Field("harga") harga : Int,
                 @Field("nominal") nominal : Int,
                 @Field("tipe") tipe : String,
                 @Field("username") username: String,
                 @Field("password") password: String
    ) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("mmbc/ppob/daftarharga")
    fun getPPOB(@Field("username") username : String,
                @Field("password") password : String) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("mmbc/ppob/cektagihan")
    fun cekTagihanPPOB(@Field("username") username : String,
                       @Field("password") password : String,
                       @Field("kode_produk") kodeproduk : String,
                       @Field("nomor_pelanggan") nomorpelanggan : String) : Call<ResponseBody>


    @FormUrlEncoded
    @POST("mmbc/ppob/isilistrik")
    fun isiTokenListrik(@Field("username") username : String,
                        @Field("password") password : String,
                        @Field("kode_produk") kodeproduk : String,
                        @Field("nomor_pelanggan") nomorpelanggan : String,
                        @Field("id_nasabah") idNasabah : Int,
                        @Field("nipBaru") nipBaru : String,
                        @Field("ppob_voucher") ppobVoucher : String


    ) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("mmbc/ppob/ppobriwayat")
    fun getRiwayat(@Field("username") username : String,
                   @Field("password") password : String,
                   @Field("id_nasabah") idNasabah: Int
    ) : Call<ResponseBody>


    @GET("json/getcodearea-json")
    fun getBandara():Call<ResponseBody>

    @FormUrlEncoded
    @POST("izi_data/creadit_feature")
    fun creditFeature(@Field("id") id : String,
                      @Field("name") name : String,
                      @Field("phone") phone : String
    ) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/insert")
    fun createUser(@Field("nip") nip: String,
                    @Field("email") email: String,
                    @Field("password") password: String,
                    @Field("no_telp") noTelp: String,
                    @Field("no_ktp") noKtp: String,
                    @Field("nama_ktp") namaKtp: String,
                    @Field("agama") agama: String,
                    @Field("jenis_kelamin") jenisKelamin: String,
                    @Field("tempatLahir") tempatLahir: String,
                    @Field("tglLhrPns") tglLahir: String,
                    @Field("status_kawin") statusKawin: String,
                    @Field("jumlah_tanggungan") jumlahTanggugan: String,
                    @Field("title") title: String,
                    @Field("ket_title") ketTitle: String,
                    @Field("inskerNama") inskerNama: String,
                    @Field("status_rumah") statusRumah: String,
                    @Field("alamat") alamat: String,
                    @Field("rt") rt: String,
                    @Field("rw") rw: String,
                    @Field("provinsi") propinsi: String,
                    @Field("kota") kota: String,
                    @Field("kecamatan") kecamatan: String,
                    @Field("kelurahan") kelurahan: String,  /*Fieldnya di app baru g ada, di api juga g ada*/
                    @Field("kode_pos") kodePos: String,  /*Perubahan param*/
                    @Field("status_hubungan_kontak_darurat") statusHubungan: String,
                    @Field("nama_kontak_darurat") namaPenanggung: String,
                    @Field("no_telp_kontak_darurat") noKtpPenanggung: String,
                    @Field("nama_gadis_ibu") namaGadisIbu: String,  /*data baru*/
                    @Field("dataBkn") dataBkn: String,
                    @Field("featurelist") featurelist: String,
                    @Field("creditscore") creditscore: String,
                    @Field("identity_address") identity_address: String,
                    @Field("identity_match") identity_match: String,
                    @Field("identity_nationnality") identity_nationnality: String,
                    @Field("identity_status") identity_status: String,
                    @Field("identity_work") identity_work: String,
                    @Field("isfacebook") isfacebook: String,
                    @Field("iswhatsapp") iswhatsapp: String,
                    @Field("multiphone_idinfo") multiphone_idinfo: String,
                    @Field("multiphone_phoneinfo_id") multiphone_phoneinfo_id: String,
                    @Field("multiphone_phoneinfo_id_phone") multiphone_phoneinfo_id_phone: String,
                    @Field("multiphone_status") multiphone_status: String,
                    @Field("phoneage") phoneage: String,
                    @Field("phoneage_status") phoneage_status: String,
                    @Field("phonealive_id_num") phonealive_id_num: String,
                    @Field("phonealive_phone_num") phonealive_phone_num: String,
                    @Field("phonealive_status") phonealive_status: String,
                    @Field("phonescore_status") phonescore_status: String,
                    @Field("npwp") npwp: String,
                    @Field("namaPasangan") namaPasangan: String,
                    @Field("noKtpPasangan") noKtpPasangan: String,
                    @Field("tglLahirPasangan") tglLahirPasangan: String,
                    @Field("namaBank") namaBank: String,
                    @Field("namaRekening") namaRekening: String,
                    @Field("noRekening") noRekening: String
    ): Call<ResponseBody>

    @Multipart
    @POST("user/update")
    fun uploadImages(@Part("nipBaru") nipBaru: RequestBody,
                     @Part image_ktp: MultipartBody.Part,
                     @Part image_selfi: MultipartBody.Part): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/get")
    fun loginDumi(
            @Field("nipBaru") nipBaru: String,
            @Field("sandi") sandi: String
    ): Call<RUser>

    @FormUrlEncoded
    @POST("digisign/register")
    fun registrasiDigisignTanpaFoto(
            @Field("nama") nama: String,
            @Field("tlp") tlp: String,
            @Field("idktp") idktp: String,
            @Field("alamat") alamat: String,
            @Field("jenis_kelamin") jenis_kelamin: String,
            @Field("kecamatan") kecamatan: String,
            @Field("kelurahan") kelurahan: String,
            @Field("kode_pos") kode_pos: String,
            @Field("kota") kota: String,
            @Field("tgl_lahir") tgl_lahir: String,
            @Field("provinci") provinci: String,
            @Field("tmp_lahir") tmp_lahir: String,
            @Field("email") email: String,
            @Field("npwp") npwp: String,
            @Field("fotoktp") fotoktp: String,
            @Field("fotodiri") fotodiri: String,
            @Field("ttd") ttd: String,
            @Field("fotonpwp") fotonpwp: String,
            @Field("is_foto") is_foto: Boolean
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("digisign/activation")
    fun activationDigisign(
            @Field("email") email: String
    ): Call<ResponseBody>

    @Multipart
    @POST("digisign/register")
    fun registrasiDigisignPakaiFoto(
            @Part("nama") namaPns: RequestBody?,
            @Part("tlp") no_hp: RequestBody?,
            @Part("idktp") idktp: RequestBody?,
            @Part("alamat") alamat: RequestBody?,
            @Part("jenis_kelamin") jenis_kelamin: RequestBody?,
            @Part("kecamatan") kecamatan: RequestBody?,
            @Part("kelurahan") kelurahan: RequestBody?,
            @Part("kode_pos") kode_pos: RequestBody?,
            @Part("kota") kota: RequestBody?,
            @Part("tgl_lahir") tgl_lahir: RequestBody?,
            @Part("provinci") provinci: RequestBody?,
            @Part("tmp_lahir") tmp_lahir: RequestBody?,
            @Part("email") email: RequestBody?,
            @Part("npwp") npwp: RequestBody?,
            @Part("is_foto") is_foto: RequestBody?,
            @Part fotoktp: MultipartBody.Part?,
            @Part fotodiri: MultipartBody.Part?,
            @Part ttd: MultipartBody.Part?,
            @Part fotonpwp: MultipartBody.Part?
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("digisign/cek_aktivasi")
    fun cekAktivasi(
            @Field("nik") nik : String,
            @Field("email") email: String,
            @Field("idPinjaman") idPinjaman: Int
    ) : Call<RDigisign>

    @FormUrlEncoded
    @POST("digisign/sign_document")
    fun signDocumentDigisign(
            @Field("email") email: String,
            @Field("document_id") document_id: String
    ): Call<ResponseBody>
}
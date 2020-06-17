package com.minjem.dumi.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterInterface {

    @FormUrlEncoded
    @POST("user/insert")
    Call<ResponseBody> createUser(
            @Field("nip") String nip,
            @Field("email") String email,
            @Field("password") String password,
            @Field("no_telp") String noTelp,
            @Field("no_ktp") String noKtp,
            @Field("nama_ktp") String namaKtp,
            @Field("agama") String agama,
            @Field("jenis_kelamin") String jenisKelamin,
            @Field("tempatLahir") String tempatLahir,
            @Field("tglLhrPns") String tglLahir,
            @Field("status_kawin") String statusKawin,
            @Field("jumlah_tanggungan") String jumlahTanggugan,
            @Field("title") String title,
            @Field("ket_title") String ketTitle,
            @Field("inskerNama") String inskerNama,
            @Field("status_rumah") String statusRumah,
            @Field("alamat") String alamat,
            @Field("rt") String rt,
            @Field("rw") String rw,
            @Field("provinsi") String propinsi,
            @Field("kota") String kota,
            @Field("kecamatan") String kecamatan,
            @Field("kelurahan") String kelurahan,
            @Field("kode_pos") String kodePos,
            @Field("status_hubungan_kontak_darurat") String statusHubungan,
            @Field("nama_kontak_darurat") String namaPenanggung,
            @Field("no_telp_kontak_darurat") String noKtpPenanggung,
            @Field("nama_gadis_ibu") String namaGadisIbu,
            @Field("dataBkn") String dataBkn,
            @Field("featurelist") String featurelist,
            @Field("creditscore") String creditscore,
            @Field("identity_address") String identity_address,
            @Field("identity_match") String identity_match,
            @Field("identity_nationnality") String identity_nationnality,
            @Field("identity_status") String identity_status,
            @Field("identity_work") String identity_work,
            @Field("isfacebook") String isfacebook,
            @Field("iswhatsapp") String iswhatsapp,
            @Field("multiphone_idinfo") String multiphone_idinfo,
            @Field("multiphone_phoneinfo_id") String multiphone_phoneinfo_id,
            @Field("multiphone_phoneinfo_id_phone") String multiphone_phoneinfo_id_phone,
            @Field("multiphone_status") String multiphone_status,
            @Field("phoneage") String phoneage,
            @Field("phoneage_status") String phoneage_status,
            @Field("phonealive_id_num") String phonealive_id_num,
            @Field("phonealive_phone_num") String phonealive_phone_num,
            @Field("phonealive_status") String phonealive_status,
            @Field("phonescore_status") String phonescore_status,
            @Field("npwp") String npwp,
            @Field("namaPasangan") String namaPasangan,
            @Field("noKtpPasangan") String noKtpPasangan,
            @Field("tglLahirPasangan") String tglLahirPasangan,
            @Field("namaBank") String namaBank,
            @Field("namaRekening") String namaRekening,
            @Field("noRekening") String noRekening
       );
}

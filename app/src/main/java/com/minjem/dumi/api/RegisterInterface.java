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
            @Field("status_hubungan") String statusHubungan,
            @Field("nama_penanggung") String namaPenanggung,
            @Field("no_ktp_penanggung") String noKtpPenanggung,
            @Field("nama_gadis_ibu") String namaGadisIbu
    );
}

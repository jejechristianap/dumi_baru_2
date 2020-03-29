package com.fidac.dumi.api;

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
            @Field("no_ktp") String noKtp,
            @Field("nama_ktp") String namaKtp,
            @Field("jenis_kelamin") String jenisKelamin,
            @Field("agama") String agama,
            @Field("title") String title,
            @Field("ket_title") String ketTitle,
            @Field("rt") String rt,
            @Field("rw") String rw,
            @Field("kelurahan") String kelurahan,
            @Field("kecamatan") String kecamatan,
            @Field("kota") String kota,
            @Field("alamat") String alamat,
            @Field("kode_pos") String kodePos,
            @Field("no_telp") String noTelp
    );
}

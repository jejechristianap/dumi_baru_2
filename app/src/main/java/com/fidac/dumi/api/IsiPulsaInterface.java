package com.fidac.dumi.api;

import com.fidac.dumi.model.DaftarHargaPulsa;
import com.fidac.dumi.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IsiPulsaInterface {

    @FormUrlEncoded
    @POST("daftarharga")
    Call<List<DaftarHargaPulsa>> getDaftarHargaPulsa(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("isipulsa")
    Call<ResponseBody> isiPulsa(
            @Field("username") String nip,
            @Field("password") String password,
            @Field("itrx") String itrx,
            @Field("kodeoperator") String kodeOperator,
            @Field("nomortujuan") String nomorTujuan
    );

    @FormUrlEncoded
    @POST("cekstatus")
    Call<ResponseBody> cekStatusTransaksi(
            @Field("username") String username,
            @Field("password") String password,
            @Field("invoice") String invoice,
            @Field("nomortujuan") String nomorTujuan
    );
}

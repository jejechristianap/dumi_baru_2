package com.fidac.dumi.api;

import java.util.ResourceBundle;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PropinsiInterface {

    @GET("provinsi/get")
    Call<ResponseBody> getPropinsi();

    @FormUrlEncoded
    @POST("kabupaten/get")
    Call<ResponseBody> getKabupaten(@Field("provinsi") String prop);

    @FormUrlEncoded
    @POST("kecamatan/get")
    Call<ResponseBody> getKecamatan(@Field("kabupaten") String kabupaten);

    @FormUrlEncoded
    @POST("desa/get")
    Call<ResponseBody> getDesa(@Field("kecamatan") String kecamatan);

    @FormUrlEncoded
    @POST("desa/get")
    Call<ResponseBody> getPos(@Field("kecamatan") String desa);

}

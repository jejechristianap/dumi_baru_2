package com.minjem.dumi.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CekNipBknInterface {

    @FormUrlEncoded
    @POST("bkn/data")
    Call<ResponseBody> getBkn(
            @Field("nip") String nip,
            @Field("tgl_lahir") String tgl,
            @Field("namaPns") String namaPns
    );

    @GET("bkn/token")
    Call<ResponseBody> getToken();
}

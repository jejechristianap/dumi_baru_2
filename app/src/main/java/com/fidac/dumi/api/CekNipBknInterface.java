package com.fidac.dumi.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CekNipBknInterface {

    @FormUrlEncoded
    @POST("bkn/get")
    Call<JsonObject> cekBkn(
            @Field("nip") String nip
    );

    Call<JsonObject> getJson();
}

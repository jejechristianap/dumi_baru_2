package com.fidac.dumi.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginInterface {

    @FormUrlEncoded
    @POST("user/get")
    Call<String> getUserLogin(
            @Field("nip") String nip,
            @Field("password") String password
    );
}

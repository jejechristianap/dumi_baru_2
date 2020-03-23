package com.fidac.dumi.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterInterface {

    @FormUrlEncoded
    @POST("user/insert")
    Call<String> createUser(
            @Field("nip") String nip,
            @Field("email") String email,
            @Field("username") String uname,
            @Field("password") String password
    );
}

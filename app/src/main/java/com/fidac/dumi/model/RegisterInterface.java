package com.fidac.dumi.model;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterInterface {
    String REGIURL = "http://app.ternak-burung.top/api/user/";
    @FormUrlEncoded
    @POST("insert")
    Call<String> getUserRegis(
            @Field("nip") String nip,
            @Field("email") String email,
            @Field("username") String uname,
            @Field("password") String password
    );
}

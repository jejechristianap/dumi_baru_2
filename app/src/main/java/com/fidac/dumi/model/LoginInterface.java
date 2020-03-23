package com.fidac.dumi.model;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginInterface {
    String LOGINURL = "http://app.ternak-burung.top/api/user/";
    @FormUrlEncoded
    @POST("get")
    Call<String> getUserLogin(

            @Field("nip") String nip,
            @Field("password") String password
    );
}

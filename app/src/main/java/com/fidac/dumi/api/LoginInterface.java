package com.fidac.dumi.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginInterface {

    @FormUrlEncoded
    @POST("user/get")
    Call<ResponseBody> getUserLogin(
            @Field("nipBaru") String nip,
            @Field("sandi") String password
    );
}

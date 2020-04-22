package com.minjem.dumi.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UbahSandiInterface {

    @FormUrlEncoded
    @POST("user/sandi")
    Call<ResponseBody> ubahSandi(@Field("nipBaru") String nip,
                                 @Field("sandi") String sandi);

}

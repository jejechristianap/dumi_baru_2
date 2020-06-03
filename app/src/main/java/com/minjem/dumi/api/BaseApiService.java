package com.minjem.dumi.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BaseApiService {
    @FormUrlEncoded
    @POST("http://app.ternak-burung.top/api/user/get")
    Call<ResponseBody> loginRequest(@Field("nip") String nip,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("http://app.ternak-burung.top/api/user/insert")
    Call<ResponseBody> registerRequest(@Field("nip") String nip,
                                       @Field("nama") String nama,
                                       @Field("email") String email,
                                       @Field("password") String password);


}

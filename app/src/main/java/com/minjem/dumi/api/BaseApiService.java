package com.minjem.dumi.api;

import com.hendi.pulsa.response.Response;
import com.minjem.dumi.response.HistoryResponse;

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

    @FormUrlEncoded
    @POST("izi_data/creadit_feature")
    Call<ResponseBody> creditFeature(@Field("id") String id,
                                     @Field("name") String name,
                                     @Field("phone") String phone);

    @GET("info/regular")
    Call<ResponseBody> getInfo();

    @FormUrlEncoded
    @POST("auth/email_auth")
    Call<ResponseBody> sendCodeVerification(@Field("email") String email,
                                            @Field("code_verification") String code);

    @GET("config/get")
    Call<ResponseBody> getVersionApp();

    @FormUrlEncoded
    @POST("pinjaman/get")
    Call<HistoryResponse> getPinjaman(
            @Field("nipBaru") String nipBaru
    );

}

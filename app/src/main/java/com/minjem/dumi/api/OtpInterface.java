package com.minjem.dumi.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OtpInterface {
    @FormUrlEncoded
    @POST("user/insert")
    Call<ResponseBody> getOtp(@Field("user") String user,
                              @Field("password") String passwrod,
                              @Field("SMSText") String SMSText,
                              @Field("GSM") String GSM,
                              @Field("unicode") String unicode,
                              @Field("otp") String otp);
}

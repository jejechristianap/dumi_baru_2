package com.fidac.dumi.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PropinsiInterface {

    @GET("wilayah/get")
    Call<ResponseBody> getPropinsi();

}

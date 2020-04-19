package com.fidac.dumi.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GetBungaInterface {

    @GET("bunga/get")
    Call<ResponseBody> getBunga();
}

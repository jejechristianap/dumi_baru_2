package com.fidac.dumi.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CekUserExist {

    @FormUrlEncoded
    @POST("user/cek")
    Call<ResponseBody> cekUser(
            @Field("nipBaru") String nip
    );
}

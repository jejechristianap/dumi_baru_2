package com.fidac.dumi.api;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface UploadImageInterface {


    @Multipart
    @POST("user/update")
    Call<ResponseBody> uploadImages(@Part("nipBaru") RequestBody nipBaru,
                                   @Part MultipartBody.Part image_ktp,
                                   @Part MultipartBody.Part image_selfi);

    @Multipart
    @POST("user/profile")
    Call<ResponseBody> uploadProfile(@Part("nipBaru") RequestBody nipBaru,
                                    @Part MultipartBody.Part image_ktp);

}

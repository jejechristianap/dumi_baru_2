package com.fidac.dumi.api;

import com.fidac.dumi.model.DaftarHargaPulsa;
import com.fidac.dumi.model.NotifikasiData;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface StatusPinjamanInterface {

    @FormUrlEncoded
    @POST("pinjaman/get")
    Call<ResponseBody> getPinjaman(
            @Field("nipBaru") String nipBaru
    );

    @FormUrlEncoded
    @POST("notification/get")
    Call<NotifikasiData> getNotif(@Field("nipBaru") String nip);
}

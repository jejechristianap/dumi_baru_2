package com.fidac.dumi.api;

import com.fidac.dumi.fragment.NotifikasiResponse;
import com.fidac.dumi.model.Angsuran_Response;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface StatusPinjamanInterface {

    @FormUrlEncoded
    @POST("pinjaman/get")
    Call<ResponseBody> getPinjaman(
            @Field("nipBaru") String nipBaru
    );

    @FormUrlEncoded
    @POST("notification/get")
    Call<NotifikasiResponse> getNotif(@Field("nipBaru") String nip);



    @FormUrlEncoded
    @POST("angsuran/get")
    Call<Angsuran_Response> getAngsuran(@Field("noPinjaman") String noPinjaman);

}

package com.minjem.dumi.ecommerce.api

import com.hendi.pulsa.response.G_Pulsa
import com.hendi.pulsa.response.R_Pulsa
import com.hendi.pulsa.response.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface BaseApiService {

    @FormUrlEncoded
    @POST("json/pulsa/daftarharga")
    fun getPulsa(@Field("username") username : String,
                 @Field("password") password : String) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("json/pulsa/isipulsa")
    fun isiPulsa(@Field("username") username: String,
                 @Field("password") password: String,
                 @Field("itrx") itrx : String,
                 @Field("kodeoperator") kodeoperator : String,
                 @Field("nomortujuan") nomortujuan : String) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("json/ppob/daftarharga")
    fun getPln(@Field("username") username : String,
                 @Field("password") password : String) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("json/ppob/cektagihan")
    fun cekTagihanPln(@Field("username") username : String,
                      @Field("password") password : String,
                      @Field("ppob_kodeproduk") kodeproduk : String,
                      @Field("ppob_nomorpelanggan") nomorpelanggan : String) : Call<ResponseBody>

    @FormUrlEncoded
    @GET("json/getcodearea-json")
    fun getBandara():Call<ResponseBody>
}
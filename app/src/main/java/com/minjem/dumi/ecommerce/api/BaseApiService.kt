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
    @POST("mmbc/saldo/get")
    fun getSaldo(@Field("id") id : Int,
                 @Field("nipBaru") nipBaru : String,
                 @Field("username") username : String,
                 @Field("password") password : String) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("mmbc/pulsa/get")
    fun getPulsa(@Field("username") username : String,
                 @Field("password") password : String) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("mmbc/pulsa/isi")
    fun isiPulsa(@Field("id_nasabah") id_nasabah : Int,
                 @Field("saldo") saldo : Int,
                 @Field("itrx") itrx : Int,
                 @Field("kodeoperator") kodeoperator : String,
                 @Field("nomortujuan") nomortujuan : String,
                 @Field("harga") harga : Int,
                 @Field("nominal") nominal : Int,
                 @Field("tipe") tipe : String,
                 @Field("username") username: String,
                 @Field("password") password: String
    ) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("mmbc/ppob/daftarharga")
    fun getPln(@Field("username") username : String,
                 @Field("password") password : String) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("mmbc/ppob/cektagihan")
    fun cekTagihanPln(@Field("username") username : String,
                      @Field("password") password : String,
                      @Field("ppob_kodeproduk") kodeproduk : String,
                      @Field("ppob_nomorpelanggan") nomorpelanggan : String) : Call<ResponseBody>


    @GET("json/getcodearea-json")
    fun getBandara():Call<ResponseBody>
}
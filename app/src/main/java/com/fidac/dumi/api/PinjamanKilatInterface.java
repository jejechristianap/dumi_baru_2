package com.fidac.dumi.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PinjamanKilatInterface {
    @FormUrlEncoded
    @POST("pinjaman/insert")
    Call<ResponseBody> ajukanPinjaman(
            @Field("nipBaru") String nip,
            @Field("pinjaman") double pinjaman,
            @Field("lamaPinjaman") double lamaPinjaman,
            @Field("bungaPertahun") double bungaPertahun,
            @Field("bungaPersen") double bungaPersen,
            @Field("bungaRupiah") double bungaRupiah,
            @Field("administrasiRupiah") double admin,
            @Field("angsuranPerbulan") double angsuran,
            @Field("transferRupiah") double transferRupiah,
            @Field("tujuanPinjaman") String tujuanPinjaman,
            @Field("tgl_mulai_pinjaman") String tglMulai,
            @Field("tgl_akhir_pinjaman") String tglAkhir,
            @Field("nopk") String noPk,
            @Field("diterimaRupiah") double diterimaRupiah,
            @Field("asuransiRupiah") double asuransiRupiah,
            @Field("namaBank") String namaBank,
            @Field("noRek") String noRek,
            @Field("namaPemilikRek") String namaPemilikRek
    );

}

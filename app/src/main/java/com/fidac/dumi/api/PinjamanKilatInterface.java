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
            @Field("pinjaman") float pinjaman,
            @Field("lamaPinjaman") float lamaPinjaman,
            @Field("bungaPertahun") float bungaPertahun,
            @Field("bungaPersen") float bungaPersen,
            @Field("bungaRupiah") float bungaRupiah,
            @Field("administrasiRupiah") float admin,
            @Field("angsuranPerbulan") float angsuran,
            @Field("transferRupiah") float transferRupiah,
            @Field("tujuanPinjaman") String tujuanPinjaman,
            @Field("tgl_mulai_pinjaman") String tglMulai,
            @Field("tgl_akhir_pinjaman") String tglAkhir,
            @Field("nopk") String noPk,
            @Field("diterimaRupiah") float diterimaRupiah,
            @Field("asuransiRupiah") float asuransiRupiah,
            @Field("namaBank") String namaBank,
            @Field("noRek") String noRek,
            @Field("namaPemilikRek") String namaPemilikRek
    );

}

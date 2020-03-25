package com.fidac.dumi.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NipResources {

    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Datum> data;

    public class Datum {
        @SerializedName("nipBaru")
        private String nipBaru;
        @SerializedName("golongan")
        private String golongan;
        @SerializedName("namaPns")
        private String namaPns;
        @SerializedName("namaJabatan")
        private String namaJabatan;
        @SerializedName("inskerNama")
        private String inskerNama;
        @SerializedName("tempatLahir")
        private String tempatLahir;
        @SerializedName("tglLhrPns")
        private String tglLhrPns;
        @SerializedName("npwpNomor")
        private String npwpNomor;
        @SerializedName("noKtp")
        private String noKtp;
        @SerializedName("pasangan")
        private String pasangan;
        @SerializedName("tglLhrPasangan")
        private String tglLhrPasangan;
        @SerializedName("statusPns")
        private String statusPns;

    }
}

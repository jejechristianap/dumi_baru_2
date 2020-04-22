package com.minjem.dumi.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NipResources {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public static class Datum {
        @SerializedName("nipBaru")
        @Expose
        private String nipBaru;
        @SerializedName("golongan")
        @Expose
        private String golongan;
        @SerializedName("namaPns")
        @Expose
        private String namaPns;
        @SerializedName("namaJabatan")
        @Expose
        private String namaJabatan;
        @SerializedName("inskerNama")
        @Expose
        private String inskerNama;
        @SerializedName("tempatLahir")
        @Expose
        private String tempatLahir;
        @SerializedName("tglLhrPns")
        @Expose
        private String tglLhrPns;
        @SerializedName("npwpNomor")
        @Expose
        private String npwpNomor;
        @SerializedName("noKtp")
        @Expose
        private String noKtp;
        @SerializedName("pasangan")
        @Expose
        private String pasangan;
        @SerializedName("tglLhrPasangan")
        @Expose
        private String tglLhrPasangan;
        @SerializedName("statusPns")
        @Expose
        private String statusPns;

        public String getNipBaru() {
            return nipBaru;
        }

        public void setNipBaru(String nipBaru) {
            this.nipBaru = nipBaru;
        }

        public String getGolongan() {
            return golongan;
        }

        public void setGolongan(String golongan) {
            this.golongan = golongan;
        }

        public String getNamaPns() {
            return namaPns;
        }

        public void setNamaPns(String namaPns) {
            this.namaPns = namaPns;
        }

        public String getNamaJabatan() {
            return namaJabatan;
        }

        public void setNamaJabatan(String namaJabatan) {
            this.namaJabatan = namaJabatan;
        }

        public String getInskerNama() {
            return inskerNama;
        }

        public void setInskerNama(String inskerNama) {
            this.inskerNama = inskerNama;
        }

        public String getTempatLahir() {
            return tempatLahir;
        }

        public void setTempatLahir(String tempatLahir) {
            this.tempatLahir = tempatLahir;
        }

        public String getTglLhrPns() {
            return tglLhrPns;
        }

        public void setTglLhrPns(String tglLhrPns) {
            this.tglLhrPns = tglLhrPns;
        }

        public String getNpwpNomor() {
            return npwpNomor;
        }

        public void setNpwpNomor(String npwpNomor) {
            this.npwpNomor = npwpNomor;
        }

        public String getNoKtp() {
            return noKtp;
        }

        public void setNoKtp(String noKtp) {
            this.noKtp = noKtp;
        }

        public String getPasangan() {
            return pasangan;
        }

        public void setPasangan(String pasangan) {
            this.pasangan = pasangan;
        }

        public String getTglLhrPasangan() {
            return tglLhrPasangan;
        }

        public void setTglLhrPasangan(String tglLhrPasangan) {
            this.tglLhrPasangan = tglLhrPasangan;
        }

        public String getStatusPns() {
            return statusPns;
        }

        public void setStatusPns(String statusPns) {
            this.statusPns = statusPns;
        }
    }
}

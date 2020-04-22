package com.minjem.dumi.model;

public class DaftarHargaPulsa {
    private String tipe, operator, kodeoperator, keterangan, nominal, harga, status;

    public DaftarHargaPulsa() {
    }

    public DaftarHargaPulsa(String tipe, String operator, String kodeoperator, String keterangan, String nominal, String harga, String status) {
        this.tipe = tipe;
        this.operator = operator;
        this.kodeoperator = kodeoperator;
        this.keterangan = keterangan;
        this.nominal = nominal;
        this.harga = harga;
        this.status = status;
    }

    public String getTipe() {
        return tipe;
    }

    public String getOperator() {
        return operator;
    }

    public String getKodeoperator() {
        return kodeoperator;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getNominal() {
        return nominal;
    }

    public String getHarga() {
        return harga;
    }

    public String getStatus() {
        return status;
    }
}

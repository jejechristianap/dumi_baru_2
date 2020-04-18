package com.fidac.dumi.model;

public class NotifikasiData {
    int id, status, lamaPinjaman;
    String nipBaru, tglPengajuan, tujuanPinjaman, tglMulaiPinjaman, tglAkhirPinjaman,
            imgSuratKuasa, imgSuratBendahara, imgPersetujuan, imgSkCpns, noPk;
    double pinjaman, bungaPertahun, bungaPersen, bungaRp, adminRp, angsuranPerbulan,
            asuransiRp, transferRp, diterimaRp;

    public NotifikasiData(){

    }

    public NotifikasiData(int id, int status, int lamaPinjaman, String nipBaru, String tglPengajuan, String tujuanPinjaman, String tglMulaiPinjaman, String tglAkhirPinjaman, String imgSuratKuasa, String imgSuratBendahara, String imgPersetujuan, String imgSkCpns, String noPk, double pinjaman, double bungaPertahun, double bungaPersen, double bungaRp, double adminRp, double angsuranPerbulan, double asuransiRp, double transferRp, double diterimaRp) {
        this.id = id;
        this.status = status;
        this.lamaPinjaman = lamaPinjaman;
        this.nipBaru = nipBaru;
        this.tglPengajuan = tglPengajuan;
        this.tujuanPinjaman = tujuanPinjaman;
        this.tglMulaiPinjaman = tglMulaiPinjaman;
        this.tglAkhirPinjaman = tglAkhirPinjaman;
        this.imgSuratKuasa = imgSuratKuasa;
        this.imgSuratBendahara = imgSuratBendahara;
        this.imgPersetujuan = imgPersetujuan;
        this.imgSkCpns = imgSkCpns;
        this.noPk = noPk;
        this.pinjaman = pinjaman;
        this.bungaPertahun = bungaPertahun;
        this.bungaPersen = bungaPersen;
        this.bungaRp = bungaRp;
        this.adminRp = adminRp;
        this.angsuranPerbulan = angsuranPerbulan;
        this.asuransiRp = asuransiRp;
        this.transferRp = transferRp;
        this.diterimaRp = diterimaRp;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public int getLamaPinjaman() {
        return lamaPinjaman;
    }

    public String getNipBaru() {
        return nipBaru;
    }

    public String getTglPengajuan() {
        return tglPengajuan;
    }

    public String getTujuanPinjaman() {
        return tujuanPinjaman;
    }

    public String getTglMulaiPinjaman() {
        return tglMulaiPinjaman;
    }

    public String getTglAkhirPinjaman() {
        return tglAkhirPinjaman;
    }

    public String getImgSuratKuasa() {
        return imgSuratKuasa;
    }

    public String getImgSuratBendahara() {
        return imgSuratBendahara;
    }

    public String getImgPersetujuan() {
        return imgPersetujuan;
    }

    public String getImgSkCpns() {
        return imgSkCpns;
    }

    public String getNoPk() {
        return noPk;
    }

    public double getPinjaman() {
        return pinjaman;
    }

    public double getBungaPertahun() {
        return bungaPertahun;
    }

    public double getBungaPersen() {
        return bungaPersen;
    }

    public double getBungaRp() {
        return bungaRp;
    }

    public double getAdminRp() {
        return adminRp;
    }

    public double getAngsuranPerbulan() {
        return angsuranPerbulan;
    }

    public double getAsuransiRp() {
        return asuransiRp;
    }

    public double getTransferRp() {
        return transferRp;
    }

    public double getDiterimaRp() {
        return diterimaRp;
    }
}

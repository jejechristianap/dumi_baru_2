package com.fidac.dumi.model;

public class User {
    private int id;
    private String nip , email, namaLengkap, noKtp, agama, title, ketTitle, rt, rw, kelurahan,
                    kecamatan, kota, alamat, kodePos, noTelp;

    public User(){

    }

    public User(int id, String nip, String email, String namaLengkap, String noKtp, String agama, String title, String ketTitle, String rt, String rw, String kelurahan, String kecamatan, String kota, String alamat, String kodePos, String noTelp) {
        this.id = id;
        this.nip = nip;
        this.email = email;
        this.namaLengkap = namaLengkap;
        this.noKtp = noKtp;
        this.agama = agama;
        this.title = title;
        this.ketTitle = ketTitle;
        this.rt = rt;
        this.rw = rw;
        this.kelurahan = kelurahan;
        this.kecamatan = kecamatan;
        this.kota = kota;
        this.alamat = alamat;
        this.kodePos = kodePos;
        this.noTelp = noTelp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getNoKtp() {
        return noKtp;
    }

    public void setNoKtp(String noKtp) {
        this.noKtp = noKtp;
    }

    public String getAgama() {
        return agama;
    }

    public void setAgama(String agama) {
        this.agama = agama;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKetTitle() {
        return ketTitle;
    }

    public void setKetTitle(String ketTitle) {
        this.ketTitle = ketTitle;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public String getRw() {
        return rw;
    }

    public void setRw(String rw) {
        this.rw = rw;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(String kelurahan) {
        this.kelurahan = kelurahan;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKodePos() {
        return kodePos;
    }

    public void setKodePos(String kodePos) {
        this.kodePos = kodePos;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    /*   public User(int anInt, String string, String string1, String string2, String string3, String string4, String string5, String string6, String string7, String string8, String string9, String string10, String string11, String string12, String string13, String string14) {

    }*/

}

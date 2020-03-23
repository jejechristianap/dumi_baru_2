package com.fidac.dumi.model;

public class User {
    private String id;
    private String nip, nama, email;

    public User(){

    }

    public User(String id, String nip, String nama, String email) {
        this.id = id;
        this.nip = nip;
        this.email = email;
        this.nama = nama;

    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

}

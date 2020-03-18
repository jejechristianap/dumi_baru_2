package com.fidac.dumi.model;

public class User {
    private int id;
    private String nip, nama, email;

    public User(){

    }

    public User(int id, String nip, String email, String nama) {
        this.id = id;
        this.nip = nip;
        this.email = email;

    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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

}

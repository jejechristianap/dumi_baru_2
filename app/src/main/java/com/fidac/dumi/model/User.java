package com.fidac.dumi.model;

public class User {
    private int id;
    private String nip, email, password;

    public User(){

    }

    public User(int id, String nip, String email, String password) {
        this.id = id;
        this.nip = nip;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

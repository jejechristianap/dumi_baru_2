package com.fidac.dumi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotifikasiData {
    int id, status;
    String judul, isi, waktu, idNasabah, token;
    boolean bool;
    @SerializedName("data")
    @Expose
    List<NotifikasiData> notif;

    public List<NotifikasiData> getNotif(){
        return notif;
    }

    public NotifikasiData(){

    }

    public NotifikasiData(boolean bool, int id, String token, String judul, String isi, int status, String waktu, String idNasabah) {
        this.bool = bool;
        this.id = id;
        this.token = token;
        this.judul = judul;
        this.isi = isi;
        this.status = status;
        this.waktu = waktu;
        this.idNasabah = idNasabah;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public void setNotif(List<NotifikasiData> notif) {
        this.notif = notif;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getIdNasabah() {
        return idNasabah;
    }

    public void setIdNasabah(String idNasabah) {
        this.idNasabah = idNasabah;
    }
}

package com.fidac.dumi.model;

public class User {
    private int id;
    private String
            nip,
            email,
            password,
            noKtp,
            namaLengkap,
            agama,
            jenisKelamin,
            tempatLahir,
            tanggalLahir,
            statusKawin,
            jumlahTanggungan,
            title,
            ketTitle,
            inskerKerja,
            statusRumah,
            alamat,
            rt,
            rw,
            propinsi,
            kota,
            kecamatan,
            kelurahan,
            kodePos,
            statusHubungan,
            namaPenanggung,
            noKtpPenanggung,
            namaIbu,
            noTelp,
            imageKtp,
            imageSelfi,
            imageProfile;

    public User(int id, String nip, String email, String password, String noKtp, String namaLengkap, String agama, String jenisKelamin, String tempatLahir, String tanggalLahir, String statusKawin, String jumlahTanggungan, String title, String ketTitle, String inskerKerja, String statusRumah, String alamat, String rt, String rw, String propinsi, String kota, String kecamatan, String kelurahan, String kodePos, String statusHubungan, String namaPenanggung, String noKtpPenanggung, String namaIbu, String noTelp, String imageKtp, String imageSelfi, String imageProfile) {
        this.id = id;
        this.nip = nip;
        this.email = email;
        this.password = password;
        this.noKtp = noKtp;
        this.namaLengkap = namaLengkap;
        this.agama = agama;
        this.jenisKelamin = jenisKelamin;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
        this.statusKawin = statusKawin;
        this.jumlahTanggungan = jumlahTanggungan;
        this.title = title;
        this.ketTitle = ketTitle;
        this.inskerKerja = inskerKerja;
        this.statusRumah = statusRumah;
        this.alamat = alamat;
        this.rt = rt;
        this.rw = rw;
        this.propinsi = propinsi;
        this.kota = kota;
        this.kecamatan = kecamatan;
        this.kelurahan = kelurahan;
        this.kodePos = kodePos;
        this.statusHubungan = statusHubungan;
        this.namaPenanggung = namaPenanggung;
        this.noKtpPenanggung = noKtpPenanggung;
        this.namaIbu = namaIbu;
        this.noTelp = noTelp;
        this.imageKtp = imageKtp;
        this.imageSelfi = imageSelfi;
        this.imageProfile = imageProfile;
    }

    public int getId() {
        return id;
    }

    public String getNip() {
        return nip;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNoKtp() {
        return noKtp;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getAgama() {
        return agama;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public String getStatusKawin() {
        return statusKawin;
    }

    public String getJumlahTanggungan() {
        return jumlahTanggungan;
    }

    public String getTitle() {
        return title;
    }

    public String getKetTitle() {
        return ketTitle;
    }

    public String getInskerKerja() {
        return inskerKerja;
    }

    public String getStatusRumah() {
        return statusRumah;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getRt() {
        return rt;
    }

    public String getRw() {
        return rw;
    }

    public String getPropinsi() {
        return propinsi;
    }

    public String getKota() {
        return kota;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public String getKodePos() {
        return kodePos;
    }

    public String getStatusHubungan() {
        return statusHubungan;
    }

    public String getNamaPenanggung() {
        return namaPenanggung;
    }

    public String getNoKtpPenanggung() {
        return noKtpPenanggung;
    }

    public String getNamaIbu() {
        return namaIbu;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public String getImageKtp() {
        return imageKtp;
    }

    public String getImageSelfi() {
        return imageSelfi;
    }

    public String getImageProfile() {
        return imageProfile;
    }
}


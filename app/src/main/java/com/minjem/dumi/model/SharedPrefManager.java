package com.minjem.dumi.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "dumi_user";
    private static final String KEY_ID = "key_id";
    private static final String KEY_NIP = "key_nip";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_PASSWORD =  "key_pass";
    private static final String KEY_NO_KTP = "key_no_ktp";
    private static final String KEY_NAMA = "key_nama";
    private static final String KEY_AGAMA = "key_agama";
    private static final String KEY_JK = "key_jk";
    private static final String KEY_TEMPAT_LAHIR = "key_tempat_lahir";
    private static final String KEY_TANGGAL_LAHIR = "key_tanggal_lahir";
    private static final String STATUS_KAWIN = "status_kawin";
    private static final String JUMLAH_TANGGUNGAN = "jumlah_tanggungan";
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_KET_TITLE = "key_ket_title";
    private static final String INSKER = "insker";
    private static final String STATUS_RUMAH = "status_rumah";
    private static final String KEY_ALAMAT = "key_alamat";
    private static final String KEY_RT = "key_rt";
    private static final String KEY_RW = "key_rw";
    private static final String PROPINSI = "propinsi";
    private static final String KEY_KOTA = "key_kota";
    private static final String KEY_KECAMATAN  = "key_kecamatan";
    private static final String KEY_KELURAHAN = "key_kelurahan";
    private static final String KEY_KODE_POS = "key_kode_pos";
    private static final String STATUS_HUBUNGAN = "status_hubungan";
    private static final String NAMA_PENANGGUNG = "nama_penanggung";
    private static final String NO_KTP_PENANGGUNG = "no_ktp_penanggung";
    private static final String NAMA_IBU = "nama_ibu";
    private static final String KEY_NO_TELP = "key_no_telp";
    private static final String PHOTO_KTP = "photo_ktp";
    private static final String PHOTO_SELFI = "photo_selfi";
    private static final String PHOTO_PROFILE = "photo_profile";
    private static final String STATUS_TOPUP = "status_topup";
    private static final String SALDO = "saldo";
    private static final String SALDO_MAX = "saldo_max";

    private static final String NAMA_BANK = "nama_bank";
    private static final String MAKSIMAL_ANGSURAN = "maksimal_angsuran";
    private static final String CICILAN_PERTAHUN = "cicilan_pertahun";
    private static final String maksimal_pinjaman = "maksimal_pinjaman";
    private static final String maksimal_tenor = "maksimal_tenor";
    private static final String plafond_1 = "plafond_1";
    private static final String plafond_2 = "plafond_2";
    private static final String plafond_3 = "plafond_3";
    private static final String plafond_4 = "plafond_4";
    private static final String plafond_5 = "plafond_5";
    private static final String plafond_6 = "plafond_6";
    private static final String plafond_7 = "plafond_7";
    private static final String plafond_8 = "plafond_8";
    private static final String plafond_9 = "plafond_9";
    private static final String plafond_10 = "plafond_10";
    private static final String plafond_11 = "plafond_11";
    private static final String plafond_12 = "plafond_12";
    private static final String plafond_13 = "plafond_13";
    private static final String plafond_14 = "plafond_14";
    private static final String plafond_15 = "plafond_15";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    public SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_NIP, user.getNip());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.putString(KEY_NO_KTP, user.getNoKtp());
        editor.putString(KEY_NAMA, user.getNamaLengkap());
        editor.putString(KEY_AGAMA, user.getAgama());
        editor.putString(KEY_JK, user.getJenisKelamin());
        editor.putString(KEY_TEMPAT_LAHIR, user.getTempatLahir());
        editor.putString(KEY_TANGGAL_LAHIR, user.getTanggalLahir());
        editor.putString(STATUS_KAWIN, user.getStatusKawin());
        editor.putString(JUMLAH_TANGGUNGAN, user.getJumlahTanggungan());
        editor.putString(KEY_TITLE, user.getTitle());
        editor.putString(KEY_KET_TITLE, user.getKetTitle());
        editor.putString(INSKER, user.getInskerKerja());
        editor.putString(STATUS_RUMAH, user.getStatusRumah());
        editor.putString(KEY_ALAMAT, user.getAlamat());
        editor.putString(KEY_RT, user.getRt());
        editor.putString(KEY_RW, user.getRw());
        editor.putString(PROPINSI, user.getPropinsi());
        editor.putString(KEY_KOTA, user.getKota());
        editor.putString(KEY_KECAMATAN, user.getKecamatan());
        editor.putString(KEY_KELURAHAN, user.getKelurahan());
        editor.putString(KEY_KODE_POS, user.getKodePos());
        editor.putString(STATUS_HUBUNGAN, user.getStatusHubungan());
        editor.putString(NAMA_PENANGGUNG, user.getNamaPenanggung());
        editor.putString(NO_KTP_PENANGGUNG, user.getNoKtpPenanggung());
        editor.putString(NAMA_IBU, user.getNamaIbu());
        editor.putString(KEY_NO_TELP, user.getNoTelp());
        editor.putString(PHOTO_KTP, user.getImageKtp());
        editor.putString(PHOTO_SELFI, user.getImageSelfi());
        editor.putString(PHOTO_PROFILE, user.getImageProfile());
        editor.putInt(STATUS_TOPUP, user.getStatus_topup());
        editor.putInt(SALDO, user.getSaldo());
        editor.putInt(SALDO_MAX, user.getSaldo_max());

        editor.putString(NAMA_BANK, user.getNama_bank());
        editor.putInt(MAKSIMAL_ANGSURAN, user.getMaksimal_angsuran());
        editor.putInt(CICILAN_PERTAHUN, user.getCicilan_pertahun());
        editor.putInt(maksimal_pinjaman, user.getMaksimal_pinjaman());
        editor.putInt(maksimal_tenor, user.getMaksimal_tenor());
        editor.putInt(plafond_1, user.getPlafond_1());
        editor.putInt(plafond_2, user.getPlafond_2());
        editor.putInt(plafond_3, user.getPlafond_3());
        editor.putInt(plafond_4, user.getPlafond_4());
        editor.putInt(plafond_5, user.getPlafond_5());
        editor.putInt(plafond_6, user.getPlafond_6());
        editor.putInt(plafond_7, user.getPlafond_7());
        editor.putInt(plafond_8, user.getPlafond_8());
        editor.putInt(plafond_9, user.getPlafond_9());
        editor.putInt(plafond_10, user.getPlafond_10());
        editor.putInt(plafond_11, user.getPlafond_11());
        editor.putInt(plafond_12, user.getPlafond_12());
        editor.putInt(plafond_13, user.getPlafond_13());
        editor.putInt(plafond_14, user.getPlafond_14());
        editor.putInt(plafond_15, user.getPlafond_15());





        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NIP, null) != null;

    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_ID, 0),
                sharedPreferences.getString(KEY_NIP, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_PASSWORD, null),
                sharedPreferences.getString(KEY_NO_KTP, null),
                sharedPreferences.getString(KEY_NAMA, null),
                sharedPreferences.getString(KEY_AGAMA, null),
                sharedPreferences.getString(KEY_JK, null),
                sharedPreferences.getString(KEY_TEMPAT_LAHIR, null),
                sharedPreferences.getString(KEY_TANGGAL_LAHIR, null),
                sharedPreferences.getString(STATUS_KAWIN, null),
                sharedPreferences.getString(JUMLAH_TANGGUNGAN, null),
                sharedPreferences.getString(KEY_TITLE, null),
                sharedPreferences.getString(KEY_KET_TITLE, null),
                sharedPreferences.getString(INSKER, null),
                sharedPreferences.getString(STATUS_RUMAH, null),
                sharedPreferences.getString(KEY_ALAMAT, null),
                sharedPreferences.getString(KEY_RT, null),
                sharedPreferences.getString(KEY_RW, null),
                sharedPreferences.getString(PROPINSI, null),
                sharedPreferences.getString(KEY_KOTA, null),
                sharedPreferences.getString(KEY_KECAMATAN, null),
                sharedPreferences.getString(KEY_KELURAHAN, null),
                sharedPreferences.getString(KEY_KODE_POS, null),
                sharedPreferences.getString(STATUS_HUBUNGAN, null),
                sharedPreferences.getString(NAMA_PENANGGUNG, null),
                sharedPreferences.getString(NO_KTP_PENANGGUNG, null),
                sharedPreferences.getString(NAMA_IBU, null),
                sharedPreferences.getString(KEY_NO_TELP, null),
                sharedPreferences.getString(PHOTO_KTP, null),
                sharedPreferences.getString(PHOTO_SELFI, null),
                sharedPreferences.getString(PHOTO_PROFILE, null),
                sharedPreferences.getInt(STATUS_TOPUP, 0),
                sharedPreferences.getInt(SALDO, 0),
                sharedPreferences.getInt(SALDO_MAX, 0),

                sharedPreferences.getString(NAMA_BANK, ""),
                sharedPreferences.getInt(MAKSIMAL_ANGSURAN, 0),
                sharedPreferences.getInt(CICILAN_PERTAHUN, 0),
                sharedPreferences.getInt(maksimal_pinjaman, 0),
                sharedPreferences.getInt(maksimal_tenor, 0),
                sharedPreferences.getInt(plafond_1, 0),
                sharedPreferences.getInt(plafond_2, 0),
                sharedPreferences.getInt(plafond_3, 0),
                sharedPreferences.getInt(plafond_4, 0),
                sharedPreferences.getInt(plafond_5, 0),
                sharedPreferences.getInt(plafond_6, 0),
                sharedPreferences.getInt(plafond_7, 0),
                sharedPreferences.getInt(plafond_8, 0),
                sharedPreferences.getInt(plafond_9, 0),
                sharedPreferences.getInt(plafond_10, 0),
                sharedPreferences.getInt(plafond_11, 0),
                sharedPreferences.getInt(plafond_12, 0),
                sharedPreferences.getInt(plafond_13, 0),
                sharedPreferences.getInt(plafond_14, 0),
                sharedPreferences.getInt(plafond_15, 0)








        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
//        mCtx.startActivity(new Intent(mCtx, HalamanDepanActivity.class));
    }


}

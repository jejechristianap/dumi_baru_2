package com.fidac.dumi.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.fidac.dumi.HalamanDepanActivity;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "dumi_user";
    private static final String KEY_NIP = "key_nip";
    private static final String KEY_NAMA = "key_nama";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_ID = "key_id";
    private static final String KEY_NO_KTP = "key_no_ktp";
    private static final String KEY_AGAMA = "key_agama";
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_KET_TITLE = "key_ket_title";
    private static final String KEY_RT = "key_rt";
    private static final String KEY_RW = "key_rw";
    private static final String KEY_KELURAHAN = "key_kelurahan";
    private static final String KEY_KECAMATAN  = "key_kecamatan";
    private static final String KEY_KOTA = "key_kota";
    private static final String KEY_ALAMAT = "key_alamat";
    private static final String KEY_KODE_POS = "key_kode_pos";
    private static final String KEY_NO_TELP = "key_no_telp";





    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
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
        editor.putString(KEY_NAMA, user.getNamaLengkap());
        editor.putString(KEY_NO_KTP, user.getNoKtp());
        editor.putString(KEY_AGAMA, user.getAgama());
        editor.putString(KEY_TITLE, user.getTitle());
        editor.putString(KEY_KET_TITLE, user.getKetTitle());
        editor.putString(KEY_RT, user.getRt());
        editor.putString(KEY_RW, user.getRw());
        editor.putString(KEY_KELURAHAN, user.getKelurahan());
        editor.putString(KEY_KECAMATAN, user.getKecamatan());
        editor.putString(KEY_KOTA, user.getKota());
        editor.putString(KEY_ALAMAT, user.getAlamat());
        editor.putString(KEY_KODE_POS, user.getKodePos());
        editor.putString(KEY_NO_TELP, user.getNoTelp());
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
                sharedPreferences.getString(KEY_NAMA, null),
                sharedPreferences.getString(KEY_NO_KTP, null),
                sharedPreferences.getString(KEY_AGAMA, null),
                sharedPreferences.getString(KEY_TITLE, null),
                sharedPreferences.getString(KEY_KET_TITLE, null),
                sharedPreferences.getString(KEY_RT, null),
                sharedPreferences.getString(KEY_RW, null),
                sharedPreferences.getString(KEY_KELURAHAN, null),
                sharedPreferences.getString(KEY_KECAMATAN, null),
                sharedPreferences.getString(KEY_KOTA, null),
                sharedPreferences.getString(KEY_ALAMAT, null),
                sharedPreferences.getString(KEY_KODE_POS, null),
                sharedPreferences.getString(KEY_NO_TELP, null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, HalamanDepanActivity.class));
    }


}

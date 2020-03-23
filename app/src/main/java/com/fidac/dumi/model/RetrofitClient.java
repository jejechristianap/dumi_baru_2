package com.fidac.dumi.model;

import com.fidac.dumi.api.CekNipBknInterface;
import com.fidac.dumi.api.RegisterInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://app.ternak-burung.top/api/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getmInstance(){
        if(mInstance == null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }


    public RegisterInterface getUserRegis(){
        return retrofit.create(RegisterInterface.class);
    }

    public CekNipBknInterface getNip(){
        return retrofit.create(CekNipBknInterface.class);
    }


}

package com.fidac.dumi.model;

import com.fidac.dumi.api.CekNipBknInterface;
import com.fidac.dumi.api.CekUserExist;
import com.fidac.dumi.api.LoginInterface;
import com.fidac.dumi.api.RegisterInterface;
import com.fidac.dumi.api.UploadImageInterface;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://app.ternak-burung.top/api/";
    private static RetrofitClient mInstance;
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();

            return retrofit;
        }

        public CekNipBknInterface getNip(){
            return retrofit.create(CekNipBknInterface.class);
        }
        public LoginInterface userLogin(){
        return retrofit.create(LoginInterface.class);
    }
        public RegisterInterface createUser(){ return retrofit.create(RegisterInterface.class); }
        public UploadImageInterface uploadImage(){ return retrofit.create(UploadImageInterface.class); }
        public CekUserExist cekUser(){ return retrofit.create(CekUserExist.class); }
    }



    /*public static synchronized RetrofitClient getmInstance(){
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
    }*/




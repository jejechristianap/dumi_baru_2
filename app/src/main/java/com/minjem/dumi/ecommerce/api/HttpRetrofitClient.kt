@file:Suppress("DEPRECATION")

package com.mdi.stockin.ApiHelper


import com.minjem.dumi.ecommerce.api.BaseApiService
import com.minjem.dumi.ecommerce.api.UnsafeOkHttpClient
import com.minjem.dumi.retrofit.RetrofitClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException

object HttpRetrofitClient {


    val instanse : Retrofit by lazy {

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

//        val sslContext = SSLContext.getInstance("SSL")
//        var keyStore = readKeyStore()
//        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//        trustManagerFactory.init(keyStore)
//        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
//        keyManagerFactory.init(keyStore, "keystore_pass".toCharArray())
//        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), SecureRandom())
//        client.setSslSocketFactory(sslContext.socketFactory)

        val ok = UnsafeOkHttpClient.getUnsafeOkHttpClient()
        val unsafe = RetrofitClient.getUnsafeOkHttpClient()

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(60,TimeUnit.SECONDS)
            .writeTimeout(60,TimeUnit.SECONDS)
            .readTimeout(60,TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        Retrofit.Builder()
                .baseUrl("https://klikmbc.co.id/")
                .client(unsafe.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    val retrofit : BaseApiService get() = instanse.create(BaseApiService::class.java)

}
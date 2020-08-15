package com.minjem.dumi

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.minjem.dumi.retrofit.RetrofitClient
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Splash : Activity() {
    private val SPLASH_DISPLAY_LENGTH = 1000
    private val api = RetrofitClient
    val versionCode_ = BuildConfig.VERSION_CODE
    val versionName_ = BuildConfig.VERSION_NAME
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_dumi)
        getVersionApp()

        Handler().postDelayed({
            val mainIntent = Intent(this@Splash, HalamanDepanActivity::class.java)

            this@Splash.startActivity(mainIntent)
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())

    }

    private fun getVersionApp(){
        val TAG = "Version app"
        api.retro.versionApp.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val jsonObject = JSONObject(response.body()!!.string())
                    if (jsonObject.getBoolean("status")){
                        val jsonArray = JSONArray(jsonObject.getString("data"))
                        for (i in 0 until jsonArray.length()){
                            val appName = jsonArray.getJSONObject(i).getString("app_name")
                            val versionName = jsonArray.getJSONObject(i).getString("versionName")
                            val versionCode = jsonArray.getJSONObject(i).getInt("versionCode")
                            Log.d(TAG, "appName: $appName, versionName: $versionName, versionCode: $versionCode")
                            if (appName == "DUMI"){
                                if (versionCode == versionCode_){
                                    Log.d(TAG, "versionCodeApi: $versionCode | versionCodeApp: $versionCode_ | UPDATED")
                                } else {
                                    Log.d(TAG, "versionCodeApi: $versionCode | versionCodeApp: $versionCode_ | NEW UPDATE")
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, jsonObject.getString("message"))
                    }
                } else {
                    Log.d(TAG, "onResponse: ${response.message()}")
                }
            }

        })
    }
}
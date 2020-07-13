package com.minjem.dumi.presenter

import android.util.Log
import com.minjem.dumi.ecommerce.api.HttpRetrofitClient
import com.minjem.dumi.fragment.DigiSign
import com.minjem.dumi.response.RUser
import com.minjem.dumi.view.UserView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserPrestImp(user: DigiSign) : UserPrest {

    var userView: UserView? = null
    lateinit var api: HttpRetrofitClient

    init {
        userView = user
    }

    override fun data(nip: String, password: String) {
        api = HttpRetrofitClient
        api.retrofit.loginDumi(nip, password).enqueue(object : Callback<RUser> {
            override fun onFailure(call: Call<RUser>, t: Throwable) {
                t.message?.let { userView!!.error(it) }
                Log.e("Error", "ERROR")
            }

            override fun onResponse(call: Call<RUser>, response: Response<RUser>) {
                Log.d("Response", "NYAMPE")
                if (response.isSuccessful) {
                    Log.d("Response", "SUCCESS")
                    userView!!.response(response.body()!!)
                } else {
                    Log.e("Response", "ERROR")
                    userView!!.error("Response failure")
                }
            }
        })
    }
}

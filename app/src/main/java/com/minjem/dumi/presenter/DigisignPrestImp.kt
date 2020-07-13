package com.minjem.dumi.presenter

import com.minjem.dumi.ecommerce.api.HttpRetrofitClient
import com.minjem.dumi.response.RDigisign
import com.minjem.dumi.view.DigisignView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DigisignPrestImp(view : DigisignView) : DigisignPrest {
    var digiModel : DigisignView? = null

    init {
        digiModel = view
    }

    override fun data(nik: String, email: String) {
        val api = HttpRetrofitClient

        api.retrofit.cekAktivasi(nik,email).enqueue(object : Callback<RDigisign> {
            override fun onFailure(call: Call<RDigisign>, t: Throwable) {
                digiModel!!.digiError(t.message!!)
            }

            override fun onResponse(call: Call<RDigisign>, response: Response<RDigisign>) {
                if (response.isSuccessful){
                    digiModel!!.digiResponse(response.body()!!)
                } else {
                    digiModel!!.digiError("Koneksi terputus, Mohon Ulangi lagi.")
                }
            }
        })
    }
}
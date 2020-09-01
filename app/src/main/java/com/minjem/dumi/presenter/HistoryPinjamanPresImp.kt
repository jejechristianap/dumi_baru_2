package com.minjem.dumi.presenter

import com.minjem.dumi.response.HistoryResponse
import com.minjem.dumi.retrofit.RetrofitClient
import com.minjem.dumi.view.HistoryPinjamanView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryPinjamanPresImp(view: HistoryPinjamanView): HistoryPinjamanPrest {
    var historyModel: HistoryPinjamanView? = null

    init {
        historyModel = view
    }

    override fun history(nip: String) {
        val api = RetrofitClient

        api.retro.getPinjaman(nip).enqueue(object: Callback<HistoryResponse> {
            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                historyModel!!.historyPinjamanError(t.message!!)
            }

            override fun onResponse(call: Call<HistoryResponse>, response: Response<HistoryResponse>) {
                if (response.isSuccessful){
                    historyModel!!.historyPinjamanResponse(response.body()!!)
                } else {
                    historyModel!!.historyPinjamanError(response.message().toString())
                }

            }

        })
    }
}
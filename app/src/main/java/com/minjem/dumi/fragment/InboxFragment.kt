package com.minjem.dumi.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.minjem.dumi.util.CustomProgressDialog

import com.minjem.dumi.R
import com.minjem.dumi.api.StatusPinjamanInterface
import com.minjem.dumi.ecommerce.Helper.mProgress
import com.minjem.dumi.model.NotifikasiAdapter
import com.minjem.dumi.model.NotifikasiData
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.retrofit.RetrofitClient
import kotlinx.android.synthetic.main.fragment_inbox.view.*
import okhttp3.ResponseBody
import org.json.JSONArray

import org.json.JSONObject

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InboxFragment : Fragment() {
    lateinit var mContext : Context
    private var layoutManager: LinearLayoutManager? = null
    val list : MutableList<NotifikasiData> = ArrayList()
    private var progressDialog = CustomProgressDialog()
    internal lateinit var notifikasiAdapter: NotifikasiAdapter
    lateinit var mView: View
    lateinit var dProgress : Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_inbox, container, false)
        mContext = this.context!!
        dProgress = Dialog(mContext)
        /*(activity as AppCompatActivity).setSupportActionBar(mView.toolbarInbox)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)*/
        (activity as AppCompatActivity).setSupportActionBar(mView.toolbarInbox)
        mView.ctl.title = "Inbox"
//        TODO implement toolbar overflow below
        mView.ivYuhu.setOnClickListener {
            Snackbar.make(mView, "Yuhuuuuuuu", Snackbar.LENGTH_LONG).show()
        }


        loadBackdrop()

        list.clear()
        layoutManager = LinearLayoutManager(mContext)
        mView.notifikasi_rv.layoutManager = layoutManager
        notifikasiAdapter = NotifikasiAdapter(mContext, list)
        mView.notifikasi_rv.adapter = notifikasiAdapter

        mView.srlNotif.setOnRefreshListener {
            mView.srlNotif.isRefreshing = true
            refreshList()
        }

        getNotif()

        /*mView.srl.setOnRefreshListener {
            mView.srl.isRefreshing = true
            refreshList()
        }*/
        return mView
    }

    private fun loadBackdrop(){
        Glide.with(mContext).load(R.drawable.bg_beranda).into(mView.ivBgInbox)
    }

    private fun refreshList(){
        mView.srlNotif.isRefreshing = false
        list.clear()
        getNotif()
    }

/*    private fun refreshList(){
        //do processing to get new data and set your listview's adapter, maybe  reinitialise the loaders you may be using or so
        //when your data has finished loading, cset the refresh state of the view to false
        mView.srl.isRefreshing = false
        progressDialog.show(mCOntext, "Memuat Data..")
        list.clear()
        getNotif()
    }*/


    private fun getNotif() {
        val cek = RetrofitClient.getClient().create(StatusPinjamanInterface::class.java)

        mProgress(dProgress)
        val prefManager = SharedPrefManager.getInstance(activity!!.applicationContext).user
        val nip = prefManager.nip
        //        Toast.makeText(getActivity(), nip, Toast.LENGTH_SHORT).show();

        val call = cek.getNotif(nip)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val jsonObject = JSONObject(response.body()!!.string())
                    if (jsonObject.getBoolean("status")){
                        val jsonArray = JSONArray(jsonObject.getString("data"))
                        for (i in 0 until jsonArray.length()){
                            val id = jsonArray.getJSONObject(i).optInt("id")
                            val status = jsonArray.getJSONObject(i).optInt("id")
                            val judul = jsonArray.getJSONObject(i).getString("judul")
                            val isi = jsonArray.getJSONObject(i).getString("isi")
                            val waktu = jsonArray.getJSONObject(i).getString("waktu")

                            val data = NotifikasiData()
                            data.id = id
                            data.status = status
                            data.judul = judul
                            data.isi = isi
                            data.waktu = waktu
                            list.add(data)
                        }
                        notifikasiAdapter.filter(list)
                        mView.notifikasi_rv.adapter = notifikasiAdapter
                        notifikasiAdapter.notifyDataSetChanged()
                        dProgress.dismiss()
//                        mToast(mCOntext,"Notif..")
                    } else {
                        dProgress.dismiss()
//                        mToast(mCOntext,"Koneksi server gagal..")
                    }

                } else {
                    dProgress.dismiss()
//                    mToast(mCOntext,"Koneksi server gagal..")
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Fail", "onFailure: " + t.message)
                dProgress.dismiss()
            }
        })
    }

}

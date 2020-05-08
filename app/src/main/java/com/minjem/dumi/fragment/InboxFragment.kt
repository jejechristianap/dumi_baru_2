package com.minjem.dumi.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.minjem.dumi.R
import com.minjem.dumi.api.StatusPinjamanInterface
import com.minjem.dumi.model.NotifikasiAdapter
import com.minjem.dumi.model.NotifikasiData
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.retrofit.RetrofitClient

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InboxFragment : Fragment() {
    lateinit var mCOntext : Context
    private var layoutManager: LinearLayoutManager? = null
    var notifikasiData: MutableList<NotifikasiData>? = null
    private var data: MutableList<NotifikasiData>? = null

    internal lateinit var notifikasiAdapter: NotifikasiAdapter
    internal lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_inbox, container, false)
        mCOntext = this.context!!
        recyclerView = view.findViewById(R.id.notifikasi_rv)
        /*layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        notifikasiAdapter = new NotifikasiAdapter(getActivity(), data);
        recyclerView.setAdapter(notifikasiAdapter);*/

        //        getNotif();
        return view
    }

    override fun onStart() {
        super.onStart()
        data = ArrayList()
        getNotif()
    }

    private fun getNotif() {
        val cek = RetrofitClient.getClient().create(StatusPinjamanInterface::class.java)

        val pDialog = ProgressDialog(activity)
        pDialog.setMessage("Loading Data...")
        pDialog.show()
        val prefManager = SharedPrefManager.getInstance(activity!!.applicationContext).user
        val nip = prefManager.nip
        //        Toast.makeText(getActivity(), nip, Toast.LENGTH_SHORT).show();

        val call = cek.getNotif(nip)
        call.enqueue(object : Callback<NotifikasiResponse> {
            override fun onResponse(call: Call<NotifikasiResponse>, response: Response<NotifikasiResponse>) {
                pDialog.dismiss()
                notifikasiData = response.body()!!.data as MutableList<NotifikasiData>
                layoutManager = LinearLayoutManager(activity)
                recyclerView.layoutManager = layoutManager
                notifikasiAdapter = NotifikasiAdapter(mCOntext, notifikasiData!!)
                recyclerView.adapter = notifikasiAdapter
                notifikasiAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<NotifikasiResponse>, t: Throwable) {
                Log.d("Fail", "onFailure: " + t.message)
                pDialog.dismiss()
            }
        })
    }

    @SuppressLint("WrongConstant")
    private fun writeRecycler(response: String) {
        try {
            val obj = JSONObject(response)
            if (obj.getBoolean("status") == true) {
                val notifikasiDataList = ArrayList<NotifikasiData>()
                val jsonArray = obj.getJSONArray("data")
                for (i in 0 until jsonArray.length()) {
                    val notif = NotifikasiData()
                    val notifObj = jsonArray.getJSONObject(i)
                    notif.id = notifObj.getInt("id")
                    notif.token = notifObj.getString("token")
                    notif.judul = notifObj.getString("judul")
                    notif.isi = notifObj.getString("isi")
                    notif.status = notifObj.getInt("status")
                    notif.waktu = notifObj.getString("waktu")
                    notif.idNasabah = notifObj.getString("id_nasabah")
                    notifikasiDataList.add(notif)
                    Log.d("RecyclerView", "writeRecycler: $notif")
                }
                //                Toast.makeText(getActivity(), "Get data", Toast.LENGTH_SHORT).show();
                notifikasiAdapter = NotifikasiAdapter(mCOntext, notifikasiDataList)
                recyclerView.adapter = notifikasiAdapter
                recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }
}

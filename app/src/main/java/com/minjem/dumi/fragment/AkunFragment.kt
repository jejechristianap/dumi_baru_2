package com.minjem.dumi.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.minjem.dumi.HalamanDepanActivity
import com.minjem.dumi.R
import com.minjem.dumi.akun.*
import com.minjem.dumi.ecommerce.ECommerceActivity
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.model.User
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_akun.view.*
import java.util.*

class AkunFragment : Fragment() {
    private var apiPhotoPath: String? = null
    private var photoIv: ImageView? = null
    private var prefManager: User? = null
    lateinit var v: View
    lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_akun, container, false)
        mContext = this.activity!!
        v.tvNama.text = SharedPrefManager.getInstance(mContext).user.namaLengkap

        photoIv = v.findViewById(R.id.photo_profile)
        v.photo_profile.setOnClickListener(View.OnClickListener { v: View? ->
            if (TextUtils.isEmpty(SharedPrefManager.getInstance(mContext).user.imageProfile)) {
                Toast.makeText(mContext, "Belum ada foto profile", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val intent = Intent(mContext, ECommerceActivity::class.java)
            intent.putExtra("fragment", "profile")
            startActivity(intent)
        })

        /*Rincian Akun*/
        v.rincian_akun_ll.setOnClickListener {
            val i = Intent(activity, RincianAkunActivity::class.java)
            i.putExtra("photo_profile", apiPhotoPath)
            startActivity(i)
        }
        v.ubah_sandi_ll.setOnClickListener { startActivity(Intent(activity, UbahSandiActivity::class.java)) }

        /*Kebijakan privasi*/
        v.kebijakan_privasi_ll.setOnClickListener {  startActivity(Intent(activity, KebijakanPrivasiActivity::class.java)) }

        /*Pusat Bantuan*/
        v.pusat_bantua_ll.setOnClickListener {  startActivity(Intent(activity, PusatBantuanActivity::class.java)) }

        /*Disclaimer*/
        v.disclaimer_ll.setOnClickListener {  startActivity(Intent(activity, DisclaimerActivity::class.java)) }

        /*Logout Button*/
        v.keluar_button.setOnClickListener {
           activity!!.finish()
            SharedPrefManager.getInstance(mContext).logout()
            startActivity(Intent(activity, HalamanDepanActivity::class.java))
        }
        return v
    }

    override fun onStart() {
        super.onStart()
        apiPhotoPath = SharedPrefManager.getInstance(mContext).user.imageProfile
        Log.d("Photo_PP", apiPhotoPath.toString())
        val reqOption = RequestOptions().signature(ObjectKey(System.currentTimeMillis()))
        Glide.with(mContext)
                .load(apiPhotoPath)
                .error(R.drawable.layout_round_corner_beranda)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .transform(CircleCrop(), RoundedCorners(16))
                .into(v.photo_profile)
    }
}
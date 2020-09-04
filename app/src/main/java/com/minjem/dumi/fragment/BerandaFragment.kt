package com.minjem.dumi.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mdi.stockin.ApiHelper.RecyclerItemClickListener
import com.minjem.dumi.PelengkapanRegularActivity
import com.minjem.dumi.R
import com.minjem.dumi.adapter.EcommerceMenuAdapter
import com.minjem.dumi.adapter.MinPinTenAdapter
import com.minjem.dumi.api.GetBungaInterface
import com.minjem.dumi.api.StatusPinjamanInterface
import com.minjem.dumi.dataclass.DataEcommerce
import com.minjem.dumi.dataclass.PlafondKreditData
import com.minjem.dumi.ecommerce.Helper.mProgress
import com.minjem.dumi.ecommerce.Helper.sBar
import com.minjem.dumi.ecommerce.Helper.spm
import com.minjem.dumi.jenispinjaman.PinjamanKilatActivity
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.retrofit.RetrofitClient
import com.minjem.dumi.util.KeyboardUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.bottom_sheet_ecommerce_menu.view.*
import kotlinx.android.synthetic.main.dialog_bottom_hitung.view.*
import kotlinx.android.synthetic.main.fragment_beranda.*
import kotlinx.android.synthetic.main.fragment_beranda.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BerandaFragment : Fragment() /*, Toolbar.OnMenuItemClickListener*/{
    private var saldoUser = 0
    lateinit var mView : View
    lateinit var mContext : Context
    lateinit var dView : View
    lateinit var eAdapter: EcommerceMenuAdapter
    private var minPinjaman = 1000000
    private val BIAYA_TRANSFER = 6500f
    private var getBunga = 0f
    private var bunga = 0f
    private var admin = 0f
    private var sisa = 0f
    private var asuransi = 0f
    private var angsuran = 0f
    private var getAdmin = 0f
    private var getAsur12 = 0f
    private var getAsur24 = 0f
    private var getAsur36 = 0f
    private var npTenor = 1
    private var npTujuan = "Renovasi Rumah"
    lateinit var localID: Locale
    lateinit var formatRp: NumberFormat
    private val listPlafond: MutableList<PlafondKreditData> = ArrayList()
    private lateinit var plafondAdapater: MinPinTenAdapter
    private lateinit var loadingP: Dialog
    lateinit var editor: SharedPreferences.Editor
    lateinit var pref: SharedPreferences
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>


    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_beranda, container, false)
        mContext = this.context!!
        initView()
        getBunga()
        initOnTouch()
        rvClickTenor()
        rvClickMenu()


        KeyboardUtils.addKeyboardToggleListener(activity) { isVisible ->
            Log.d("keyboard", "keyboard visible: $isVisible")
            /*if (isVisible){
                mView.persistent_bottom_sheet.visibility = View.GONE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                mView.persistent_bottom_sheet.visibility = View.VISIBLE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }*/
        }


        /*val toolbar = activity!!.findViewById<View>(R.id.toolbarMain) as Toolbar
        toolbar.inflateMenu(R.menu.menu_toolbar_main)
        toolbar.setOnMenuItemClickListener(this)*/
        /*saldo()
        val network = InternetConnection(mContext)
        network.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (isConnected){
                Log.d("Network", "onCreateView: $isConnected")
            } else {
                dialogTkb("connection")
            }
        })*/
        return mView
    }

    private fun initPlafondView(){
//         Plafondview adapter
//        val values: List<IntArray> = ArrayList(2)
        val lp = arrayListOf(spm(mContext).plafond_1,
                spm(mContext).plafond_2, spm(mContext).plafond_3,
                spm(mContext).plafond_4, spm(mContext).plafond_5,
                spm(mContext).plafond_6, spm(mContext).plafond_7,
                spm(mContext).plafond_8, spm(mContext).plafond_9,
                spm(mContext).plafond_10, spm(mContext).plafond_11,
                spm(mContext).plafond_12, spm(mContext).plafond_13,
                spm(mContext).plafond_14, spm(mContext).plafond_15
        )
        val lb = arrayListOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)

        for (i in 0 until 15){
            if (lp[i] != 0){
                val pkd = PlafondKreditData()
                pkd.plafond = lb[i] * 12
                pkd.minimal_pinjaman =lp[i]
                listPlafond.add(pkd)
            }

            plafondAdapater.filter(listPlafond)
            mView.rvMaksimalPinjaman.adapter = plafondAdapater
            plafondAdapater.notifyDataSetChanged()
        }
        Log.d("ArrayList >>>>>>>>>>>>>>>>>>>> ", listPlafond.toString())
        Log.d("ArrayList LP >>>>>>>>>>>>>>>>>>>> ", lp.toString())
        Log.d("ArrayList Lb >>>>>>>>>>>>>>>>>>>> ", lb.toString())
    }

    private fun initView(){
        bottomSheetBehavior = BottomSheetBehavior.from<LinearLayout>(mView.persistent_bottom_sheet)

        if (spm(mContext).nama_bank == "BNI"){
            mView.rvMaksimalPinjaman.visibility = View.VISIBLE
            mView.llPinjaman.visibility = View.GONE
            bottomSheetBehavior.peekHeight = activity!!.resources.getDimension(R.dimen._180sdp).toInt()
//            mView.rvMinimalPinjaman.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            mView.rvMaksimalPinjaman.layoutManager = GridLayoutManager(mContext, 2)
            plafondAdapater = MinPinTenAdapter(mContext, listPlafond)
            mView.rvMaksimalPinjaman.adapter = plafondAdapater
            plafondAdapater.notifyDataSetChanged()
            initPlafondView()
        } else {
            bottomSheetBehavior.peekHeight = activity!!.resources.getDimension(R.dimen._90sdp).toInt()
            mView.llPinjaman.visibility = View.VISIBLE
            mView.rvMaksimalPinjaman.visibility = View.GONE
        }
        loadingP = Dialog(mContext)


//        mView.gvMenuUtama.layoutManager = GridLayoutManager(mContext, 4)
        mView.rvEcommerce.layoutManager = GridLayoutManager(mContext, 4)
        eAdapter = EcommerceMenuAdapter(mContext, generateMenu())
        mView.rvEcommerce.adapter = eAdapter
//        mView.gvMenuUtama.adapter = eAdapter


        mView.cetJumlahPinjaman.setDecimals(false)
        mView.cetJumlahPinjaman.setSeparator(".")
        mView.cetJumlahPinjaman.setText("0")


        localID = Locale("in", "ID")
        formatRp = NumberFormat.getCurrencyInstance(localID)

        when(SharedPrefManager.getInstance(mContext).user.nama_bank){
            "MANTAP" -> {
                mView.npTenorPinjaman.minValue = 1
                mView.npTenorPinjaman.maxValue = 180
            }
            "BNI" -> {
                mView.npTenorPinjaman.minValue = 1
                mView.npTenorPinjaman.maxValue = SharedPrefManager.getInstance(mContext).user.maksimal_tenor * 12
            }
        }

        mView.npTujuanPinjaman.minValue = 0
        mView.npTujuanPinjaman.maxValue = PinjamanKilatActivity.TUJUAN_PINJAMAN.size - 1
        mView.npTujuanPinjaman.displayedValues = PinjamanKilatActivity.TUJUAN_PINJAMAN

        mView.npTenorPinjaman.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("NumberPicker", "initTouch: $picker, $oldVal, $newVal")
            npTenor = newVal
        }

        mView.npTujuanPinjaman.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("NumberPicker", "initTouch: $picker, $oldVal, ${PinjamanKilatActivity.TUJUAN_PINJAMAN[newVal]}")
            npTujuan = PinjamanKilatActivity.TUJUAN_PINJAMAN[newVal]
        }

        mView.sbPinjaman.max = 50


    }

    private fun generateMenu(): List<DataEcommerce> {
        print("masuk ====================================================== rv")
        return listOf(DataEcommerce("PULSA", R.drawable.ic_pulsa),
                DataEcommerce("PLN", R.drawable.ic_token_pln),
                DataEcommerce("GOPAY", R.drawable.ic_gopay),
                DataEcommerce("OVO", R.drawable.ic_ovo),
                DataEcommerce("HOTEL", R.drawable.ic_hotel),
                DataEcommerce("PESAWAT", R.drawable.ic_pesawat),
                DataEcommerce("KERETA", R.drawable.ic_kereta),
                DataEcommerce("PDAM", R.drawable.ic_drop),
                DataEcommerce("BPJS", R.drawable.ic_bpjs),
                DataEcommerce("TELKOM", R.drawable.ic_telkom),
                DataEcommerce("GAS PGN", R.drawable.ic_pgn_gas),
                DataEcommerce("TV KABEL", R.drawable.ic_tv_kabel),
                DataEcommerce("INTERNET", R.drawable.ic_internet),
                DataEcommerce("EMONEY", R.drawable.ic_emoney),
                DataEcommerce("BNI", R.drawable.ic_bni),
                DataEcommerce("LINK AJA", R.drawable.ic_linkaja),
                DataEcommerce("DANA", R.drawable.ic_dana),
                DataEcommerce("GRAB", R.drawable.ic_grab),
                DataEcommerce("VOUCHER GAME", R.drawable.ic_voucher_game),
                DataEcommerce("PELNI", R.drawable.ic_pelni),
                DataEcommerce("FERRY", R.drawable.ic_ferry),
                DataEcommerce("BUS", R.drawable.ic_bus_travel)
        )
    }

    private fun rvClickTenor(){
        mView.rvMaksimalPinjaman.addOnItemTouchListener(RecyclerItemClickListener(mContext, object: RecyclerItemClickListener.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
//                sBar(mView, plafondAdapater.list[position].plafond.toString())
                hitungPinjaman(plafondAdapater.list[position].plafond!!, plafondAdapater.list[position].minimal_pinjaman!!.toFloat())
            }
        }))
    }

    private fun rvClickMenu(){
        mView.rvEcommerce.addOnItemTouchListener(RecyclerItemClickListener(mContext, object: RecyclerItemClickListener.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                sBar(mView, eAdapter.list[position].title.toString())
            }
        }))
    }

    private fun initOnTouch() {
        mView.bHitungPinjaman.setOnClickListener {
            if (cetJumlahPinjaman.cleanIntValue == 0){
                mView.cetJumlahPinjaman.error = "Silakan tentukan jumlah pinjaman"
                return@setOnClickListener
            } else {
                mView.cetJumlahPinjaman.error = null
            }
            hitungPinjaman(0, 0f)
        }

        mView.sbPinjaman.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var progressValue = 0
            val base = 2000000
            var x = 0

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progressValue = progress
                x = progressValue * base
                mView.cetJumlahPinjaman.setText(x.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                x = progressValue * base
                mView.cetJumlahPinjaman.setText(x.toString())
            }

        })

        /*mView.persistentBtn.setOnClickListener{
            expandCollapseSheet()
        }*/

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, state: Int) {
                print(state)
                when (state) {

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        persistentBtn.text = "Show Bottom Sheet"
                    }
                    BottomSheetBehavior.STATE_EXPANDED ->
                        persistentBtn.text = "Close Bottom Sheet"
                    BottomSheetBehavior.STATE_COLLAPSED ->
                        persistentBtn.text = "Show Bottom Sheet"
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                    }
                }
            }

        })

        /*mView.cardKilat.setOnClickListener { goTo("kilat") }
        mView.kilatButton.setOnClickListener { goTo("kilat") }
        mView.cardRegular.setOnClickListener { goTo("regular") }
        mView.regularButton.setOnClickListener { goTo("regular") }
        mView.cardUltimate.setOnClickListener { goTo("ultimate") }
        mView.bUltimate.setOnClickListener { goTo("ultimate") }
        mView.riwayatButton.setOnClickListener { goTo("riwayat") }
        mView.icPulsa.setOnClickListener { goTo("pulsa") }
        mView.textPulsa.setOnClickListener { goTo("pulsa") }
        mView.icPln.setOnClickListener { goTo("pln") }
        mView.textPln.setOnClickListener { goTo("pln") }
        mView.icGopay.setOnClickListener { goTo("na") }
        mView.textGopay.setOnClickListener { goTo("na") }
        mView.icOvo.setOnClickListener { goTo("na") }
        mView.textOvo.setOnClickListener { goTo("na") }
        mView.icHotel.setOnClickListener { goTo("na") }
        mView.textHotel.setOnClickListener { goTo("na") }
        mView.icPesawat.setOnClickListener { goTo("na") }
        mView.textPesawat.setOnClickListener { goTo("na") }
        mView.icKereta.setOnClickListener { goTo("na") }
        mView.textKereta.setOnClickListener { goTo("na") }
        mView.icSemua.setOnClickListener { goTo("semua") }
        mView.textSemua.setOnClickListener { goTo("semua") }
        mView.tkb.setOnClickListener {
            dialogTkb("tkb")
        }*/


    }

    private fun expandCollapseSheet() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            persistentBtn.text = "Close Bottom Sheet"
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            persistentBtn.text = "Show Bottom Sheet"
        }
    }

    lateinit var v: View
    private fun hitungDialog(angsuran: Double, admin: Double, asuransi: Double, trf: Double, sisa: Double){
        dView = layoutInflater.inflate(R.layout.dialog_bottom_hitung, null)
        val dialog = BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme)
        dialog.setContentView(dView)
        dialog.show()

        dView.tvAngsuranDialog.text = formatRp.format(angsuran)
        dView.tvAdminDialog.text = formatRp.format(admin)
        dView.tvAsuransiDialog.text = formatRp.format(asuransi)
        dView.tvTrfBankDialog.text = formatRp.format(trf)
        dView.tvJumlahTerimaDialog.text = formatRp.format(sisa)

        v = dView

        dView.bAjukanSekarang.setOnClickListener {
            pinjaman
        }
        dView.bBatal.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun getBunga(){
        mProgress(loadingP)
        RetrofitClient.client.create(GetBungaInterface::class.java)
                .bunga.enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("Get Bunga", "onFailure: ${t.message.toString()}")
                        sBar(mView, "Koneksi server terputus!")
                        loadingP.dismiss()
                        getBunga()
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful){
                            val jsonObject = JSONObject(response.body()!!.string())
                            if (jsonObject.getBoolean("status")){
                                val jsonArray = JSONArray(jsonObject.getString("data"))
                                for (i in 0 until jsonArray.length()){
                                    if (jsonArray.getJSONObject(i).getInt("id_bunga") == 1 &&
                                            SharedPrefManager.getInstance(mContext).user.inskerKerja.contains("Badan Kepegawaian Negara")){
                                        admin_asn_ultimate.text = "Biaya Administrasi 1%"
                                        asuransi_asn_ultimate.text = "Biaya Asuransi 1%"
                                        getBunga = jsonArray.getJSONObject(i).getDouble("bunga").toFloat()
                                        getAdmin = jsonArray.getJSONObject(i).getDouble("biaya_admin").toFloat()
                                        getAsur12 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_12").toFloat()
                                        getAsur24 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_24").toFloat()
                                        getAsur36 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_36").toFloat()
                                    } else if (jsonArray.getJSONObject(i).getInt("id_bunga") == 2 && !SharedPrefManager.getInstance(mContext).user.inskerKerja.contains("Badan Kepegawaian Negara")){
                                        admin_asn_ultimate.text = "Biaya Administrasi 2%"
                                        asuransi_asn_ultimate.text = "Biaya Asuransi 2%"
                                        getBunga = jsonArray.getJSONObject(i).getDouble("bunga").toFloat()
                                        getAdmin = jsonArray.getJSONObject(i).getDouble("biaya_admin").toFloat()
                                        getAsur12 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_12").toFloat()
                                        getAsur24 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_24").toFloat()
                                        getAsur36 = jsonArray.getJSONObject(i).getDouble("biaya_asuransi_36").toFloat()
                                    }
                                }
                                loadingP.dismiss()

                            } else {
                                sBar(mView, "Gagal mengambil data!")
                                loadingP.dismiss()
                            }

                        } else {
                            loadingP.dismiss()
                            sBar(mView, response.message().toString())
                        }
                    }

                })
    }



    private val pinjaman: Unit
        get(){
            val status = RetrofitClient.client.create(StatusPinjamanInterface::class.java)
                    .getPinjaman(SharedPrefManager.getInstance(mContext).user.nip)
                    .enqueue(object : Callback<ResponseBody>{
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.d("Get Bunga", "onFailure: ${t.message.toString()}")
                            sBar(v, "Koneksi server terputus!")
                            pinjaman
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful){
                                val jsonObject = JSONObject(response.body()!!.string())
                                if (jsonObject.getBoolean("status")){
                                    val jsonArray = JSONArray(jsonObject.getString("data"))
                                    val statusPinjaman = ArrayList<Int>()
                                    if (!jsonArray.isNull(0)){
                                        for (i in 0 until jsonArray.length()) {
                                            val jsonObject = jsonArray.getJSONObject(i)
                                            statusPinjaman.add(jsonObject.getInt("status"))
                                        }
                                        when {
                                            statusPinjaman.contains(1) -> {
//                                                sBar(v, "Anda sudah mengajukan pinjaman. Mohon menunggu info dari kami.")
                                                Toasty.warning(mContext, "Anda sudah mengajukan pinjaman. Mohon menunggu info dari kami.", Toast.LENGTH_LONG, true).show()
                                            }
                                            statusPinjaman.contains(2) -> {
//                                                sBar(v, "Masih ada tagihan yang belum selesai, terima kasih.")
                                                Toasty.warning(mContext, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_LONG, true).show()

                                            }
                                            statusPinjaman.contains(4) -> {
//                                                sBar(v, "Masih ada tagihan yang belum selesai, terima kasih.")
                                                Toasty.warning(mContext, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_LONG, true).show()
                                            }
                                            statusPinjaman.contains(5) -> {
//                                                sBar(v, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_SHORT)
                                                Toasty.warning(mContext, "Masih ada tagihan yang belum selesai, terima kasih.", Toast.LENGTH_LONG, true).show()
                                            }
                                            else -> {
                                                ajukanPinjaman()
//                                                sBar(v, "Bisa ajukan")
//                                                Toasty.warning(mContext, "Bisa ajukan", Toast.LENGTH_LONG, true).show()
                                            }
                                        }
                                    } else {
                                        sBar(v, "Bisa ajukan")
                                        ajukanPinjaman()
                                    }
                                } else {
                                    sBar(v, "Gagal mengambil data")
                                }
                            } else {
                                sBar(v, "Koneksi server terputus!")

                            }
                        }

                    })

        }

    private fun hitungPinjaman(tenor: Int, jumlah: Float){
        val pokok: Float
        var jmlPinjaman = 0f
        when{
            tenor != 0 -> {
                npTenor = tenor
                jmlPinjaman = jumlah
            }
            tenor == 0 -> {
                jmlPinjaman = mView.cetJumlahPinjaman.cleanDoubleValue.toFloat()
            }
        }
        pokok =  jmlPinjaman / npTenor
        bunga = jmlPinjaman * getBunga / PinjamanKilatActivity.JUMLAH_BULAN_1_TAHUN
        angsuran = pokok + bunga
        admin = jmlPinjaman * getAdmin
        asuransi = jmlPinjaman * getAsur12
        val totalPengurangan = admin + asuransi + PinjamanKilatActivity.BIAYA_TRANSFER
        sisa = jmlPinjaman - totalPengurangan
        Log.d("Asuransi", ": $getAsur12")
        Log.d("Hitung Pinjaman",
                "\nPokok: $pokok\n" +
                        "Tenor: $npTenor" +
                        "Angsuran: $angsuran\n" +
                        "Admin: $admin\n" +
                        "Asuransi: $asuransi\n" +
                        "Jumlah Terima: $sisa")

        hitungDialog(angsuran.toDouble(), admin.toDouble(), asuransi.toDouble(), PinjamanKilatActivity.BIAYA_TRANSFER.toDouble(), sisa.toDouble())

        /*tvAngsuranPerbulanUltimate.text = formatRp.format(angsuran.toDouble())
        tvAdminUltimate.text = formatRp.format(admin.toDouble())
        tvAsuransiUltimate.text = formatRp.format(asuransi.toDouble())
        tvTrfUltimate.text = formatRp.format(PinjamanKilatActivity.BIAYA_TRANSFER.toDouble())
        tvJumlahTerimaUltimate.text = formatRp.format(sisa.toDouble())*/
//        dialogTkb()
    }

    private fun ajukanPinjaman() {
//        val user = SharedPrefManager.getInstance(applicationContext).user
        val nip = spm(mContext).nip
//        val tujuan = tujuanSpinner!!.selectedItem.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val c = Calendar.getInstance()
        val today = c.time
        val tglPinjam = dateFormat.format(today)
        c.add(Calendar.MONTH, npTenor)
        val dueDate = c.time
        val tglAkhirPinjam = dateFormat.format(dueDate)
        pref = mContext.getSharedPreferences("ajukanPinjaman", 0) // 0 - for private mode
        editor = pref.edit()
        Log.d("Pinjaman",
                """
                    nip: $nip
                    Pinjaman: ${mView.cetJumlahPinjaman.cleanIntValue}
                    Plafond: $npTenor
                    bunga: $getBunga
                    Bunga: $bunga
                    Admin: $admin
                    Angsuran: $angsuran
                    TrfBank: $BIAYA_TRANSFER
                    Tujuan: $tujuan
                    TglPinjam: $tglPinjam
                    AkhirTgl: $tglAkhirPinjam
                    Sisa: $sisa
                    asurans: $asuransi
                    """.trimIndent())
        editor.putString("nip", nip)
        editor.putFloat("pinjaman", mView.cetJumlahPinjaman.cleanDoubleValue.toFloat())
        editor.putInt("lamaPinjaman", npTenor)
        editor.putFloat("bunga", bunga)
        editor.putFloat("admin", admin)
        editor.putFloat("angsuran", angsuran)
        editor.putFloat("diterima", sisa)
        editor.putString("tujuan", npTujuan)
        editor.putString("tglMulai", tglPinjam)
        editor.putString("tglAkhir", tglAkhirPinjam)
        editor.putFloat("asuransi", asuransi)
        editor.apply()
        startActivity(Intent(mContext, PelengkapanRegularActivity::class.java))
    }



    private fun goTo(item: String) {
        /*val intent: Intent
        when (item) {
            "kilat" -> startActivity(Intent(activity, PinjamanKilatActivity::class.java))
            "regular" -> startActivity(Intent(activity, PinjamanRegularActivity::class.java))
            "ultimate" -> startActivity(Intent(activity, PinjamanUltimateActivity::class.java))
            "riwayat" -> startActivity(Intent(activity, RiwayatView::class.java))
            "pulsa" -> {
                intent = Intent(activity, ECommerceActivity::class.java)
                intent.putExtra("fragment", "pulsa")
                startActivity(intent)
            }
            "pln" -> {
                intent = Intent(activity, ECommerceActivity::class.java)
                intent.putExtra("fragment", "pln")
                startActivity(intent)
            }
            "flight" -> {
                intent = Intent(activity, ECommerceActivity::class.java)
                intent.putExtra("fragment", "flight")
                startActivity(intent)
            }
            "pinjaman" -> {
                intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("fragment", "pinjaman")
            }
            "semua" -> startActivity(Intent(activity, SemuaEcommerceActivity::class.java))
            else -> Toast.makeText(activity, "Tunggu update kami selanjutanya...", Toast.LENGTH_SHORT).show()
        }*/
    }

    /*override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.actionLogout -> {
//                getHistoryPinjaman()
                SharedPrefManager.getInstance(mContext).logout()
                activity!!.finish()
                startActivity(Intent(mContext, HalamanDepanActivity::class.java))
                return true
            }

        }
        return false
    }*/

    /*private fun showBottomDialog(){
           val bsf = BottomSheetFragment()
           bsf.show(fragmentManager!!, bsf.tag)
       }*/
    /*private fun saldo(){
            val idUser = SharedPrefManager.getInstance(activity).user.id
            val nipBaru : String? = SharedPrefManager.getInstance(activity).user.nip
            val api = RetrofitClient.client.create(BaseApiService::class.java)
            val call = api.getSaldo(idUser, nipBaru.toString(), USERNAME, PASSWORD)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            if (jsonObject.getBoolean("status")) {
                                val jsonArray = JSONArray(jsonObject.getString("data"))
                                for (i in 0 until jsonArray.length()) {
                                    saldoUser = jsonArray.getJSONObject(i).optInt("saldo", 0)
                                }
                                Log.d("SaldoUser", "Saldo: $saldoUser")
                                localID = Locale("in", "ID")
                                formatRp = NumberFormat.getCurrencyInstance(localID!!)
                                mView.saldoPayLater.text = formatRp.format(saldoUser)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    val base = "Rp0"
                    mView.saldoPayLater.text = base
                    dialogTkb("connection")
                }
            })
        }*/
}
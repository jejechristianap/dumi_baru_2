package com.minjem.dumi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.minjem.dumi.ecommerce.Helper.KOLOM
import com.minjem.dumi.ecommerce.Helper.mToast
import kotlinx.android.synthetic.main.activity_rekening_bank.*
import org.json.JSONArray
import org.json.JSONException
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList


class RekeningBankActivity : AppCompatActivity() {
    private val bankList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rekening_bank)
        getCityFromJsonData()
        setAutocompleteSpinnerList(bankList)

        backRekeningBank.setOnClickListener { finish() }

        var check = false

        ctv.setOnCheckedChangeListener { _, isChecked ->
            check = isChecked
        }

        bLanjutRekening.setOnClickListener {
            if (actvNamaBank.text.toString().isEmpty()){
                actvNamaBank.error = KOLOM
                actvNamaBank.requestFocus()
                return@setOnClickListener
            }

            if (etNamaRekening.text.toString().isEmpty()){
                etNamaRekening.error = KOLOM
                etNamaRekening.requestFocus()
                return@setOnClickListener
            }

            if (etNoRekening.text.toString().isEmpty()){
                etNoRekening.error = KOLOM
                etNamaRekening.requestFocus()
                return@setOnClickListener
            }

            if (!check){
                mToast(this, "Mohon centrang pernyataan di bawah nomor rekening!")
                return@setOnClickListener
            }

            moveTo()

        }


//        val JSONfile = File(getExternalFilesDir(null)!!.path, "nama_bank.json")

        /*val out: OutputStream? = null
        try {
            out = FileOutputStream(JSONfile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            val writer = JsonWriter(OutputStreamWriter(out, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }*/
    }

    private fun moveTo(){
        val sp = getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("namaBank", actvNamaBank.text.toString())
        editor.putString("namaRekening", etNamaRekening.text.toString())
        editor.putString("noRekening", etNoRekening.text.toString())
        editor.apply()
        startActivity(Intent(this, FotoKtpActivity::class.java))
    }

    private fun getJsonDataFromLocalFile(): String? {
        return try {
            val inputStream: InputStream = assets.open("nama_bank.json")
            val fileSize: Int = inputStream.available()
            val buffer = ByteArray(fileSize)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    private fun getCityFromJsonData() {
        try {
//            val jsonObject = JSONObject(getJsonDataFromLocalFile())
            val array = JSONArray(getJsonDataFromLocalFile())
            for (i in 0 until array.length()) {
                val bank = array.getJSONObject(i).getString("name")
                bankList.add(bank)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setAutocompleteSpinnerList(arrayList: ArrayList<String>) {

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayList)
        actvNamaBank.setAdapter(adapter)
        hideKeyBoard()
    }
    private fun hideKeyBoard() {
        actvNamaBank.setOnItemClickListener { _, _, _, _ ->
            val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }
}
package com.minjem.dumi.ecommerce

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.minjem.dumi.R
import kotlinx.android.synthetic.main.ecommerce_flight.*
import kotlinx.android.synthetic.main.ecommerce_flight.view.*
import kotlinx.android.synthetic.main.ecommerce_flight.view.switchIv
import java.text.SimpleDateFormat
import java.util.*

class FlightFragment : Fragment() {
    lateinit var mView : View
    lateinit var mContext : Context
    var cal = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        mView = layoutInflater.inflate(R.layout.ecommerce_flight,container,false)
        mContext = this.context!!

        updateDateInView()
        initOnTouch()



        return mView
    }

    private fun initOnTouch(){


        mView.radio_group_flight.setOnCheckedChangeListener { _, checkId ->
            val radio: RadioButton = mView.findViewById(checkId)
            Toast.makeText(mContext, "on check: ${radio.text}", Toast.LENGTH_LONG).show()
            if(radio.text == "Round Trip"){
                mView.roundTripCl.visibility = View.VISIBLE
            } else {
                mView.roundTripCl.visibility = View.GONE
            }

        }

        mView.switchIv.setOnClickListener {
            val dariKota = dariTv.text.toString()
            val tujuanKota = tujuanTv.text.toString()
            dariTv.text = tujuanKota
            tujuanTv.text = dariKota
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        mView.tglBerangkat.setOnClickListener {
            DatePickerDialog(mContext,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        mView.tglKeberangkatan!!.setOnClickListener {
            DatePickerDialog(mContext,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateDateInView() {
        val local = Locale("in", "ID")
        val myFormat = "EEEE, d MMM yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, local)
        mView.tglBerangkat!!.text = sdf.format(cal.time)
    }
}
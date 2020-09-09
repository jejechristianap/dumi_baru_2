package com.minjem.dumi.view

import android.view.View
import com.minjem.dumi.dataclass.PlafondKreditData

interface RcOnClick {
    fun onPlafondClick(view: View, plafond: PlafondKreditData)
}
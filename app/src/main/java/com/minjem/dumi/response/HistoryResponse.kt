package com.minjem.dumi.response

import com.minjem.dumi.dataclass.HistoryData

class HistoryResponse {
    val status : Boolean = false
    val message : String ? = null
    val data : List<HistoryData>? = null
}
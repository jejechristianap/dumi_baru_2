package com.minjem.dumi.response

import com.minjem.dumi.dataclass.NotifikasiData

class NotifikasiResponse {
    var status : Boolean = false
    var message : String? = null
    var data : List<NotifikasiData>? = null
}
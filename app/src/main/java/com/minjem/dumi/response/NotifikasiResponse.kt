package com.minjem.dumi.response

import com.minjem.dumi.model.NotifikasiData

class NotifikasiResponse {
    var status : Boolean = false
    var message : String? = null
    var data : List<NotifikasiData>? = null
}
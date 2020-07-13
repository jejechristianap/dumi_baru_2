package com.minjem.dumi.view

import com.minjem.dumi.response.RUser

interface UserView {
    fun response(response : RUser)
    fun error(text : String)
}
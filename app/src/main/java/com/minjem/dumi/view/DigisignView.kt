package com.minjem.dumi.view

import com.minjem.dumi.response.RDigisign

interface DigisignView {
    fun digiResponse (response : RDigisign)
    fun digiError (error : String)
}
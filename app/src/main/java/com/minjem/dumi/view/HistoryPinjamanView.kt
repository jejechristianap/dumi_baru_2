package com.minjem.dumi.view

import com.minjem.dumi.response.HistoryResponse

interface HistoryPinjamanView {
    fun historyPinjamanResponse(response: HistoryResponse)
    fun historyPinjamanError(error: String)
}
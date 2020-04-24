package com.hendi.pulsa.response

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("keterangan")
	val keterangan: String? = null,

	@field:SerializedName("nominal")
	val nominal: String? = null,

	@field:SerializedName("harga")
	val harga: String? = null,

	@field:SerializedName("kodeoperator")
	val kodeoperator: String? = null,

	@field:SerializedName("tipe")
	val tipe: String? = null,

	@field:SerializedName("operator")
	val operator: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
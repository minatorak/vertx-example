package com.example.vertx_kotlin.api

data class GetInformationRequest(
  val information: String? = null,
  val reqId: Long? = null
)

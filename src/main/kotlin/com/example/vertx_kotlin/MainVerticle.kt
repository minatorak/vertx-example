package com.example.vertx_kotlin

import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.launch

class MainVerticle :CoroutineVerticle() {

  override suspend fun start() {
    launch {  startWebServer() }
  }

  private suspend fun startWebServer() {
    vertx.deployVerticle(WebServer::class.qualifiedName).await()
  }

}

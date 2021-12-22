package com.example.vertx_kotlin

import com.example.vertx_kotlin.commons.MainRouter
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await

class WebServer : CoroutineVerticle() {

  companion object {
    private val log = LoggerFactory.getLogger(WebServer::class.java)
  }

  override suspend fun start() {
    val handler = MainRouter(vertx).routerHandler()
    vertx.createHttpServer()
      .requestHandler(handler)
      .listen(config.getInteger("port", 8080))
      .onSuccess {
        log.info("HTTP server started on port " + it.actualPort())
      }
      .await()
  }

  override suspend fun stop() {
    log.info("WebServer stop")
  }
}

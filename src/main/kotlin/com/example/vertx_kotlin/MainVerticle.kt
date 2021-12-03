package com.example.vertx_kotlin

import com.example.vertx_kotlin.commons.mainRouter
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServerOptions

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    val server = vertx.createHttpServer(HttpServerOptions().apply {

    })
    server.requestHandler(mainRouter(vertx)).listen(8080)
  }

  override fun stop(stopPromise: Promise<Void>) {
    super.stop(stopPromise)
  }


}

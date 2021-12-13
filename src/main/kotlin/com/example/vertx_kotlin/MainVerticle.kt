package com.example.vertx_kotlin

import com.example.vertx_kotlin.commons.mainRouter
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions

class MainVerticle : AbstractVerticle() {

  private val server by lazy { vertx.createHttpServer(HttpServerOptions()) }

  override fun start(startPromise: Promise<Void>) {
    server
      .requestHandler(mainRouter(vertx))
      .listen(8080) { httpServe: AsyncResult<HttpServer> ->
        if (httpServe.succeeded()) {
          println("HTTP server started on port 8888")
          startPromise.complete()
        } else {
          startPromise.fail(httpServe.cause())
        }
      }
  }

  override fun stop(stopPromise: Promise<Void>) {
    super.stop(stopPromise)
  }


}

package com.example.vertx_kotlin.commons

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.TimeoutHandler
import java.time.Duration

fun mainRouter(vertx: Vertx): Router {
  val router = Router.router(vertx)
  router.route().handler(::loggerHandler)

  router.route().handler(TimeoutHandler.create(Duration.ofSeconds(2).toMillis()))
  router.post().failureHandler(::helloFailureHandler)

  router.post("/hello").handler(::helloHandler)
  return router
}

private fun loggerHandler(routingContext: RoutingContext) {
  routingContext.request().body {
    println("loggerHandler ${it.result()}")
  }
  routingContext.next()

  routingContext.response()
}

private fun helloFailureHandler(failureRoutingContext: RoutingContext) {
  println("bodyAsString " + failureRoutingContext.bodyAsString)
  println(failureRoutingContext.failure())
  failureRoutingContext.response().statusCode = 400
  failureRoutingContext.response().send()
}

private fun helloHandler(routingContext: RoutingContext) {
  routingContext.request().body {
    println("helloHandler ${it.result()}")
  }
  throw RuntimeException()

}



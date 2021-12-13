package com.example.vertx_kotlin.commons

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod.GET
import io.vertx.core.http.HttpMethod.POST
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.TimeoutHandler
import java.time.Duration

fun mainRouter(vertx: Vertx): Router = Router.router(vertx).apply {
  route().handler(loggerHandler)
  route().handler(cors)

  route().handler(TimeoutHandler.create(Duration.ofSeconds(2).toMillis()))
  post().failureHandler(helloFailureHandler)

  post("/hello").handler(helloHandler)
}

val loggerHandler = Handler { ctx: RoutingContext ->
  ctx.request().body {
    println("loggerHandler ${it.result()}")
  }
  ctx.next()

}

val cors = CorsHandler.create("*")
  .allowedHeader("content-type")
  .allowedMethods(setOf(GET, POST))

val helloFailureHandler = Handler { failureRoutingContext: RoutingContext  ->
  println("bodyAsString " + failureRoutingContext.bodyAsString)
  println(failureRoutingContext.failure())
  failureRoutingContext.response().statusCode = 400
  failureRoutingContext.response().send()
}

val helloHandler = Handler { routingContext: RoutingContext ->
  routingContext.request().body {
    println("helloHandler ${it.result()}")
  }
  throw RuntimeException()

}



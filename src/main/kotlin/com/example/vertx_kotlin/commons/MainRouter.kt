package com.example.vertx_kotlin.commons

import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod.GET
import io.vertx.core.http.HttpMethod.POST
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.TimeoutHandler
import java.time.Duration

class MainRouter(vertx: Vertx) {
  private val router: Router by lazy { Router.router(vertx) }

  fun routerHandler(): Router = router.apply {
    route().handler(cors())
    route().handler(loggerHandler())
    router.route().consumes(APPLICATION_JSON.toString())
    router.route().produces(APPLICATION_JSON.toString())
    router.route().handler(BodyHandler.create())
    router.route().handler { context: RoutingContext ->
      context.response().headers().add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
      context.next()
    }

    route().handler(TimeoutHandler.create(Duration.ofSeconds(2).toMillis()))
    post().failureHandler(globalFailureHandler())
  }

  private fun cors() = CorsHandler.create("*")
    .allowedHeader("content-type")
    .allowedMethods(setOf(GET, POST))


  private fun loggerHandler() = Handler { ctx: RoutingContext ->
    ctx.request().body().onComplete {
      println("request -> ${it.result()}")
    }

    ctx.addHeadersEndHandler {
      ctx.request().bodyHandler {
        println("addHeadersEndHandler ${ctx.get<String>("response")}")
        println("log api summary ${System.currentTimeMillis() - ctx.get<Long>("timestamp")}")
      }
    }
    ctx.put("timestamp", System.currentTimeMillis())
    ctx.next()
  }

  private fun globalFailureHandler() = Handler { failureRoutingContext: RoutingContext ->
    println("bodyAsString " + failureRoutingContext.bodyAsString)
    println(failureRoutingContext.failure())
    failureRoutingContext.response().statusCode = 400
    failureRoutingContext.response().send()
  }
}



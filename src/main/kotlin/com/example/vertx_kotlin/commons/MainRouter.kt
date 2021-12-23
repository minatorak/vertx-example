package com.example.vertx_kotlin.commons

import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod.GET
import io.vertx.core.http.HttpMethod.POST
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.TimeoutHandler
import java.time.Duration

class MainRouter(vertx: Vertx) {
  private val router: Router by lazy { Router.router(vertx) }
  companion object {
    private val log = LoggerFactory.getLogger(MainRouter::class.java)
  }

  fun routerHandler(): Router = router.apply {
    route().handler(cors())
    route().handler(loggerHandler())
    route().consumes(APPLICATION_JSON.toString())
    route().produces(APPLICATION_JSON.toString())

    route().handler(TimeoutHandler.create(Duration.ofSeconds(2).toMillis()))
    post().failureHandler(globalFailureHandler())
  }

  private fun cors() = CorsHandler.create("*")
    .allowedHeader("content-type")
    .allowedMethods(setOf(GET, POST))


  private fun loggerHandler() = Handler { ctx: RoutingContext ->
    ctx.request().body().onComplete {
      log.info("request path: ${ctx.currentRoute().path} -> ${it.result()}")
    }

    ctx.addHeadersEndHandler {
      ctx.response().endHandler {
        log.info("response path: ${ctx.currentRoute().path} -> ${ctx.get<String>("response")}")
        log.info("log api summary ${System.currentTimeMillis() - ctx.get<Long>("timestamp")}")
      }
    }
    ctx.put("timestamp", System.currentTimeMillis())
    ctx.next()
  }

  private fun globalFailureHandler() = Handler { ctx: RoutingContext ->
//    log.info("bodyAsString " + ctx.request().body())
    log.info("globalFailureHandler ${ctx.failure()}")
    ctx.response().statusCode = 400
    ctx.response().send()
  }

}

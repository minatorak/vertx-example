package com.example.vertx_kotlin.commons

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod.GET
import io.vertx.core.http.HttpMethod.POST
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.TimeoutHandler
import io.vertx.kotlin.coroutines.awaitResult
import java.time.Duration

fun mainRouter(vertx: Vertx): Router = Router.router(vertx).apply {
  route().handler(loggerHandler)
  route().handler(cors)

  route().handler(TimeoutHandler.create(Duration.ofSeconds(2).toMillis()))
  post().failureHandler(helloFailureHandler)

  post("/hello").handler(helloHandler)
}

val loggerHandler = Handler { ctx: RoutingContext ->
  ctx.request().body().onComplete {
    println("request -> ${it.result()}")
  }

  ctx.addHeadersEndHandler {
    ctx.request().bodyHandler {
      println("addHeadersEndHandler ${ctx.get<String>("response")}")
    }
  }

//  ctx.request().handler {requestBuffer->
//    println("request().handler $requestBuffer")
//  }
//
//  ctx.addBodyEndHandler {
//    println("addBodyEndHandler ${ctx.get<String>("response")}")
//  }
//  ctx.addEndHandler {
//    ctx.request().bodyHandler { requestBuffer ->
//      println("addEndHandler $requestBuffer + ${ctx.get<String>("response")}")
//    }
//  }
//
//  ctx.addHeadersEndHandler {
//    ctx.request().bodyHandler { requestBuffer ->
//      println("addHeadersEndHandler $requestBuffer + ${ctx.get<String>("response")}")
//    }
//  }
//
//  ctx.addBodyEndHandler {
//    ctx.request().bodyHandler { requestBuffer ->
//      println("addHeadersEndHandler $requestBuffer + ${ctx.get<String>("response")}")
//    }
//  }
//
//  ctx.response().closeHandler {
//    ctx.request().bodyHandler { requestBuffer ->
//      println("response().closeHandler $requestBuffer + ${ctx.get<String>("response")}")
//    }
//  }
//  ctx.request().handler {
//      println("request().endHandler $it + ${ctx.get<String>("response")}") // 4
//  }
//
//  ctx.request().bodyHandler {
//    println("bodyHandler ${it}") // 5
//    println("bodyHandler response-> ${ctx.get<String>("response")}") // 6
//  }
//
//  ctx.request().body {
//    println("loggerHandler ${it.result()}") // 7
//  }
//    ctx.request().body().onSuccess {
//      println("ctx.request().body().onSuccess")
//    }.onComplete {
//      println("ctx.request().body().onComplete")
//    }
//  ctx.addHeadersEndHandler {
//    ctx.request().body().onComplete {
//      println("addHeadersEndHandler request -> $it") // 9
//    }
//    println("addHeadersEndHandler response-> ${ctx.get<String>("response")}") // 1
//  }
//  ctx.addEndHandler {
//    println("addEndHandler -> ${ctx.get<String>("response")}") // 3
//  }
//  ctx.response().bodyEndHandler {
//    println("closeHandler -> ${ctx.get<String>("response")}") // 2
//  }
  ctx.next()

}

val cors = CorsHandler.create("*")
  .allowedHeader("content-type")
  .allowedMethods(setOf(GET, POST))

val helloFailureHandler = Handler { failureRoutingContext: RoutingContext ->
  println("bodyAsString " + failureRoutingContext.bodyAsString)
  println(failureRoutingContext.failure())
  failureRoutingContext.response().statusCode = 400
  failureRoutingContext.response().send()
}

val helloHandler = Handler { routingContext: RoutingContext ->
  routingContext.request().body {
    println("service ${it.result()}") // 8
  }
  val responseBody = "Hello"
  routingContext.put("response", responseBody)
  routingContext
    .response().end(responseBody)
}



package com.example.vertx_kotlin.api

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import io.vertx.redis.client.RedisAPI

class HelloApi(private val redisAPI: RedisAPI) {

  fun helloHandler() = Handler { routingContext: RoutingContext ->
    routingContext.request().body {
      println("service ${it.result()}") // 8
    }
    val responseBody = "Hello"
    routingContext.put("response", responseBody)
    routingContext
      .response().end(responseBody)
  }

}

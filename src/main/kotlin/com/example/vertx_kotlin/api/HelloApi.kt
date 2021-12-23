package com.example.vertx_kotlin.api

import com.example.vertx_kotlin.commons.sendResponse
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.redis.client.RedisAPI
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class HelloApi(private val redisClient: RedisAPI, private val vertx: Vertx) : CoroutineScope {

  companion object {
    private val log = LoggerFactory.getLogger(HelloApi::class.java)
  }

  fun helloHandler() = Handler { routingContext: RoutingContext ->
    routingContext.request().body {
      log.info("service ${it.result()}") // 8
    }
    val responseBody = "Hello"
    routingContext.put("response", responseBody)
    routingContext.sendResponse(responseBody)
  }

  fun getInformation() = Handler<RoutingContext> { ctx ->
    ctx.request().body() { buf ->
      val request: GetInformationRequest = Json.decodeValue(buf.result(), GetInformationRequest::class.java)
      log.info("request getInformation $request")

      redisClient.get(request.information) { redis ->
        ctx.sendResponse(redis.result().toString())
      }
    }

  }

  override val coroutineContext: CoroutineContext
    get() = vertx.orCreateContext.dispatcher()

}

package com.example.vertx_kotlin

import com.example.vertx_kotlin.api.HelloApi
import com.example.vertx_kotlin.commons.MainRouter
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.redis.client.*

class WebServer : CoroutineVerticle() {
  lateinit var redis: RedisAPI
  lateinit var helloApi: HelloApi

  companion object {
    private val log = LoggerFactory.getLogger(WebServer::class.java)
  }

  override suspend fun start() {
    createRedisApi()
    val handler = MainRouter(vertx)
      .routerHandler()
      .apply {
        apiRouter(this)
      }
    vertx.createHttpServer()
      .requestHandler(handler)
      .listen(config.getInteger("port", 8080))
      .onSuccess {
        log.info("HTTP server started on port " + it.actualPort())
      }
      .await()
  }

  private fun apiRouter(router: Router) {
    helloApi = HelloApi(redis, vertx)
    router.post("/hello").handler(helloApi.helloHandler())
    router.post("/get-redis").handler(helloApi.getInformation())

  }

  fun createRedisApi() {
    val option = RedisOptions()
      .setType(RedisClientType.STANDALONE)
      .addConnectionString("redis://127.0.0.1:6379")
//      .addConnectionString("redis://127.0.0.1:5001")
//      .addConnectionString("redis://127.0.0.1:5002")
//      .setMasterName("sentinel7000")
      .setRole(RedisRole.MASTER)
    val redisClient = Redis.createClient(vertx, option)

    redis = RedisAPI.api(redisClient)
    redisClient
      .send(Request.cmd(Command.PING))
      .onSuccess {
        log.info("connected redis complete PING -> $it with ${option.toJson()} ")
      }.onFailure {
        log.error("connected redis failure cause: ${it.message} with ${option.toJson()} ")
      }

  }

  override suspend fun stop() {
    redis.close()
    log.info("WebServer stop")
  }
}

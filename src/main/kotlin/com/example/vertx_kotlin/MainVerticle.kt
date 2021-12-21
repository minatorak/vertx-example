package com.example.vertx_kotlin

import com.example.vertx_kotlin.commons.ConfigurationUtil
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.core.DeploymentOptions
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.launch

class MainVerticle : CoroutineVerticle() {

  override suspend fun start() {
    launch { startWebServer() }
  }

  private suspend fun startWebServer() {
    val readConfig = ConfigurationUtil().getConfigStoreJson("verticles")
    val configRetriever = ConfigRetriever.create(vertx, ConfigRetrieverOptions().addStore(readConfig))
    val brokerOptions = DeploymentOptions().setConfig(configRetriever.config.await())
    vertx.deployVerticle(WebServer::class.qualifiedName, brokerOptions).await()
  }

}

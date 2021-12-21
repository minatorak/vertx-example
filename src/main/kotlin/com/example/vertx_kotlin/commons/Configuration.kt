package com.example.vertx_kotlin.commons

import io.vertx.config.ConfigStoreOptions
import io.vertx.core.json.JsonObject

private const val RESOURCE_PATH = "src/main/kotlin/resources/"

class ConfigurationUtil {

  fun getConfigStoreJson(fileName: String): ConfigStoreOptions =
    ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(JsonObject().put("path", "${RESOURCE_PATH}${fileName}.json"))
}

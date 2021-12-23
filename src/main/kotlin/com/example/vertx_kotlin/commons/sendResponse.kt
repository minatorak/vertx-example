package com.example.vertx_kotlin.commons

import io.vertx.ext.web.RoutingContext


fun RoutingContext.sendResponse(response: String) {
  this.put("response", response)
  this.response().end(response)
}


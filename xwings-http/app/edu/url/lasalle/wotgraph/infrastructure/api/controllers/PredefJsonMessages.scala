package edu.url.lasalle.wotgraph.infrastructure.api.controllers

import play.api.libs.json.Json


trait PredefJsonMessages {

  val MessageKey = "msg"

  val BadJsonFormatMessage = Json.obj(MessageKey -> "bad json format")

}

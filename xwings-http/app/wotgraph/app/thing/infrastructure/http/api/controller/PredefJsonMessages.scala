package wotgraph.app.thing.infrastructure.http.api.controller

import play.api.libs.json.Json


trait PredefJsonMessages {

  val MessageKey = "msg"

  val BadJsonFormatMessage = Json.obj(MessageKey -> "bad json format")

}

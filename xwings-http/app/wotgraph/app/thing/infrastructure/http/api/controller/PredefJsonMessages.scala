package wotgraph.app.thing.infrastructure.http.api.controller

import play.api.libs.json.Json


trait PredefJsonMessages {

  val MessagesKey = "msg"

  val BadJsonFormatMessage = Json.obj(MessagesKey -> "bad json format")

}

package wotgraph.app.sensedv.application.usecase.dto

import play.api.libs.json.JsObject
import wotgraph.app.sensedv.domain.Sensed

case class CreateSensed(data: JsObject)

object CreateSensed {
  def toSensed(c: CreateSensed): Sensed = ???
}

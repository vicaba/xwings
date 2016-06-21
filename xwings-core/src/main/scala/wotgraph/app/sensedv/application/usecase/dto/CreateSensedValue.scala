package wotgraph.app.sensedv.application.usecase.dto

import java.util.UUID

import play.api.libs.json.JsObject
import wotgraph.app.sensedv.domain.SensedValue
import wotgraph.app.thing.application.service.action.ThingAndAction

case class CreateSensedValue(data: JsObject)

object CreateSensedValue {
  def toSensedValue(c: CreateSensedValue, thingId: UUID, actionName: String): SensedValue =
    SensedValue(namespace = ThingAndAction.namespace(thingId, actionName), data = c.data)
}

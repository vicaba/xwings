package wotgraph.app.thing.application.service.action

import java.util.UUID

import wotgraph.app.thing.domain.entity.Action

case class ThingAndAction(thingId: UUID, action: Action) {
  def namespace = ThingAndAction.namespace(thingId, action.actionName)
}

object ThingAndAction {
  def namespace(thingId: UUID, actionName: String) = s"$thingId/$actionName"
}

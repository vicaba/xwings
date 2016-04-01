package edu.url.lasalle.infrastructure.serializers.json

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Action
import play.api.libs.functional.syntax._
import play.api.libs.json.{OFormat, OWrites, _}

object ActionSerializer {

  val ActionNameKey = "actionName"

  val ContextIdKey = "contextId"

  val ContextValueKey = "contextValue"

  val actionReads: Reads[Action] = (
    (__ \ ActionNameKey).read[String] and
      (__ \ ContextIdKey).read[UUID] and
      (__ \ ContextValueKey).read[String]
    ) (Action.apply _)

  val actionWrites: OWrites[Action] = (
    (__ \ ActionNameKey).write[String] and
      (__ \ ContextIdKey).write[UUID] and
      (__ \ ContextValueKey).write[String]
    ) (unlift(Action.unapply))

  val actionFormat: OFormat[Action] = OFormat(actionReads, actionWrites)
}
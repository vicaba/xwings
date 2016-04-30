package edu.url.lasalle.wotgraph.infrastructure.serializers.json

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
      (__ \ ContextValueKey).read[JsObject]
    ) (Action.apply _)

  val actionWrites: OWrites[Action] = (
    (__ \ ActionNameKey).write[String] and
      (__ \ ContextIdKey).write[UUID] and
      (__ \ ContextValueKey).write[JsObject]
    ) (unlift(Action.unapply))


  val actionFormat: OFormat[Action] = OFormat(actionReads, actionWrites)
}
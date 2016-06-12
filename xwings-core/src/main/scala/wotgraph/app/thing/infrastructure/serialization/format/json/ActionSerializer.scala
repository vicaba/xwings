package wotgraph.app.thing.infrastructure.serialization.format.json

import java.util.UUID

import wotgraph.app.thing.domain.entity.Action
import play.api.libs.functional.syntax._
import play.api.libs.json.{OFormat, OWrites, _}
import wotgraph.app.thing.infrastructure.serialization.keys.ActionKeys

object ActionSerializer {


  val actionReads: Reads[Action] = (
    (__ \ ActionKeys.ActionName).read[String] and
      (__ \ ActionKeys.ContextId).read[UUID] and
      (__ \ ActionKeys.ContextValue).read[String]
    ) (Action.apply _)

  val actionWrites: OWrites[Action] = (
    (__ \ ActionKeys.ActionName).write[String] and
      (__ \ ActionKeys.ContextId).write[UUID] and
      (__ \ ActionKeys.ContextValue).write[String]
    ) (unlift(Action.unapply))


  val actionFormat: OFormat[Action] = OFormat(actionReads, actionWrites)
}
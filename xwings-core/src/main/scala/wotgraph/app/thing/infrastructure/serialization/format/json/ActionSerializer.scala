package wotgraph.app.thing.infrastructure.serialization.format.json

import java.util.UUID

import wotgraph.app.thing.domain.entity.Action
import play.api.libs.functional.syntax._
import play.api.libs.json.{OFormat, OWrites, _}
import wotgraph.app.thing.infrastructure.serialization.keys.ActionKeys._

object ActionSerializer {


  val actionReads: Reads[Action] = (
    (__ \ ActionName).read[String] and
      (__ \ ContextId).read[UUID] and
      (__ \ ContextValue).read[String]
    ) (Action.apply _)

  val actionWrites: OWrites[Action] = (
    (__ \ ActionName).write[String] and
      (__ \ ContextId).write[UUID] and
      (__ \ ContextValue).write[String]
    ) (unlift(Action.unapply))


  val actionFormat: OFormat[Action] = OFormat(actionReads, actionWrites)
}
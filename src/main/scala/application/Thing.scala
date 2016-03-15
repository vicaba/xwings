package application

import java.util.UUID

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, _}


case class Action(actionName: String, contextId: UUID, contextValue: String)

object Action {
  implicit val actionReads: Reads[Action] = (
    (__ \ "actionName").read[String] and
      (__ \ "contextId").read[UUID] and
      (__ \ "contextValue").read[String]
    ) (Action.apply _)
}


case class Thing(id: UUID, humanName: String, action: Action)

object Thing {
  implicit val thingReads: Reads[Thing] = (
    (__ \ "id").read[UUID] and
      (__ \ "hName").read[String] and
      (__ \ "action").read[Action]
    ) (Thing.apply _)
}

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


case class Thing(id: UUID, humanName: String, action: Action, labels: List[String], relations: List[String])

object Thing {

  implicit object ThingReads extends Reads[Thing] {
    override def reads(json: JsValue): JsResult[Thing] = {
      val id = (json \ "_id").as[UUID]
      val name = (json \ "hName").as[String]
      val action = Json.parse((json \ "action").as[String].replace("\\\"","\"")).validate[Action].get
      val labels = Nil
      val relations = Nil
      val result = Thing(id,name,action,labels,relations)
      JsSuccess(result)
    }
  }
}

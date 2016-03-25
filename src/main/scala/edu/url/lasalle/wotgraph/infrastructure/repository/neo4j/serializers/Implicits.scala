package edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.serializers

import java.util.UUID

import application.Action
import edu.url.lasalle.wotgraph.infrastructure.thing.repository.neo4j.mappings.Thing
import play.api.libs.functional.syntax._
import play.api.libs.json.{OFormat, OWrites, _}

object Implicits {

  implicit val thingJsonSerializer = ThingSerializer.ThingReads

  implicit val actionJsonSerializer = ActionSerializer.actionFormat

  object ThingSerializer {
//    object ThingReads extends Reads[Thing] {
//      override def reads(json: JsValue): JsResult[Thing] = {
//        val id = (json \ "_id").as[UUID]
//        val name = (json \ "hName").as[String]
//        val action = Json.parse((json \ "action").as[String].replace("\\\"", "\"")).validate[Action].get
//        val labels = Nil
//        val relations = Nil
//        val result = Thing(id, name, action, labels, relations)
//        JsSuccess(result)
//      }
//    }

    object ThingWrites extends OWrites[Thing] {
      override def writes(o: Thing): JsObject = {
        Json.obj("_id" -> o._id, "hName" -> o.hName, "actions" -> 
      }
    }
  }

  object ActionSerializer {

    val actionReads: Reads[Action] = (
      (__ \ "actionName").read[String] and
        (__ \ "contextId").read[UUID] and
        (__ \ "contextValue").read[String]
      ) (Action.apply _)

    val actionWrites: OWrites[Action] = (
      (__ \ "actionName").write[String] and
        (__ \ "contextId").write[UUID] and
        (__ \ "contextValue").write[String]
      ) (unlift(Action.unapply))

    val actionFormat: OFormat[Action] = OFormat(actionReads, actionWrites)
  }

}

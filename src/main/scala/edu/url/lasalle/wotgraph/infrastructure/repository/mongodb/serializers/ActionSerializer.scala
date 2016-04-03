package edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.serializers

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Action
import play.api.libs.json.{JsError, Json, OFormat, OWrites, _}
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.{ActionSerializer => Serializer}


object ActionSerializer {

  val MongoIdKey = "_id"

  object ActionReads extends Reads[Action] {
    override def reads(json: JsValue): JsResult[Action] = {
      json match {
        case json: JsObject =>
          Serializer.ActionReads.reads(json) match {
            case JsSuccess(action, _) =>
              val actionId = (json \ MongoIdKey).as[UUID]
              JsSuccess(action.copy(thingId = actionId))
            case e: JsError => e
          }
        case _ => JsError()
      }
    }
  }

  object ActionWrites extends OWrites[Action] {
    override def writes(o: Action): JsObject =
      Serializer.ActionWrites.writes(o) ++ Json.obj(MongoIdKey -> o.thingId)
  }

  val actionFormat: OFormat[Action] = OFormat(ActionReads, ActionWrites)

  implicit val implicitActionFormat = actionFormat

  object SetOfActionsWrites extends OWrites[Set[Action]] {
    override def writes(o: Set[Action]): JsObject = {
      val id = o.head.thingId
      Json.obj("actions" -> Writes.set[Action](ActionWrites).writes(o)) ++ Json.obj(MongoIdKey -> id)
    }
  }

  object SetOfActionsReads extends Reads[Set[Action]] {
    override def reads(json: JsValue): JsResult[Set[Action]] = json match {
      case json: JsObject =>
        val set = (json \ "actions").as[Set[Action]](Reads.set[Action](ActionReads))
        JsSuccess(set)
      case _ => JsError()
    }
  }

  val setOfActionsFormat = OFormat(SetOfActionsReads, SetOfActionsWrites)

  implicit val implicitSetOfActionsFormat = setOfActionsFormat


}

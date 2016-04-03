package edu.url.lasalle.wotgraph.infrastructure.serializers.json

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Action
import play.api.libs.functional.syntax._
import play.api.libs.json.{OFormat, OWrites, _}

object ActionSerializer {

  val ActionNameKey = "actionName"

  val ContextIdKey = "contextId"

  val ContextValueKey = "contextValue"

  object ActionReads extends Reads[Action] {
    override def reads(json: JsValue): JsResult[Action] = {
      json match {
        case json: JsObject =>
          val actionName = (json \ ActionNameKey).as[String]
          val contextId = (json \ ContextIdKey).as[UUID]
          val contextValue = (json \ ContextValueKey).as[String]
          JsSuccess(Action(actionName, contextId, contextValue))
        case _ => JsError()
      }
    }
  }

  object ActionWrites extends OWrites[Action] {
    override def writes(o: Action): JsObject =
      Json.obj(ActionNameKey -> o.actionName, ContextIdKey -> o.contextId, ContextValueKey -> o.contextValue)
  }


  val actionFormat: OFormat[Action] = OFormat(ActionReads, ActionWrites)
}
package edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.serializers

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.{Action, Thing}
import play.api.libs.functional.syntax._
import play.api.libs.json.{OFormat, OWrites, _}

object Implicits {

  implicit val thingJsonSerializer = ThingSerializer.ThingWrites

  implicit val actionJsonSerializer = ActionSerializer.actionFormat

  object ThingSerializer {

    object ThingWrites extends OWrites[Thing] {
      override def writes(o: Thing): JsObject = {

        import scala.collection.JavaConverters._

        def parseActionFromString(action: String): Action =
          Json.parse(action.replace("\\\"", "\"")).validate[Action].get

        def createActionJson(t: Thing): JsObject = {
          val actionName = parseActionFromString(t.action).actionName
          Json.obj("actionName" -> actionName, "thingId" -> t._id)
        }

        val children = o.children.asScala.map(_._id)

        val actions = o.actions.asScala.map(createActionJson) + createActionJson(o)

        Json.obj("_id" -> o._id, "hName" -> o.hName, "actions" -> actions, "children" -> children)
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

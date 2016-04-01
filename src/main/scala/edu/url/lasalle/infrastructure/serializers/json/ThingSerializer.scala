package edu.url.lasalle.infrastructure.serializers.json

import edu.url.lasalle.wotgraph.domain.thing.{Action, Thing}
import play.api.libs.json.{JsObject, Json, OWrites}

object ThingSerializer {

  val IdKey = "_id"

  val HNameKey = "hName"

  val ActionsKey = "actions"

  val ChildrenKey = "children"

  val Action$ThingId = "thingId"

  object ThingWrites extends OWrites[Thing] {
    override def writes(o: Thing): JsObject = {

      import scala.collection.JavaConverters._

      def parseActionFromString(action: String): Action =
        Json.parse(action.replace("\\\"", "\"")).validate[Action](ActionSerializer.actionReads).get

      def createActionJson(t: Thing): JsObject = {
        val actionName = parseActionFromString(t.action).actionName
        Json.obj(ActionSerializer.ActionNameKey -> actionName, Action$ThingId -> t._id)
      }

      val children = o.children.asScala.map(_._id)

      val actions = o.actions.asScala.map(createActionJson) + createActionJson(o)

      Json.obj(IdKey -> o._id, HNameKey -> o.hName, ActionsKey -> actions, ChildrenKey -> children)
    }
  }
}
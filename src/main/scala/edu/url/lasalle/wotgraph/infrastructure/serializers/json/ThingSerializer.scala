package edu.url.lasalle.wotgraph.infrastructure.serializers.json

import edu.url.lasalle.wotgraph.domain.thing.{Action, Thing}
import play.api.libs.json._
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.Implicits._

object ThingSerializer {

  val IdKey = "_id"

  val ActionsKey = "actions"

  val ChildrenKey = "children"

  val MetadataKey = "metadata"

  object ThingWrites extends OWrites[Thing] {
    override def writes(o: Thing): JsObject = {

      val children = Json.obj(ChildrenKey -> o.children.map(_._id))

      val actions =
        if (o.actions.isEmpty) Json.obj()
        else Json.obj(ActionsKey -> Writes.set[Action].writes(o.actions))

      val metadata = o.metadata.fold(Json.obj())(m => Json.obj(MetadataKey -> m.data))

      Json.obj(IdKey -> o._id) ++ children ++ actions ++ metadata
    }
  }

}
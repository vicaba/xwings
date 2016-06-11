package wotgraph.app.thing.infrastructure.serialization.format.json

import java.util.UUID

import wotgraph.app.thing.domain.entity.{Action, Metadata, Thing}
import wotgraph.app.thing.infrastructure.serialization.format.json.Implicits._

import play.api.libs.json._

object ThingSerializer {

  val IdKey = "_id"

  val ActionsKey = "actions"

  val ChildrenKey = "children"

  val MetadataKey = "metadata"

  object ThingWrites extends OWrites[Thing] {
    override def writes(o: Thing): JsObject = {

      val actions =
        if (o.actions.isEmpty) Json.obj()
        else Json.obj(ActionsKey -> o.actions)

      val metadata = o.metadata.fold(Json.obj())(m => Json.obj(MetadataKey -> m.data))

      Json.obj(IdKey -> o._id) ++ actions ++ metadata
    }
  }

  object ThingReads extends Reads[Thing] {
    override def reads(json: JsValue): JsResult[Thing] = {

      val id = (json \ IdKey).as[UUID]

      val actions = (json \ ActionsKey).asOpt[Set[Action]] getOrElse Set.empty

      val metadata = (json \ MetadataKey).asOpt[JsObject] map (Metadata(_))

      JsSuccess(Thing(id, metadata, actions))

    }
  }

  val thingFormat = OFormat(ThingReads, ThingWrites)

}
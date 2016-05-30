package edu.url.lasalle.wotgraph.infrastructure.serializers.json

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.thing.{Action, Metadata, Thing}
import play.api.libs.json._
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.Implicits._

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
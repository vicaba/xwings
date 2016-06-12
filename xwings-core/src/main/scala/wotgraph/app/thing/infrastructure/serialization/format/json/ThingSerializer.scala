package wotgraph.app.thing.infrastructure.serialization.format.json

import java.util.UUID

import wotgraph.app.thing.domain.entity.{Action, Metadata, Thing}
import wotgraph.app.thing.infrastructure.serialization.format.json.Implicits._
import play.api.libs.json._
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys

object ThingSerializer {

  object ThingWrites extends OWrites[Thing] {
    override def writes(o: Thing): JsObject = {

      val actions =
        if (o.actions.isEmpty) Json.obj()
        else Json.obj(ThingKeys.Actions -> o.actions)

      val metadata = o.metadata.fold(Json.obj())(m => Json.obj(ThingKeys.Metadata -> m.data))

      Json.obj(ThingKeys.Id -> o._id) ++ actions ++ metadata
    }
  }

  object ThingReads extends Reads[Thing] {
    override def reads(json: JsValue): JsResult[Thing] = {

      val id = (json \ ThingKeys.Id).as[UUID]

      val actions = (json \ ThingKeys.Actions).asOpt[Set[Action]] getOrElse Set.empty

      val metadata = (json \ ThingKeys.Metadata).asOpt[JsObject] map (Metadata(_))

      JsSuccess(Thing(id, metadata, actions))

    }
  }

  val thingFormat = OFormat(ThingReads, ThingWrites)

}
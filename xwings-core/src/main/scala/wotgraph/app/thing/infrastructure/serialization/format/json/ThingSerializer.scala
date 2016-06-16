package wotgraph.app.thing.infrastructure.serialization.format.json

import java.util.UUID

import wotgraph.app.thing.domain.entity.{Action, Metadata, Thing}
import wotgraph.app.thing.infrastructure.serialization.format.json.Implicits._
import play.api.libs.json._
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys._

object ThingSerializer {

  object ThingWrites extends OWrites[Thing] {
    override def writes(o: Thing): JsObject = {

      val actions =
        if (o.actions.isEmpty) Json.obj()
        else Json.obj(Actions -> o.actions)

      val metadata = o.metadata.fold(Json.obj())(m => Json.obj(ThingKeys.Metadata -> m.data))

      Json.obj(Id -> o._id) ++ actions ++ metadata
    }
  }

  object ThingReads extends Reads[Thing] {
    override def reads(json: JsValue): JsResult[Thing] = {

      val id = (json \ Id).as[UUID]

      val actions = (json \ Actions).asOpt[Set[Action]] getOrElse Set.empty

      val metadata = (json \ ThingKeys.Metadata).asOpt[JsObject] map (Metadata(_))

      JsSuccess(Thing(id, metadata, actions))

    }
  }

  val thingFormat = OFormat(ThingReads, ThingWrites)

}
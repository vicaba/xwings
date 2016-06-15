package wotgraph.app.thing.infrastructure.http.serialization.format.json

import java.util.UUID

import wotgraph.app.thing.domain.entity.{Action, Metadata, Thing}
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys
import wotgraph.app.thing.infrastructure.serialization.format.json.Implicits._
import play.api.libs.json._

object ThingSerializer {

  object ThingWrites extends OWrites[Thing] {
    override def writes(o: Thing): JsObject = {

      val actions = Json.obj(ThingKeys.Actions -> o.actions)

      val metadata = Json.obj(ThingKeys.Metadata -> o.metadata)

      val children = Json.obj(ThingKeys.Children -> o.children)

      Json.obj(ThingKeys.Id -> o._id) ++ actions ++ metadata ++ children
    }
  }

  object ThingReads extends Reads[Thing] {
    override def reads(json: JsValue): JsResult[Thing] = {

      val id = (json \ ThingKeys.Id).as[UUID]

      val actions = (json \ ThingKeys.Actions).asOpt[Set[Action]] getOrElse Set.empty

      val metadata = (json \ ThingKeys.Metadata).asOpt[JsObject] map (Metadata(_))

      val children = (json \ ThingKeys.Children).asOpt[Set[UUID]] map(_.map(Thing(_))) getOrElse Set.empty

      JsSuccess(Thing(id, metadata, actions, children))

    }
  }

  val thingFormat = OFormat(ThingReads, ThingWrites)

}


object ThingMinifiedSerializer {

  object ThingWrites extends OWrites[Thing] {
    override def writes(o: Thing): JsObject = {

      val actions = Json.obj(ThingKeys.Actions -> o.actions)

      val metadata = Json.obj(ThingKeys.Metadata -> o.metadata)

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

  val thingSeqReads = Reads.seq[Thing](ThingReads)

  val thingSeqWrites = Writes.seq[Thing](ThingWrites)

  val thingFormat = OFormat(ThingReads, ThingWrites)

  val thingSeqFormat = Format(thingSeqReads, thingSeqWrites)

}
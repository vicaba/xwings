package edu.url.lasalle.wotgraph.infrastructure.api.serializers.json

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.thing.{Action, Metadata, Thing}
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.{ ThingSerializer => ThingBaseSerializer}
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.Implicits._

import play.api.libs.json._

object ThingSerializer {

  object ThingWrites extends OWrites[Thing] {
    override def writes(o: Thing): JsObject = {

      val actions = Json.obj(ThingBaseSerializer.ActionsKey -> o.actions)

      val metadata = Json.obj(ThingBaseSerializer.MetadataKey -> o.metadata)

      val children = Json.obj(ThingBaseSerializer.ChildrenKey -> o.children)

      Json.obj(ThingBaseSerializer.IdKey -> o._id) ++ actions ++ metadata ++ children
    }
  }

  object ThingReads extends Reads[Thing] {
    override def reads(json: JsValue): JsResult[Thing] = {

      val id = (json \ ThingBaseSerializer.IdKey).as[UUID]

      val actions = (json \ ThingBaseSerializer.ActionsKey).asOpt[Set[Action]] getOrElse Set.empty

      val metadata = (json \ ThingBaseSerializer.MetadataKey).asOpt[JsObject] map (Metadata(_))

      val children = (json \ ThingBaseSerializer.ChildrenKey).asOpt[Set[UUID]] map(_.map(Thing(_))) getOrElse Set.empty

      JsSuccess(Thing(id, metadata, actions, children))

    }
  }

  val thingFormat = OFormat(ThingReads, ThingWrites)

}


object ThingMinifiedSerializer {

  object ThingWrites extends OWrites[Thing] {
    override def writes(o: Thing): JsObject = {

      val actions = Json.obj(ThingBaseSerializer.ActionsKey -> o.actions)

      val metadata = Json.obj(ThingBaseSerializer.MetadataKey -> o.metadata)

      Json.obj(ThingBaseSerializer.IdKey -> o._id) ++ actions ++ metadata
    }
  }

  object ThingReads extends Reads[Thing] {
    override def reads(json: JsValue): JsResult[Thing] = {

      val id = (json \ ThingBaseSerializer.IdKey).as[UUID]

      val actions = (json \ ThingBaseSerializer.ActionsKey).asOpt[Set[Action]] getOrElse Set.empty

      val metadata = (json \ ThingBaseSerializer.MetadataKey).asOpt[JsObject] map (Metadata(_))

      JsSuccess(Thing(id, metadata, actions))

    }
  }

  private val setOfThingsReads = Reads.seq[Thing](ThingReads)

  private val setOfThingsWrites = Writes.seq[Thing](ThingWrites)

  val thingFormat = OFormat(ThingReads, ThingWrites)

  val thingSeqFormat = Format(setOfThingsReads, setOfThingsWrites)

}
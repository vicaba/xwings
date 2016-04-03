package edu.url.lasalle.wotgraph.infrastructure.serializers.json.dto

import java.util.UUID

import edu.url.lasalle.wotgraph.application.usecase.CreateThing
import edu.url.lasalle.wotgraph.domain.thing.Metadata
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.ThingSerializer
import play.api.libs.json.{JsObject, _}
import play.api.libs.functional.syntax._


object CreateThingSerializer {
  val createThingReads: Reads[CreateThing] = (
    (__ \ ThingSerializer.HNameKey).read[String] and
      (__ \ ThingSerializer.MetadataKey).read[JsObject].map(Metadata(_)) and
      (__ \ ThingSerializer.ChildrenKey).read[Set[UUID]]
    ) (CreateThing.apply _)
}



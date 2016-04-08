package edu.url.lasalle.wotgraph.infrastructure.serializers.json.dto

import java.util.UUID

import edu.url.lasalle.wotgraph.application.usecase.CreateThing
import edu.url.lasalle.wotgraph.domain.thing.{Action, Metadata}
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.Implicits._
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.ThingSerializer
import play.api.libs.json.{JsObject, _}
import play.api.libs.functional.syntax._


object CreateThingSerializer {
  val createThingReads: Reads[CreateThing] = (
    (__ \ ThingSerializer.MetadataKey).read[JsObject].map(Metadata(_)) and
      (__ \ ThingSerializer.ChildrenKey).readNullable[Set[Action]].map(_.getOrElse(Set.empty)) and
      (__ \ ThingSerializer.ChildrenKey).readNullable[Set[UUID]].map(_.getOrElse(Set.empty))
    ) (CreateThing.apply _)
}

package wotgraph.app.thing.infrastructure.serialization.format.json.dto

import java.util.UUID

import wotgraph.app.thing.application.usecase.CreateThing
import wotgraph.app.thing.domain.entity.{Action, Metadata}
import wotgraph.app.thing.infrastructure.serialization.format.json.ThingSerializer
import wotgraph.app.thing.infrastructure.serialization.format.json.Implicits._

import play.api.libs.json.{JsObject, _}
import play.api.libs.functional.syntax._


object CreateThingSerializer {
  val createThingReads: Reads[CreateThing] = (
    (__ \ ThingSerializer.MetadataKey).read[JsObject].map(Metadata(_)) and
      (__ \ ThingSerializer.ActionsKey).readNullable[Set[Action]].map(_.getOrElse(Set.empty)) and
      (__ \ ThingSerializer.ChildrenKey).readNullable[Set[UUID]].map(_.getOrElse(Set.empty))
    ) (CreateThing.apply _)
}

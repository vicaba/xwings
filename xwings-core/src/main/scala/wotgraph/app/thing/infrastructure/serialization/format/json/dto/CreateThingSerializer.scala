package wotgraph.app.thing.infrastructure.serialization.format.json.dto

import java.util.UUID

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsObject, _}
import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.domain.entity.{Action, Metadata}
import wotgraph.app.thing.infrastructure.serialization.format.json.Implicits._
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys


object CreateThingSerializer {
  val createThingReads: Reads[CreateThing] = (
    (__ \ ThingKeys.Metadata).read[JsObject].map(Metadata(_)) and
      (__ \ ThingKeys.Actions).readNullable[Set[Action]].map(_.getOrElse(Set.empty)) and
      (__ \ ThingKeys.Children).readNullable[Set[UUID]].map(_.getOrElse(Set.empty))
    ) (CreateThing.apply _)
}

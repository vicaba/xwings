package edu.url.lasalle.infrastructure.serializers.json.dto

import java.util.UUID

import edu.url.lasalle.wotgraph.application.usecase.CreateThing
import edu.url.lasalle.wotgraph.domain.thing.Metadata
import play.api.libs.json.{JsObject, _}
import play.api.libs.functional.syntax._


object CreateThingSerializer {
  val createThingReads: Reads[CreateThing] = (
    (__ \ "hName").read[String] and
      (__ \ "metadata").read[JsObject].map(Metadata(_)) and
      (__ \ "children").read[Set[UUID]]
    ) (CreateThing.apply _)
}

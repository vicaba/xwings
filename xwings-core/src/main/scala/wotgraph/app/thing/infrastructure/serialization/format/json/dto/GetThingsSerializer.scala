package wotgraph.app.thing.infrastructure.serialization.format.json.dto

import play.api.libs.functional.syntax._
import play.api.libs.json._
import wotgraph.app.thing.application.usecase.dto.GetThings


object GetThingsSerializer {

  val PageKey = "page"

  val ItemsPerPageKey = "itemsPerPage"

  val getThingsReads: Reads[GetThings] = (
    (__ \ PageKey).read[Int] and
      (__ \ ItemsPerPageKey).read[Int]
    ) (GetThings.apply _)
}
package edu.url.lasalle.infrastructure.serializers.json.dto

import edu.url.lasalle.wotgraph.application.usecase.GetThings
import play.api.libs.json._
import play.api.libs.functional.syntax._


object GetThingsSerializer {

  val PageKey = "page"
  val ItemsPerPageKey = "itemsPerPage"

  val getThingsReads: Reads[GetThings] = (
    (__ \ PageKey).read[Int] and
      (__ \ ItemsPerPageKey).read[Int]
    ) (GetThings.apply _)
}
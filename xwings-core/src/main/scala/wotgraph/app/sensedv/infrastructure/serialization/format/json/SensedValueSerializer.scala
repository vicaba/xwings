package wotgraph.app.sensedv.infrastructure.serialization.format.json


import java.util.{Date, UUID}

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.{OFormat, OWrites, _}
import wotgraph.app.sensedv.domain.SensedValue
import wotgraph.app.sensedv.infrastructure.serialization.keys.SensedValueKeys
import wotgraph.app.sensedv.infrastructure.serialization.keys.SensedValueKeys._

object SensedValueSerializer {

  val datePath = __ \ SensedValueKeys.Date

  val sensedValueReads: Reads[SensedValue] = (
    (__ \ Id).read[UUID] and
      (__ \ Namespace).read[String] and
      (__ \ SensedValueKeys.Date).read[DateTime] and
      (__ \ Data).read[JsObject]
    ) (SensedValue.apply _)

  val sensedValueWrites: OWrites[SensedValue] = (
    (__ \ Id).write[UUID] and
      (__ \ Namespace).write[String] and
      (__ \ SensedValueKeys.Date).write[DateTime] and
      (__ \ Data).write[JsObject]
    ) (unlift(SensedValue.unapply)
  )

  val sensedValueFormat = OFormat(sensedValueReads, sensedValueWrites)

}

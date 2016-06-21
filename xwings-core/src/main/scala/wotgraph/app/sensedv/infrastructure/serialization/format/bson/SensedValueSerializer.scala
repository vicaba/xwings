package wotgraph.app.sensedv.infrastructure.serialization.format.bson

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json.JsObject
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter}
import wotgraph.app.sensedv.domain.SensedValue
import wotgraph.app.sensedv.infrastructure.serialization.keys.SensedValueKeys
import reactivemongo.bson._
import reactivemongo.play.json.BSONFormats._


object SensedValueWrites extends BSONDocumentWriter[SensedValue] {
  override def write(t: SensedValue): BSONDocument = BSONDocument(
    SensedValueKeys.Id -> t.id.toString,
    SensedValueKeys.Namespace -> t.namespace,
    SensedValueKeys.Date -> BSONDateTime.apply(t.date.getMillis),
    SensedValueKeys.Data -> t.data
  )
}

object SensedValueReads extends BSONDocumentReader[SensedValue] {

  override def read(bson: BSONDocument): SensedValue = {
    SensedValue(
      bson.getAs[String](SensedValueKeys.Id).map(UUID.fromString).get,
      bson.getAs[String](SensedValueKeys.Namespace).get,
      bson.getAs[BSONDateTime](SensedValueKeys.Date).map(d => new DateTime(d.value)).get,
      bson.getAs[BSONDocument](SensedValueKeys.Data).map(d => BSONDocumentFormat.writes(d).as[JsObject]).get
    )
  }
}

package edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.serializers

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Metadata
import play.api.libs.json.{JsError, Json, OFormat, OWrites, _}
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.{ActionSerializer, MetadataSerializer => Serializer}


object MetadataSerializer {
  val MongoIdKey = "_id"

  object MetadataReads extends Reads[Metadata] {
    override def reads(json: JsValue): JsResult[Metadata] = {
      json match {
        case json: JsObject =>
          Serializer.metadataReads.reads(json) match {
            case JsSuccess(metadata, _) =>
              val metadataId = (json \ MongoIdKey).as[UUID]
              JsSuccess(metadata.copy(thingId = metadataId))
            case e: JsError => e
          }
        case _ => JsError()
      }
    }
  }

  object MetadataWrites extends OWrites[Metadata] {
    override def writes(o: Metadata): JsObject =
      Serializer.metadataWrites.writes(o) ++ Json.obj(MongoIdKey -> o.thingId)
  }

  val metadataFormat: OFormat[Metadata] = OFormat(MetadataReads, MetadataWrites)

  implicit val implicitMetadataFormat = metadataFormat
}

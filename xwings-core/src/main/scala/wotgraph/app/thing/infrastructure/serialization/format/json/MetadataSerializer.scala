package wotgraph.app.thing.infrastructure.serialization.format.json

import wotgraph.app.thing.domain.entity.Metadata
import play.api.libs.json.{OFormat, OWrites, _}

object MetadataSerializer {

  val MetadataIdKey = "actionName"

  val ContextIdKey = "contextId"

  val ContextValueKey = "contextValue"

  val metadataReads = Reads.apply[Metadata](js => JsSuccess(Metadata(js.as[JsObject])))

  val metadataWrites = OWrites.apply[Metadata](_.data)

  val metadataFormat: OFormat[Metadata] = OFormat(metadataReads, metadataWrites)
}
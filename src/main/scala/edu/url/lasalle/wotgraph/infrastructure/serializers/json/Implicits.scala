package edu.url.lasalle.wotgraph.infrastructure.serializers.json

import edu.url.lasalle.wotgraph.infrastructure.serializers.json.dto.{CreateThingSerializer, GetThingsSerializer}

object Implicits {

  implicit val thingJsonSerializer = ThingSerializer.thingFormat

  implicit val actionJsonSerializer = ActionSerializer.actionFormat

  implicit val metadataSerializer = MetadataSerializer.metadataFormat

  implicit val createThingSerializer = CreateThingSerializer.createThingReads

  implicit val getThingsSerializer = GetThingsSerializer.getThingsReads
}

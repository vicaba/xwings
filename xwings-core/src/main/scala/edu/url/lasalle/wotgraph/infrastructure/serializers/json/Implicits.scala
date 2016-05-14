package edu.url.lasalle.wotgraph.infrastructure.serializers.json

object Implicits {

  implicit val thingJsonSerializer = ThingSerializer.thingFormat

  implicit val actionJsonSerializer = ActionSerializer.actionFormat

  implicit val metadataSerializer = MetadataSerializer.metadataFormat

}

package wotgraph.app.thing.infrastructure.serialization.format.json

object Implicits {

  implicit val thingJsonSerializer = ThingSerializer.thingFormat

  implicit val actionJsonSerializer = ActionSerializer.actionFormat

  implicit val metadataSerializer = MetadataSerializer.metadataFormat

}

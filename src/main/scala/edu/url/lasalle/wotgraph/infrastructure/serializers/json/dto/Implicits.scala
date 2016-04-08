package edu.url.lasalle.wotgraph.infrastructure.serializers.json.dto


object Implicits {

  implicit val createThingSerializer = CreateThingSerializer.createThingReads

  implicit val getThingsSerializer = GetThingsSerializer.getThingsReads

}

package wotgraph.app.thing.infrastructure.serialization.format.json.dto


object Implicits {

  implicit val createThingJsonSerializer = CreateThingSerializer.createThingReads

  implicit val getThingsJsonSerializer = GetThingsSerializer.getThingsReads

}

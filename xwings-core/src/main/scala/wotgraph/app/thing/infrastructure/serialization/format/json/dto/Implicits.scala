package wotgraph.app.thing.infrastructure.serialization.format.json.dto


object Implicits {

  implicit val createThingSerializer = CreateThingSerializer.createThingReads

  implicit val getThingsSerializer = GetThingsSerializer.getThingsReads

}

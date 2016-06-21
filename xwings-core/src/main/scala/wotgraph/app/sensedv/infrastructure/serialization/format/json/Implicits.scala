package wotgraph.app.sensedv.infrastructure.serialization.format.json

object Implicits {

  implicit val SensedValueJsonSerializer = SensedValueSerializer.sensedValueFormat

}

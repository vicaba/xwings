package wotgraph.app.sensedv.infrastructure.serialization.format.bson


object Implicits {

  implicit val sensedValueBsonReads = SensedValueReads

  implicit val sensedValueBsonWrites = SensedValueWrites

}

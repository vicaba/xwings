package wotgraph.app.role.infrastructure.serialization.format.json


object Implicits {

  implicit val roleJsonSerializer = RoleSerializer.roleFormat

}

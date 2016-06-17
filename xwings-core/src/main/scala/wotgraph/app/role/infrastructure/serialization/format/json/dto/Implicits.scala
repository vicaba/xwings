package wotgraph.app.role.infrastructure.serialization.format.json.dto

object Implicits {

  implicit val createRoleJsonSerializer = CreateRoleSerializer.createRoleReads

}


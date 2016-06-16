package wotgraph.app.role.infrastructure.serialization.format.json.dto

object Implicits {

  implicit val createRoleSerializer = CreateRoleSerializer.createRoleReads

}


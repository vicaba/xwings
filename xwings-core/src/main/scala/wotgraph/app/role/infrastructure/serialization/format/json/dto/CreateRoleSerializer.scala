package wotgraph.app.role.infrastructure.serialization.format.json.dto

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, _}
import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.permission.infrastructure.serialization.format.json.Implicits._
import wotgraph.app.role.application.dto.CreateRole
import wotgraph.app.role.infrastructure.serialization.keys.RoleKeys._

object CreateRoleSerializer {
  val createRoleReads: Reads[CreateRole] = (
    (__ \ Name).read[String] and
      (__ \ Permissions).read[Set[Permission]]
    )(CreateRole.apply _)
}

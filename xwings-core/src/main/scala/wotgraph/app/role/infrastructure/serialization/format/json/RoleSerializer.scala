package wotgraph.app.role.infrastructure.serialization.format.json

import java.util.UUID

import play.api.libs.functional.syntax._
import play.api.libs.json.{OFormat, OWrites, _}
import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.infrastructure.serialization.keys.RoleKeys._
import play.api.libs.json._
import play.api.libs.json.Reads._
import wotgraph.app.permission.infrastructure.serialization.format.json.Implicits._

object RoleSerializer {

  val roleReads: Reads[Role] = (
    (__ \ Id).read[UUID] and
      (__ \ Name).read[String] and
      (__ \ Permissions).read[Set[Permission]]
    ) (Role.apply _)

  val roleWrites: OWrites[Role] = (
    (__ \ Id).write[UUID] and
      (__ \ Name).write[String] and
      (__ \ Permissions).write[Set[Permission]]
    ) (unlift(Role.unapply))

  val roleFormat: OFormat[Role] = OFormat(roleReads, roleWrites)

  val roleSeqReads = Reads.seq[Role](roleReads)

  val roleSeqWrites = Writes.seq[Role](roleWrites)

  val roleSeqFormat: Format[Seq[Role]] = Format(roleSeqReads, roleSeqWrites)
}

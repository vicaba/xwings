package wotgraph.app.permission.infrastructure.serialization.format.json

import java.util.UUID

import play.api.libs.json.Reads
import wotgraph.app.permission.domain.entity.Permission
import play.api.libs.functional.syntax._
import play.api.libs.json.{OFormat, OWrites, _}
import wotgraph.app.permission.infrastructure.serialization.keys.PermissionKeys._


object PermissionSerializer {

  val permissionReads: Reads[Permission] = (
    (__ \ Id).read[UUID] and
      (__ \ Desc).read[String]
    ) (Permission.apply _)

  val permissionWrites: OWrites[Permission] = (
    (__ \ Id).write[UUID] and
      (__ \ Desc).write[String]
    ) (unlift(Permission.unapply))

  val permissionFormat: OFormat[Permission] = OFormat(permissionReads, permissionWrites)

  val permissionSeqReads = Reads.seq[Permission](permissionReads)

  val permissionSeqWrites = Writes.seq[Permission](permissionWrites)

  val permissionSeqFormat: Format[Seq[Permission]] = Format(permissionSeqReads, permissionSeqWrites)

}

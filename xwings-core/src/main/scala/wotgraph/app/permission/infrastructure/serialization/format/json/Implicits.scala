package wotgraph.app.permission.infrastructure.serialization.format.json

object Implicits {

  implicit val permissionJsonSerializer = PermissionSerializer.permissionFormat

}

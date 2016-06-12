package wotgraph.app.permission.infrastructure.repository.neo4j

import java.util.UUID

import wotgraph.app.permission.domain.entity.Permission

import scala.collection._

object Neo4jHelper {

  import wotgraph.app.permission.infrastructure.serialization.keys.PermissionKeys

  def mapAsPermission(map: mutable.Map[String, AnyRef]): Permission = {
    val id = UUID.fromString(map.get(PermissionKeys.Id).get.asInstanceOf[String])
    val desc = map.get(PermissionKeys.Desc).get.asInstanceOf[String]
    Permission(id, desc)
  }

}

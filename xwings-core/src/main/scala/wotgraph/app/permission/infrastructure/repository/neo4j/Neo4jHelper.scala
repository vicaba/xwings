package wotgraph.app.permission.infrastructure.repository.neo4j

import java.util.UUID

import wotgraph.app.permission.domain.entity.Permission

import scala.collection._

object Neo4jHelper {

  import Permission.Keys._

  def mapAsPermission(map: mutable.Map[String, AnyRef]): Permission = {
    val id = UUID.fromString(map.get(IdKey).get.asInstanceOf[String])
    val desc = map.get(DescKey).get.asInstanceOf[String]
    Permission(id, desc)
  }

}

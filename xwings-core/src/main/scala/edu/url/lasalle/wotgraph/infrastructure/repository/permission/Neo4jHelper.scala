package edu.url.lasalle.wotgraph.infrastructure.repository.permission

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Permission

import scala.collection._

object Neo4jHelper {

  import Permission.Keys._

  def mapAsPermission(map: mutable.Map[String, AnyRef]): Permission = {
    val id = UUID.fromString(map.get(IdKey).get.asInstanceOf[String])
    val desc = map.get(DescKey).get.asInstanceOf[String]
    Permission(id, desc)
  }

}

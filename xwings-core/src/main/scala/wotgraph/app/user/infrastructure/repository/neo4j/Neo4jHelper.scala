package wotgraph.app.user.infrastructure.repository.neo4j

import java.util.UUID

import wotgraph.app.role.domain.entity.Role
import wotgraph.app.user.domain.entity.User
import wotgraph.app.user.infrastructure.serialization.keys.UserKeys._

object Neo4jHelper {

  def mapAsUser(map: scala.collection.mutable.Map[String, AnyRef]): (Role) => User = {
    val userId = UUID.fromString(map.get(Id).get.asInstanceOf[String])
    val userName = map.getOrElse(Name, "").asInstanceOf[String]
    val userPassword = map.getOrElse(Password, "").asInstanceOf[String]
    (r: Role) => User(userId, userName, userPassword, r)
  }

}

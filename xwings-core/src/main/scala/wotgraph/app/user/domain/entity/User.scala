package wotgraph.app.user.domain.entity

import java.util.UUID

import wotgraph.app.role.domain.entity.Role

object User {

  object Keys {

    val IdKey = "_id"

  }

}

case class User(id: UUID = UUID.randomUUID(), role: Role, info: Option[UserInfo] = None)

case class UserInfo(name: String)
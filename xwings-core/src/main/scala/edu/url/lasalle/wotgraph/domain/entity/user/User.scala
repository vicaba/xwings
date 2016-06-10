package edu.url.lasalle.wotgraph.domain.entity.user

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Role

object User {

  object Keys {

    val IdKey = "_id"

  }

}

case class User(id: UUID = UUID.randomUUID(), role: Role, info: Option[UserInfo] = None)

case class UserInfo(name: String)
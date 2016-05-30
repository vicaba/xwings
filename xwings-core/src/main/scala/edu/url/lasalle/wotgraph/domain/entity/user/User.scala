package edu.url.lasalle.wotgraph.domain.entity.user

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Role

case class User(id: UUID = UUID.randomUUID(), role: Role, info: Option[UserInfo] = None)

case class UserInfo(name: String)
package wotgraph.app.user.domain.entity

import java.util.UUID

import wotgraph.app.role.domain.entity.Role

case class User(id: UUID = UUID.randomUUID(), role: Role, info: Option[UserInfo] = None)

case class UserInfo(name: String)
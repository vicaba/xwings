package wotgraph.app.user.domain.entity

import java.util.UUID

import wotgraph.app.role.domain.entity.Role

object User {
  type Id = UUID
}

case class User(id: User.Id = UUID.randomUUID(), name: String, password: String, role: Role)

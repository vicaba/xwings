package wotgraph.app.user.application.usecase.dto

import java.util.UUID

import wotgraph.app.role.domain.entity.Role
import wotgraph.app.user.domain.entity.User

object CreateUser {

  def toUser(c: CreateUser): User = User(name = c.name, password = c.password, role = Role(c.role))

}

case class CreateUser(name: String, password: String, role: UUID)
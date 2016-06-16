package wotgraph.app.role.application.dto

import java.util.UUID

import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.role.domain.entity.Role

object CreateRole {

  def toRole(c: CreateRole): Role = Role(name = c.name, permissions = c.permissions.map(Permission(_)))

}

case class CreateRole(name: String, permissions: Set[UUID])
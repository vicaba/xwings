package wotgraph.app.role.application.dto

import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.role.domain.entity.Role

object CreateRole {

  def toRole(c: CreateRole): Role = Role(name = c.name, permissions = c.permissions)

}

case class CreateRole(name: String, permissions: Set[Permission])
package wotgraph.toolkit.application.usecase

import java.util.UUID

import wotgraph.app.permission.domain.entity.Permission


trait PermissionProvider {
  protected val permissionName: String = this.getClass.getSimpleName.filter(_ != '$')
  protected val permissionId: UUID
  lazy val permission: Permission = Permission(permissionId, permissionName)
}

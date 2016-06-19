package wotgraph.toolkit.application.usecase

import wotgraph.app.permission.domain.entity.Permission


trait PermissionsProvider {
  val providers: List[PermissionProvider] = Nil
  lazy val permissions: List[Permission] = providers.map(_.permission)
}

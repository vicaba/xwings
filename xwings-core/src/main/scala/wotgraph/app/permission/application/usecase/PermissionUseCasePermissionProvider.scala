package wotgraph.app.permission.application.usecase

import wotgraph.toolkit.application.usecase.{PermissionProvider, PermissionsProvider}

object PermissionUseCasePermissionProvider extends PermissionsProvider {
  override val providers: List[PermissionProvider] = ListPermissionsUseCase :: Nil
}

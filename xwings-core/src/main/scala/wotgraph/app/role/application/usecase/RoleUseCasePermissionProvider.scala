package wotgraph.app.role.application.usecase

import wotgraph.toolkit.application.usecase.{PermissionProvider, PermissionsProvider}


object RoleUseCasePermissionProvider extends PermissionsProvider {
  override val providers: List[PermissionProvider] =
    CreateRoleUseCase ::
      ListRolesUseCase ::
      Nil
}

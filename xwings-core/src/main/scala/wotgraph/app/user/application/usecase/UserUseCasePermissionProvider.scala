package wotgraph.app.user.application.usecase

import wotgraph.toolkit.application.usecase.{PermissionProvider, PermissionsProvider}


object UserUseCasePermissionProvider extends PermissionsProvider {
  override val providers: List[PermissionProvider] =
    AuthenticateUserUseCase ::
      CreateUserUseCase ::
      DeleteUserUseCase ::
      ListUsersUseCase ::
      UpdateUserUseCase ::
      Nil
}

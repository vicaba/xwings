package wotgraph.app.thing.application.usecase

import wotgraph.toolkit.application.usecase.{PermissionProvider, PermissionsProvider}


object ThingUseCasePermissionProvider extends PermissionsProvider {
  override val providers: List[PermissionProvider] =
    CreateThingUseCase ::
      DeleteThingUseCase ::
      ExecuteThingActionUseCase ::
      ListThingsUseCase ::
      ShowThingUseCase ::
      UpdateThingUseCase ::
      Nil
}

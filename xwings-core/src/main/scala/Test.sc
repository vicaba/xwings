import java.util.UUID

import wotgraph.toolkit.application.usecase.PermissionProvider

object ListPermissionsUseCase extends PermissionProvider {
  override protected val useCaseId: UUID = UUID.fromString("593904f4-ca97-40d4-b7f3-454c7032ab61")
}

ListPermissionsUseCase.permission


package wotgraph.app.permission.application.usecase

import java.util.UUID

import org.scalactic.{Every, Good, Or}
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.AppError
import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.permission.domain.repository.PermissionRepository
import wotgraph.app.role.application.usecase.CreateRoleUseCase
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListPermissionsUseCase(permsRepository: PermissionRepository, authorizationService: AuthorizationService) {

  def execute()(executorAgentId: UUID): Future[List[Permission] Or Every[AppError]] =
    AuthorizationService.asyncExecute(authorizationService, executorAgentId, CreateRoleUseCase.permission.id) {
      permsRepository.getAll.map(Good(_))
    }
}

object ListPermissionsUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("593904f4-ca97-40d4-b7f3-454c7032ab61")
}

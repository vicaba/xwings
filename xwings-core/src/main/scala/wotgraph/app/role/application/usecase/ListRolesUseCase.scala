package wotgraph.app.role.application.usecase

import java.util.UUID

import org.scalactic.{Every, Good, Or}
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.AppError
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.domain.repository.RoleRepository
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.{ExecutionContext, Future}

class ListRolesUseCase(roleRepository: RoleRepository, authorizationService: AuthorizationService) {

  def execute()(executorAgentId: UUID): Future[List[Role] Or Every[AppError]] =
    AuthorizationService.asyncExecute(authorizationService, executorAgentId, CreateRoleUseCase.permission.id) {
      roleRepository.getAll.map(Good(_))
    }

}

object ListRolesUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("134799fc-28c8-49c1-8224-4e17f1cb32ad")
}

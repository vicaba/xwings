package wotgraph.app.role.application.usecase

import java.util.UUID

import org.scalactic.{Every, Good, Or}
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.AppError
import wotgraph.app.role.application.dto.CreateRole
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.domain.repository.RoleRepository
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.Future

class CreateRoleUseCase(roleRepository: RoleRepository, authorizationService: AuthorizationService) {

  def execute(c: CreateRole)(executorAgentId: UUID): Future[Role Or Every[AppError]] =
    AuthorizationService.asyncExecute(authorizationService, executorAgentId, CreateRoleUseCase.permission.id) {
      roleRepository.create(CreateRole.toRole(c)).map(Good(_))
    }
}

object CreateRoleUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("bdb1d552-80af-4a9f-958b-f779e492a4b2")
}

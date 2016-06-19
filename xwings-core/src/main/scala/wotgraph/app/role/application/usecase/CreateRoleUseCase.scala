package wotgraph.app.role.application.usecase

import java.util.UUID

import wotgraph.app.role.application.dto.CreateRole
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.domain.repository.RoleRepository
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.Future

class CreateRoleUseCase(roleRepository: RoleRepository) {

  def execute(c: CreateRole): Future[Role] = roleRepository.create(CreateRole.toRole(c))

}

object CreateRoleUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("bdb1d552-80af-4a9f-958b-f779e492a4b2")
}

package wotgraph.app.role.application.usecase

import java.util.UUID

import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.domain.repository.RoleRepository
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.{ExecutionContext, Future}

class ListRolesUseCase(roleRepository: RoleRepository) {

  def execute(): Future[List[Role]] = roleRepository.getAll

}

object ListRolesUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("134799fc-28c8-49c1-8224-4e17f1cb32ad")
}

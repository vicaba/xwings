package wotgraph.app.permission.application.usecase

import java.util.UUID

import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.permission.domain.repository.PermissionRepository
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.{ExecutionContext, Future}


class ListPermissionsUseCase(permsRepository: PermissionRepository) {

  def execute()(implicit ec: ExecutionContext): Future[List[Permission]] = permsRepository.getAll

}

object ListPermissionsUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("593904f4-ca97-40d4-b7f3-454c7032ab61")
}

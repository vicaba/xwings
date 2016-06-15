package wotgraph.app.permission.application.usecase

import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.permission.domain.repository.PermissionRepository

import scala.concurrent.{ExecutionContext, Future}

class ListPermissionsUseCase(permsRepository: PermissionRepository) {

  def execute()(implicit ec: ExecutionContext): Future[List[Permission]] = permsRepository.getAll

}

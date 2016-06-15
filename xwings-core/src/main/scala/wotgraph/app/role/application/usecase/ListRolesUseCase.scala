package wotgraph.app.role.application.usecase

import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.domain.repository.RoleRepository

import scala.concurrent.{ExecutionContext, Future}

class ListRolesUseCase(roleRepository: RoleRepository) {

  def execute()(implicit ec: ExecutionContext): Future[List[Role]] = roleRepository.getAll

}

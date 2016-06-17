package wotgraph.app.role.application.usecase

import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.domain.repository.RoleRepository

import scala.concurrent.{ExecutionContext, Future}

class ListRolesUseCase(roleRepository: RoleRepository) {

  def execute(): Future[List[Role]] = roleRepository.getAll

}

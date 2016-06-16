package wotgraph.app.role.application.usecase

import wotgraph.app.role.application.dto.CreateRole
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.domain.repository.RoleRepository

import scala.concurrent.Future

class CreateRoleUseCase(roleRepository: RoleRepository) {

  def execute(c: CreateRole): Future[Role] = roleRepository.create(CreateRole.toRole(c))

}

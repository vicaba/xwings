package wotgraph.app.user.application.usecase


import java.util.UUID

import org.scalactic.{Every, Or}
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.AppError
import wotgraph.app.user.application.usecase.dto.CreateUser
import wotgraph.app.user.domain.entity.User
import wotgraph.app.user.domain.repository.UserRepository
import wotgraph.toolkit.application.usecase.PermissionProvider
import wotgraph.toolkit.crypt.Hasher

import scala.concurrent.Future


class CreateUserUseCase(userRepository: UserRepository, hash: Hasher.PreconfiguredHash, authorizationService: AuthorizationService) {

  def execute(c: CreateUser)(executorAgentId: UUID): Future[User Or Every[AppError]] = {
    AuthorizationService.asyncExecute(authorizationService, executorAgentId, CreateUserUseCase.permission.id) {
      val user = CreateUser.toUser(c)
      val password = hash(user.password.toCharArray)
      userRepository.create(user.copy(password = password))
    }
  }
}

object CreateUserUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("a2e0c04b-3507-4daa-a783-55d0763306c4")
}



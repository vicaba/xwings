package wotgraph.app.user.application.usecase

import java.util.UUID

import wotgraph.app.user.application.usecase.dto.UserCredentials
import wotgraph.app.user.domain.entity.User
import wotgraph.app.user.domain.repository.UserRepository
import wotgraph.toolkit.application.usecase.PermissionProvider
import wotgraph.toolkit.crypt.Hasher

import scala.concurrent.Future

class AuthenticateUserUseCase(userRepository: UserRepository, hash: Hasher.PreconfiguredHash) {

  def execute(uC: UserCredentials): Future[Option[User]] =
    userRepository.findByCredentials(uC.name, hash(uC.password.toCharArray))

}



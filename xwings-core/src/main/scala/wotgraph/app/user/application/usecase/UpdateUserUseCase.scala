package wotgraph.app.user.application.usecase

import java.util.UUID

import org.scalactic.{Bad, Every, One, Or}
import wotgraph.app.error.{AppError, ValidationError}
import wotgraph.app.user.application.usecase.dto.CreateUser
import wotgraph.app.user.domain.entity.User
import wotgraph.app.user.domain.repository.UserRepository
import wotgraph.toolkit.crypt.Hasher

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class UpdateUserUseCase(userRepository: UserRepository, hash: Hasher.PreconfiguredHash) {

  def execute(id: String, u: CreateUser): Future[User Or Every[AppError]] = {

    Try(UUID.fromString(id)) match {
      case Failure(_) => Future(Bad(One(ValidationError.WrongUuidFormat)))
      case Success(uuid) =>
        val password = hash(u.password.toCharArray)
        userRepository.update(CreateUser.toUser(u).copy(id = uuid, password = password))
    }
  }

}

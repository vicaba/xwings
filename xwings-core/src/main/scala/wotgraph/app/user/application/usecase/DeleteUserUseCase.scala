package wotgraph.app.user.application.usecase

import java.util.UUID

import org.scalactic._
import wotgraph.app.error.{AppError, ValidationError}
import wotgraph.app.user.domain.repository.UserRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class DeleteUserUseCase(userRepository: UserRepository) {

  def execute(id: String): Future[UUID Or Every[AppError]] = {
    Try(UUID.fromString(id)) match {
      case Failure(_) => Future(Bad(One(ValidationError.WrongUuidFormat)))
      case Success(uuid) => userRepository.delete(uuid)
    }
  }

}

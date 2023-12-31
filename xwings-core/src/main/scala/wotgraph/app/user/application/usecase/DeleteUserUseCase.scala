package wotgraph.app.user.application.usecase

import java.util.UUID

import org.scalactic._
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.{AppError, ValidationError}
import wotgraph.app.user.domain.repository.UserRepository
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class DeleteUserUseCase(userRepository: UserRepository, authorizationService: AuthorizationService) {

  def execute(id: String)(executorAgentId: UUID): Future[UUID Or Every[AppError]] = {
    AuthorizationService.asyncExecute(authorizationService, executorAgentId, DeleteUserUseCase.permission.id) {
      Try(UUID.fromString(id)) match {
        case Failure(_) => Future(Bad(One(ValidationError.WrongUuidFormat)))
        case Success(uuid) => userRepository.delete(uuid)
      }
    }
  }

}

object DeleteUserUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("b31a2955-5791-4fe0-a7c2-b23b7a9075c1")
}

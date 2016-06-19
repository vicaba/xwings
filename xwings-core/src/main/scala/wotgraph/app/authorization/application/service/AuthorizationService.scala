package wotgraph.app.authorization.application.service

import java.util.UUID

import org.scalactic.{Bad, Every, One, Or}
import wotgraph.app.authorization.domain.repository.AuthorizationRepository
import wotgraph.app.error.{AppError, AuthorizationError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthorizationService(authorizationRepository: AuthorizationRepository) {
  def execute(nodeId: UUID, useCaseId: UUID): Future[Boolean] = {
    if (nodeId == AuthorizationService.BypassUUID) Future.successful(true)
    else authorizationRepository.isNodeAllowedToExecuteUseCase(nodeId, useCaseId)
  }
}

object AuthorizationService {

  val BypassUUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

  def asyncExecute[T](authorizationService: AuthorizationService,
                      nodeId: UUID,
                      useCaseId: UUID)
                     (e: => Future[T Or Every[AppError]]): Future[T Or Every[AppError]] = {
    authorizationService.execute(nodeId, useCaseId).flatMap {
      case true => e
      case false => Future.successful(Bad(One(AuthorizationError.NotAuthorized)))
    }
  }

  def execute[T](authorizationService: AuthorizationService,
                 nodeId: UUID,
                 useCaseId: UUID)
                (e: => T Or Every[AppError]): Future[T Or Every[AppError]] = {
    authorizationService.execute(nodeId, useCaseId).map {
      case true => e
      case false => Bad(One(AuthorizationError.NotAuthorized))
    }
  }
}
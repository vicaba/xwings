package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalactic._
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.{AppError, AuthorizationError, ValidationError}
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}


class DeleteThingUseCase(thingRepository: ThingRepository, authorizationService: AuthorizationService) {

  def execute(id: String)(userId: User.Id): Future[UUID Or Every[AppError]] = {
    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.successful(Bad(One(ValidationError.WrongUuidFormat)))
      case Success(uuid) =>
        authorizationService.execute(userId, DeleteThingUseCase.permission.id).flatMap {
          case true => thingRepository.delete(uuid).map(Good(_))
          case false => Future.successful(Bad(One(AuthorizationError.NotAuthorized)))
        }
    }
  }

}

object DeleteThingUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("bc51f20f-32f0-48ea-8d22-fde5117d695f")
}


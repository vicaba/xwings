package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalactic._
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.{AppError, ValidationError}
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class ShowThingUseCase(thingRepository: ThingRepository, authorizationService: AuthorizationService) {

  def execute(id: String)(userId: User.Id): Future[Option[Thing] Or Every[AppError]] = {

    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.successful(Bad(One(ValidationError.WrongUuidFormat)))
      case Success(uuid) =>
        AuthorizationService.executeAsync(authorizationService, userId, ShowThingUseCase.permission.id) {
          thingRepository.findById(uuid).map(Good(_))
        }
    }
  }

}

object ShowThingUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("f0aa6864-83ee-4a6e-9346-9dee732f6453")
}




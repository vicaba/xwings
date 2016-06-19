package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalactic._
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.{AppError, AuthorizationError}
import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateThingUseCase(thingRepository: ThingRepository, authorizationService: AuthorizationService) {

  def execute(c: CreateThing)(userId: User.Id): Future[Thing Or Every[AppError]] =
    authorizationService.execute(userId, CreateThingUseCase.permission.id).flatMap {
      case true => thingRepository.create(CreateThing.toThing(c)).map(Good(_))
      case false => Future.successful(Bad(One(AuthorizationError.NotAuthorized)))
    }

}

object CreateThingUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("dd9728e1-2962-49a2-a3b8-66516128dbb6")
}

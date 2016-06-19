package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalactic._
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.{AppError, ValidationError}
import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class UpdateThingUseCase(thingRepository: ThingRepository, authorizationService: AuthorizationService) {

  def execute(id: String, c: CreateThing)(executorAgentId: UUID): Future[Option[Thing] Or Every[AppError]] = {
    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.successful(Bad(One(ValidationError.WrongUuidFormat)))
      case Success(uuid) =>
        AuthorizationService.asyncExecute(authorizationService, executorAgentId, ShowThingUseCase.permission.id) {
          val thing = CreateThing.toThing(c).copy(_id = uuid)
          thingRepository.update(thing).map(Good(_))
        }
    }
  }
}

object UpdateThingUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("b1ab681b-36e4-492b-9074-b992f771d205")
}


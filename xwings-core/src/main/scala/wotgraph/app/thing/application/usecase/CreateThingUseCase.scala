package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalactic._
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.AppError
import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateThingUseCase(thingRepository: ThingRepository, authorizationService: AuthorizationService) {

  def execute(c: CreateThing)(executorAgentId: UUID): Future[Thing Or Every[AppError]] =
    AuthorizationService.asyncExecute(authorizationService, executorAgentId, CreateThingUseCase.permission.id) {
      thingRepository.create(CreateThing.toThing(c)).map(Good(_))
    }
}

object CreateThingUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("dd9728e1-2962-49a2-a3b8-66516128dbb6")
}

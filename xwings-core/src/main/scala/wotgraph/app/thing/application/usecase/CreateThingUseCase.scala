package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalactic._
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.{AppError, Validation}
import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.thing.infrastructure.service.thing.ThingTransformer
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateThingUseCase(
                          thingRepository: ThingRepository,
                          authorizationService: AuthorizationService,
                          thingTransformer: ThingTransformer) {

  def execute(c: CreateThing)(executorAgentId: UUID): Future[Thing Or Every[AppError]] = {
    AuthorizationService.asyncExecute(authorizationService, executorAgentId, CreateThingUseCase.permission.id) {
      val transformedThing = thingTransformer(CreateThing.toThing(c))
      Thing.ensureCorrect(transformedThing) match {
        case Good(_) => thingRepository.create(transformedThing).map(Good(_))
        case b: Bad[_, _] => Future.successful(b)
      }
    }
  }
}

object CreateThingUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("dd9728e1-2962-49a2-a3b8-66516128dbb6")
}

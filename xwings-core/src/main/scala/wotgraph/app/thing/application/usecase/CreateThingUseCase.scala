package wotgraph.app.thing.application.usecase

import java.util.UUID

import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.Future

class CreateThingUseCase(thingRepository: ThingRepository) {

  def execute(c: CreateThing)(userId: User.Id): Future[Thing] = thingRepository.create(CreateThing.toThing(c))

}

object CreateThingUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("dd9728e1-2962-49a2-a3b8-66516128dbb6")
}

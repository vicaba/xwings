package wotgraph.app.thing.application.usecase

import java.util.UUID

import wotgraph.app.exceptions.ClientFormatException
import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class UpdateThingUseCase(thingRepository: ThingRepository) {

  def execute(id: String, c: CreateThing)(userId: User.Id): Future[Option[Thing]] = {

    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.failed(new ClientFormatException("UUID not provided"))
      case Success(uuid) =>
        val thing = CreateThing.toThing(c).copy(_id = uuid)
        thingRepository.update(thing)
    }

  }

}

object UpdateThingUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("b1ab681b-36e4-492b-9074-b992f771d205")
}


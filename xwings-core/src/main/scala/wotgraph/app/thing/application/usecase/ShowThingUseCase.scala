package wotgraph.app.thing.application.usecase

import java.util.UUID

import wotgraph.app.exceptions.ClientFormatException
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class ShowThingUseCase(thingRepository: ThingRepository) {

  def execute(id: String)(userId: User.Id): Future[Option[Thing]] = {
    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.failed(new ClientFormatException("UUID not provided"))
      case Success(uuid) => thingRepository.findById(uuid)
    }
  }

}

object ShowThingUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("f0aa6864-83ee-4a6e-9346-9dee732f6453")
}




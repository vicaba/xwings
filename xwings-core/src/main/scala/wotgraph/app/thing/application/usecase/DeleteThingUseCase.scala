package wotgraph.app.thing.application.usecase

import java.util.UUID

import wotgraph.app.exceptions.ClientFormatException
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}


class DeleteThingUseCase(thingRepository: ThingRepository) {

  def execute(id: String)(userId: User.Id): Future[UUID] = {
    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.failed(new ClientFormatException("UUID not provided"))
      case Success(uuid) => thingRepository.delete(uuid)
    }
  }

}

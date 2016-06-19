package wotgraph.app.thing.application.usecase

import java.util.UUID

import wotgraph.app.thing.application.usecase.dto.GetThings
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.{ExecutionContext, Future}


class ListThingsUseCase(thingRepository: ThingRepository) {

  def execute(g: GetThings = GetThings(0, 100))(userId: User.Id): Future[List[Thing]] =
    thingRepository.getAll(g.itemPerPage * g.pageNumber, g.itemPerPage)

  def executeAsStream(userId: User.Id) = thingRepository.getAllAsStream

}

object ListThingsUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("6006f27b-edad-4b91-aee4-6f4c477cad9a")
}

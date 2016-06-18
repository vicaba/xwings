package wotgraph.app.thing.application.usecase

import wotgraph.app.thing.application.usecase.dto.GetThings
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User

import scala.concurrent.{ExecutionContext, Future}


class ListThingsUseCase(thingRepository: ThingRepository) {

  def execute(g: GetThings = GetThings(0, 100))(userId: User.Id): Future[List[Thing]] =
    thingRepository.getAll(g.itemPerPage * g.pageNumber, g.itemPerPage)

  def executeAsStream(userId: User.Id) = thingRepository.getAllAsStream

}

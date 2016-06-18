package wotgraph.app.thing.application.usecase

import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.user.domain.entity.User

import scala.concurrent.Future

class CreateThingUseCase(thingRepository: ThingRepository) {

  def execute(c: CreateThing)(userId: User.Id): Future[Thing] = thingRepository.create(CreateThing.toThing(c))

}

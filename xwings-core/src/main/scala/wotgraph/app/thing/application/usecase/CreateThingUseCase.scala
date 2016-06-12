package wotgraph.app.thing.application.usecase

import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.domain.repository.ThingRepository

import scala.concurrent.Future

class CreateThingUseCase(thingRepository: ThingRepository) {

  def execute(c: CreateThing): Future[Thing] = {

    val thing = CreateThing.toThing(c)

    thingRepository.create(thing)
  }


}

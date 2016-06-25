package wotgraph.app.thing.infrastructure.service.action.context
import java.util.UUID

import wotgraph.app.thing.application.service.action.ThingAndAction
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.AvailableContexts
import wotgraph.app.thing.infrastructure.service.action.context.db.WriteToDatabaseTransformer

object Main {

  def main(args: Array[String]) {

    val a = Action(actionName = "getConsume", contextId = AvailableContexts.WriteToDatabaseContext)

    val r = defaultActionTransformer(UUID.randomUUID(), List(a))

    println(r)

  }

}

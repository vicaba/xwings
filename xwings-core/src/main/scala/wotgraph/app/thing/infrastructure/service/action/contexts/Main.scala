package wotgraph.app.thing.infrastructure.service.action.contexts
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.AvailableContexts
import wotgraph.app.thing.infrastructure.service.action.contexts.writedb.WriteDbTransformer

object Main {

  def main(args: Array[String]) {

    val a = Action(actionName = "getConsume", contextId = AvailableContexts.WriteToDatabaseContext)

    val r = actionTransformer(a)

    println(r)

  }

}

package wotgraph.app.thing.infrastructure.service.action.contexts.writedb

import java.util.UUID

import wotgraph.app.thing.application.service.action.ActionTransformer
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.AvailableContexts


class WriteDbTransformer extends ActionTransformer {

  override val contextId: UUID = AvailableContexts.WriteToDatabaseContext


  val GetWordPrefix = "getOf"

  override def transform(a: Action): List[Action] = {
      val aName = GetWordPrefix + a.actionName.capitalize
      val action = Action(actionName = aName, contextId = AvailableContexts.ReadFromDatabaseContext, "")
      a :: action :: Nil
  }
}

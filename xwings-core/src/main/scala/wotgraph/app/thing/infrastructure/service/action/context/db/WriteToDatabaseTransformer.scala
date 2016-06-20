package wotgraph.app.thing.infrastructure.service.action.context.db

import java.util.UUID

import wotgraph.app.thing.application.service.action.{ActionTransformer, ThingAndAction}
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.AvailableContexts


class WriteToDatabaseTransformer extends ActionTransformer {

  override val contextId: UUID = AvailableContexts.WriteToDatabaseContext

  override def transform(ta: ThingAndAction): List[Action] = ta.action :: ReadFromDatabaseContext.createAction(ta)
}

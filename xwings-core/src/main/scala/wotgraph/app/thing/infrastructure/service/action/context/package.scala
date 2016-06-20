package wotgraph.app.thing.infrastructure.service.action

import java.util.UUID

import wotgraph.app.thing.application.service.action.{ActionTransformer, ThingAndAction}
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.context.db.WriteToDatabaseTransformer

package object context {

  private val transformers: ActionTransformer = new WriteToDatabaseTransformer

  val actionTransformer = transformers.orElse(new ActionTransformer {
    override def transform(ta: ThingAndAction): List[Action] = List(ta.action)

    override val contextId: UUID = null

    override def aroundTransform(ta: ThingAndAction): List[Action] = transform(ta)
  })

}

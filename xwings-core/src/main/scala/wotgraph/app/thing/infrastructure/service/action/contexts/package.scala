package wotgraph.app.thing.infrastructure.service.action

import java.util.UUID

import wotgraph.app.thing.application.service.action.ActionTransformer
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.contexts.writedb.WriteDbTransformer

package object contexts {

  private val transformers: ActionTransformer = new WriteDbTransformer

  val actionTransformer = transformers.orElse(new ActionTransformer {
    override def transform(a: Action): List[Action] = List(a)

    override val contextId: UUID = null

    override def aroundTransform(a: Action): List[Action] = transform(a)
  })

}

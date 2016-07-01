package wotgraph.app.thing.infrastructure.service.action

import java.util.UUID

import wotgraph.app.thing.application.service.action.{ActionTransformer, ThingAndAction}
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.context.db.write.WriteToDatabaseTransformer

package object context {

  val defaultTransformers: List[ActionTransformer] = new WriteToDatabaseTransformer :: Nil

  def defaultActionTransformer(thingId: UUID, actions: List[Action]) = {
    ActionTransformer.transform(thingId, actions, defaultTransformers)
  }
}

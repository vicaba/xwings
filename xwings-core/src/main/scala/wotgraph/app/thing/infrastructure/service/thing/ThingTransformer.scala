package wotgraph.app.thing.infrastructure.service.thing

import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.infrastructure.service.action.context._


class ThingTransformer extends (Thing => Thing) {
  override def apply(v1: Thing): Thing = {
    val actions = defaultActionTransformer(v1._id, v1.actions.toList)
    v1.copy(actions = v1.actions ++ actions.toSet)
  }
}

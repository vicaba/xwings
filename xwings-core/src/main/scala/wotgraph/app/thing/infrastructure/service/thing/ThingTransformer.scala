package wotgraph.app.thing.infrastructure.service.thing

import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.infrastructure.service.action.contexts._


class ThingTransformer extends (Thing => Thing) {
  override def apply(v1: Thing): Thing = {
    val actions = v1.actions.flatMap(actionTransformer.apply)
    v1.copy(actions = actions)
  }
}

package wotgraph.app.thing.infrastructure.service.thing

import wotgraph.app.thing.application.service.action.ThingAndAction
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.infrastructure.service.action.context._


class ThingTransformer extends (Thing => Thing) {
  override def apply(v1: Thing): Thing = {
    val actions = v1.actions.flatMap(a => actionTransformer.apply(ThingAndAction(v1._id, a)))
    v1.copy(actions = actions)
  }
}

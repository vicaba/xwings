package wotgraph.app.thing.domain.entity

import java.util.UUID

case class Action(actionName: String, contextId: UUID, contextValue: String)
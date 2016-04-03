package edu.url.lasalle.wotgraph.domain.thing

import java.util.UUID

case class Action(actionName: String, contextId: UUID, contextValue: String, thingId: UUID = UUID.randomUUID())
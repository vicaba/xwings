package application

import java.util.UUID

case class Action(actionName: String, contextId: UUID, contextValue: String)

case class Thing(id: UUID = UUID.randomUUID(), humanName: String, action: Action, labels: List[String], relations: List[String])
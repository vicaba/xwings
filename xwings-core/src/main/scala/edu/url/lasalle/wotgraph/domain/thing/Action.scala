package edu.url.lasalle.wotgraph.domain.thing

import java.util.UUID

import play.api.libs.json.JsObject

case class Action(actionName: String, contextId: UUID, contextValue: String)
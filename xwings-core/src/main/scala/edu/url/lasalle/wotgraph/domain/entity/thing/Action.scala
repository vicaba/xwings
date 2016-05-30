package edu.url.lasalle.wotgraph.domain.entity.thing

import java.util.UUID

import play.api.libs.json.JsObject

case class Action(actionName: String, contextId: UUID, contextValue: String)
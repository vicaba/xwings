package edu.url.lasalle.wotgraph.domain.thing

import java.util.UUID

import play.api.libs.json.JsObject

case class Metadata(data: JsObject, thingId: UUID = UUID.randomUUID())

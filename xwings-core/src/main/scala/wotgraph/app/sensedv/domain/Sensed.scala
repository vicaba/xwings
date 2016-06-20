package wotgraph.app.sensedv.domain

import java.time.Instant
import java.util.{Date, UUID}

import play.api.libs.json.JsValue

case class Sensed(id: UUID = UUID.randomUUID(), path: String, date: Date = Date.from(Instant.now()), data: JsValue)
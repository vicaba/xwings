package wotgraph.app.sensedv.domain

import java.time.Instant
import java.util.{Date, UUID}

import play.api.libs.json.JsValue

case class Sensed(id: UUID = UUID.randomUUID(), date: Date = Date.from(Instant.now()), path: String, data: JsValue)
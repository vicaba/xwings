package wotgraph.app.sensedv.domain

import java.time.Instant
import java.util.{Date, UUID}

import play.api.libs.json.JsObject

case class SensedValue(id: UUID = UUID.randomUUID(), namespace: String, date: Date = Date.from(Instant.now()), data: JsObject)
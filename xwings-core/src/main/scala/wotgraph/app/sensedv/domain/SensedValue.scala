package wotgraph.app.sensedv.domain

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json.JsObject

case class SensedValue(id: UUID = UUID.randomUUID(), namespace: String, date: DateTime = DateTime.now(), data: JsObject)
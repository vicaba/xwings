package edu.url.lasalle.wotgraph.domain.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.{Metadata, Thing}
import play.api.libs.json.JsObject

import scala.concurrent.Future

trait ThingRepository {

  def createThing(t: Thing): Future[Thing]

  def getThing(id: UUID): Future[Option[Thing]]

  def getThings(skip: Int = 0, limit: Int = 1000): Future[List[Thing]]

  def getThingMetadata(id: UUID): Future[Option[Metadata]]

}
package domain.thing.repository

import java.util.UUID

import play.api.libs.json.JsValue
import edu.url.lasalle.wotgraph.infrastructure.thing.repository.neo4j.mappings.Thing

import scala.concurrent.Future

trait ThingRepository {

  def createThing(thing: Thing): Future[Thing]

  def getThing(id: UUID): Future[Option[Thing]]

  def getThings(skip: Int = 0, limit: Int = 1000): Future[List[Thing]]

  def getThingInfo(id: UUID): Future[Option[JsValue]]

}
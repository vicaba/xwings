package domain.thing.repository

import java.util.UUID

import application.Thing
import play.api.libs.json.JsValue

import scala.concurrent.Future

trait ThingRepository {

  def createThing(thing: Thing): Future[Thing]

  def getAllThings(skip: Int = 0, limit: Int = 1000): Future[List[Thing]]

  def getThing(id: UUID): Future[Option[Thing]]

  def getThingInfo(id: UUID): Future[Option[JsValue]]

  def getThingActions(id: UUID): Future[List[Thing]]

  def getThingRelations(id: UUID): Future[String]

  def getThingGraph(id: UUID, depth: Int = 1): Future[List[Thing]]

  def getThingChildren(id: UUID): Future[List[Thing]]

}
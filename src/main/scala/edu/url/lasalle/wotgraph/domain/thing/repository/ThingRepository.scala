package domain.thing.repository

import java.util.UUID

import application.Thing

import scala.concurrent.Future

trait ThingRepository {

  def getThing(id: UUID): Future[Option[Thing]]

  def getThingInfo(id: UUID): Future[List[Thing]]

  def getThingActions(id: UUID): Future[List[Thing]]

  def getThingRelations(id: UUID): Future[String]

  def getThingAndChildren(id: UUID, depth: Int = 1): Future[List[Thing]]

  def getThingChildren(id: UUID): Future[List[Thing]]

}
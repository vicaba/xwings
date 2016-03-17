package domain.thing.repository

import java.util.UUID

import application.Thing

import scala.concurrent.Future

trait ThingRepository {

  def getAll(skip: Int = 0, limit: Int = 1000): Future[List[Thing]]

  def getThing(id: UUID): Future[Option[Thing]]

  def getThingInfo(id: UUID): Future[List[Thing]]

  def getThingActions(id: UUID): Future[List[Thing]]

  def getThingRelations(id: UUID): Future[String]

  def getThingGraph(id: UUID, depth: Int = 1): Future[List[Thing]]

  def getThingChildren(id: UUID): Future[List[Thing]]

}
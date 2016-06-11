package wotgraph.app.thing.domain.repository

import java.util.UUID

import wotgraph.app.thing.domain.entity.Thing
import play.api.libs.iteratee.Enumerator

import scala.concurrent.Future

/**
  * Created by vicaba on 11/06/16.
  */
trait ThingRepository {

  def create(thing: Thing): Future[Thing]

  def update(thing: Thing): Future[Option[Thing]]

  def delete(id: UUID): Future[UUID]

  def findById(id: UUID): Future[Option[Thing]]

  def getAll(skip: Int = 0, limit: Int = 1000): Future[List[Thing]]

  def getAllAsStream: Enumerator[Thing]

  def deleteAll(): Unit

}

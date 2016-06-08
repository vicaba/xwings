package edu.url.lasalle.wotgraph.domain.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.thing.{Metadata, Thing}
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.JsObject

import scala.concurrent.Future

trait ThingRepository {

  def create(thing: Thing): Future[Thing]

  def update(thing: Thing): Future[Option[Thing]]

  def delete(id: UUID): Future[UUID]

  def findById(id: UUID): Future[Option[Thing]]

  def getAll(skip: Int = 0, limit: Int = 1000): Future[List[Thing]]

  def getAllAsStream: Enumerator[Thing]

  def deleteAll(): Unit

}
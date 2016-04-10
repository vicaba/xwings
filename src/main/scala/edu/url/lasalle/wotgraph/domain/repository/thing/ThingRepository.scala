package edu.url.lasalle.wotgraph.domain.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.{Metadata, Thing}
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.JsObject

import scala.concurrent.Future

trait ThingRepository {

  /**
    * Creates a Thing
    * @param thing the Thing
    * @return the new thing
    */
  def createThing(thing: Thing): Future[Thing]

  def deleteThing(id: UUID): Future[UUID]

  /**
    *
    * @param id
    * @return
    */
  def findThingById(id: UUID): Future[Option[Thing]]

  def getThings(skip: Int = 0, limit: Int = 1000): Future[List[Thing]]

  def getThingsAsStream: Enumerator[Thing]

}
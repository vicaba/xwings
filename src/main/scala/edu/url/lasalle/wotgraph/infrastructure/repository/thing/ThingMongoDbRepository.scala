package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.{DeleteException, ReadException, SaveException}
import edu.url.lasalle.wotgraph.domain.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.MongoCRUDService
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.Implicits._
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB

import scala.concurrent.{ExecutionContext, Future}


case class ThingMongoDbRepository(db: DB)(implicit ec: ExecutionContext) {

  val mongoService: MongoCRUDService[Thing, UUID] = {
    new MongoCRUDService[Thing, UUID]() {

      override val collection: JSONCollection = db.collection("thing")

      override def getIdFromEntity(entity: Thing): UUID = entity._id

      override val identityName: String = "_id"

    }
  }

  def findThingById(id: UUID) = mongoService.findById(id) recover {
    case t: Throwable => throw new ReadException(s"Can't get Thing with id: $id") }

  def getThings = mongoService.findByCriteria(Json.obj()) recover {
    case t: Throwable => throw new ReadException("Can't get Things") }

  def createThing(t: Thing) = mongoService.create(t) flatMap {
    case Right(thing) => Future.successful(thing)
    case Left(w) => Future.failed(new SaveException(s"Failed to create thing with id ${t._id}"))
  }

  def deleteThing(t: UUID) = mongoService.delete(t) flatMap {
    case Right(thing) => Future.successful(thing)
    case Left(w) => Future.failed(new DeleteException(s"Failed to delete thing with id $t"))
  }

}

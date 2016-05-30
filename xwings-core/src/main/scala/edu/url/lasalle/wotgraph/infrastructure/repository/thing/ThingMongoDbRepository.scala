package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.{DeleteException, ReadException, SaveException, UpdateException}
import edu.url.lasalle.wotgraph.domain.entity.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.MongoCRUDService
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.Implicits._
import play.api.libs.iteratee.Enumerator
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

  def findById(id: UUID) = mongoService.findById(id) recover {
    case t: Throwable => throw new ReadException(s"Can't get Thing with id: $id") }

  def getAll = mongoService.findByCriteria(Json.obj()) recover {
    case t: Throwable => throw new ReadException("Can't get Things") }

  def getAllAsStream: Enumerator[Thing] = mongoService.findStreamByCriteria(Json.obj())

  def create(thing: Thing) = mongoService.create(thing) flatMap {
    case Right(t) => Future.successful(t)
    case Left(w) => Future.failed(new SaveException(s"Failed to create thing with id ${thing._id}"))
  }

  def update(thing: Thing) = mongoService.update(thing) flatMap {
    case Right(t) => Future.successful(t)
    case Left(w) => Future.failed(new UpdateException(s"Failed to update thing with id ${thing._id}"))
  }

  def delete(id: UUID) = mongoService.delete(id) flatMap {
    case Right(thing) => Future.successful(thing)
    case Left(w) => Future.failed(new DeleteException(s"Failed to delete thing with id $id"))
  }

}

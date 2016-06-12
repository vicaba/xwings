package wotgraph.app.thing.infrastructure.repository.mongodb

import java.util.UUID

import wotgraph.app.thing.infrastructure.serialization.format.json.Implicits._
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.exceptions.{DeleteException, ReadException, SaveException, UpdateException}
import wotgraph.toolkit.repository.mongodb.MongoCRUDService
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import reactivemongo.api.DB
import reactivemongo.play.json.collection.JSONCollection
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by vicaba on 11/06/16.
  */
case class ThingMongoDbRepository(db: DB)(implicit ec: ExecutionContext) {

  val mongoService: MongoCRUDService[Thing, UUID] = {
    new MongoCRUDService[Thing, UUID]() {

      override val collection: JSONCollection = db.collection("thing")

      override def getIdFromEntity(entity: Thing): UUID = entity._id

      override val identityName: String = ThingKeys.Id

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

  def deleteAll(): Unit = mongoService.deleteAll()

}

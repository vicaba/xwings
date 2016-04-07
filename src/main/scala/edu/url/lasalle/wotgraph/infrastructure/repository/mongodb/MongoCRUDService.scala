package edu.url.lasalle.wotgraph.infrastructure.repository.mongodb

import edu.url.lasalle.wotgraph.infrastructure.serializers.json.ThingSerializer
import play.api.libs.iteratee.Enumerator
import play.api.libs.json._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.{MultiBulkWriteResult, WriteResult}
import reactivemongo.bson.{BSON, BSONDocument}

import scala.concurrent.{ExecutionContext, Future}

abstract class MongoCRUDService[E, ID](implicit tFormat: OFormat[E], idFormat: Format[ID], ec: ExecutionContext) {

  val collection: JSONCollection

  val identityName: String

  def getIdFromEntity(entity: E): ID

  def findById(id: ID): Future[Option[E]] = {
    collection.find(Json.obj(identityName -> id)).one[E]
  }

  def findStreamByCriteria(criteria: JsObject, limit: Int): Enumerator[E] = {
    collection.find(criteria).cursor[E](readPreference = ReadPreference.primary).enumerate(limit)
  }

  def findStreamByCriteria(criteria: JsObject): Enumerator[E] = {
    collection.find(criteria).cursor[E](readPreference = ReadPreference.primary).enumerate()
  }

  def findByCriteria(criteria: JsObject, limit: Int): Future[Traversable[E]] = {
    collection.find(criteria).cursor[E](readPreference = ReadPreference.primary).collect[List](limit)
  }

  def findByCriteria(criteria: JsObject): Future[Traversable[E]] = {
    collection.find(criteria).cursor[E](readPreference = ReadPreference.primary).collect[List]()
  }

  def findOneByCriteria(criteria: JsObject): Future[Option[E]] = {
    collection.
      find(criteria).
      one[E]
  }

  def create(o: E): Future[Either[WriteResult, E]] = {
    collection.insert(o).map {
      case wr if wr.ok => Right(o)
      case wr => Left(wr)
    }.recover {
      case wr: WriteResult => Left(wr)
    }
  }

  def create(o: Iterable[E]): Future[Either[MultiBulkWriteResult, Iterable[E]]] = {
    val docs = o.toSeq.map(implicitly[collection.ImplicitlyDocumentProducer](_))
    collection.bulkInsert(ordered = false)(docs: _*).map {
      case wr if wr.ok => Right(o)
      case wr => Left(wr)
    }
  }

  def update(o: E): Future[Either[WriteResult, E]] = {
    collection.update(Json.obj(identityName -> getIdFromEntity(o)), o).map {
      case wr if wr.ok => Right(o)
      case wr => Left(wr)
    }
  }

  def delete(id: ID): Future[Either[WriteResult, ID]] = {
    collection.remove(Json.obj(identityName -> id)) map {
      case le if le.ok => Right(id)
      case le => Left(le)
    }
  }

  def delete(selector: JsObject): Future[WriteResult] = {
    collection.remove(selector)
  }
}
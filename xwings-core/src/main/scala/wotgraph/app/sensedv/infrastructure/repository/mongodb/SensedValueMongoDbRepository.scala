package wotgraph.app.sensedv.infrastructure.repository.mongodb

import org.scalactic._
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.{JsObject, Json}
import reactivemongo.api.{DB, ReadPreference}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import wotgraph.app.error.{Storage, StorageError}
import wotgraph.app.sensedv.domain.SensedValue
import wotgraph.app.sensedv.infrastructure.serialization.keys.SensedValueKeys
import reactivemongo.bson._
import wotgraph.app.exceptions.DeleteException
import wotgraph.toolkit.repository.dsl._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class SensedValueMongoDbRepository(db: DB)(implicit ec: ExecutionContext) {

  import wotgraph.app.sensedv.infrastructure.serialization.format.bson.Implicits._

  def ordering2MongoDbOrder(ordering: FieldOrdering): Int = ordering match {
    case FieldOrdering.Ascendant => 1
    case FieldOrdering.Descendant => -1
  }

  implicit def order2Criteria(order: Order): BSONDocument = BSONDocument(order.field -> ordering2MongoDbOrder(order.order))

  def collection: BSONCollection = db.collection("sensed")

  def findLastByNamespace(namespace: String): Future[Option[SensedValue] Or Every[StorageError]] = {
    val criteria = namespaceCriteria(namespace)
    val sortOrder = sortOrderCriteria(-1)
    collection
      .find(criteria)
      .sort(sortOrder)
      .one[SensedValue]
      .map(Good(_))
  }

  def create(sensed: SensedValue): Future[SensedValue Or Every[StorageError]] = {
    collection.insert(sensed).map {
      case wr if wr.ok => Good(sensed)
      case wr => Bad(One(Storage("Error saving entity")))
    }
  }

  def getAll(namespace: String): Future[List[SensedValue] Or Every[StorageError]] = {
    val criteria = namespaceCriteria(namespace)
    val sortOrder = sortOrderCriteria(-1)
    collection
      .find(criteria)
      .sort(sortOrder)
      .cursor[SensedValue](readPreference = ReadPreference.primary)
      .collect[List]().map(Good(_))
  }

  def getAll(namespace: String, orderedBy: Order): Future[List[SensedValue] Or Every[StorageError]] = {
    val criteria = namespaceCriteria(namespace)
    collection
      .find(criteria)
      .sort(orderedBy)
      .cursor[SensedValue](readPreference = ReadPreference.primary)
      .collect[List]().map(Good(_))
  }

  def getAllAsStream(namespace: String): Enumerator[SensedValue] Or Every[StorageError] = {
    val criteria = namespaceCriteria(namespace)
    val sortOrder = sortOrderCriteria(-1)
    Good(collection
      .find(criteria)
      .sort(sortOrder)
      .cursor[SensedValue](readPreference = ReadPreference.primary)
      .enumerate())
  }

  def getAllAsStream(namespace: String, orderedBy: Order): Enumerator[SensedValue] Or Every[StorageError] = {
    val criteria = namespaceCriteria(namespace)
    Good(collection
      .find(criteria)
      .sort(orderedBy)
      .cursor[SensedValue](readPreference = ReadPreference.primary)
      .enumerate())
  }

  def deleteAll(namespace: String): Future[Boolean Or Every[StorageError]] = {
    collection.remove(namespaceCriteria(namespace)).map {
      case wr if wr.ok => Good(true)
      case wr => Bad(One(Storage("Error deleting entity")))
    } recover {
      case t: Throwable => throw new DeleteException("MongoDB error, cannot delete sensed values that belong to " + namespace)
    }
  }

  private def namespaceCriteria(namespace: String): BSONDocument = BSONDocument(SensedValueKeys.Namespace -> namespace)

  private def sortOrderCriteria(order: Int): BSONDocument = BSONDocument(SensedValueKeys.Date -> order)

}

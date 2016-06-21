package wotgraph.app.sensedv.infrastructure.repository.mongodb
/*

import java.util.UUID

import org.scalactic._
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.json._
import reactivemongo.api.{DB, ReadPreference}
import reactivemongo.play.json.collection.JSONCollection
import wotgraph.app.error.{Storage, StorageError}
import wotgraph.app.sensedv.domain.SensedValue
import wotgraph.app.sensedv.infrastructure.serialization.format.json.Implicits._
import wotgraph.app.sensedv.infrastructure.serialization.keys.SensedValueKeys
import wotgraph.toolkit.repository.mongodb.MongoCRUDService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


case class SensedValueMongoDbRepository(db: DB)(implicit ec: ExecutionContext) {

  val mongoService: MongoCRUDService[SensedValue, UUID] =
    new MongoCRUDService[SensedValue, UUID]() {

      override val collection: JSONCollection = db.collection("sensed")

      override def getIdFromEntity(entity: SensedValue): UUID = entity.id

      override val identityName: String = SensedValueKeys.Id

    }

  def create(sensed: SensedValue): Future[SensedValue Or Every[StorageError]] =
    mongoService.create(sensed).map {
      case Right(s) => Good(s)
      case Left(wr) => Bad(One(Storage("DB error")))
    }

  def getAll(namespace: String): Future[List[SensedValue] Or Every[StorageError]] = {
    val criteria = namespaceCriteria(namespace)
    val sortOrder = sortOrderCriteria(1)
    mongoService.collection
      .find(criteria)
      .sort(sortOrder)
      .cursor[SensedValue](readPreference = ReadPreference.primary)
      .collect[List]().map(Good(_))
  }

  def getAllAsStream(namespace: String): Enumerator[SensedValue] Or Every[StorageError] = {
    val criteria = namespaceCriteria(namespace)
    val sortOrder = sortOrderCriteria(1)
    val result = mongoService.collection
      .find(criteria)
      .sort(sortOrder)
      .cursor[SensedValue](readPreference = ReadPreference.primary)
      .enumerate()
    Good(result)
  }

  private def namespaceCriteria(namespace: String): JsObject =
    Json.obj(SensedValueKeys.Namespace -> namespace)

  private def sortOrderCriteria(order: Int): JsObject =
    Json.obj("_id" -> order)




}
*/

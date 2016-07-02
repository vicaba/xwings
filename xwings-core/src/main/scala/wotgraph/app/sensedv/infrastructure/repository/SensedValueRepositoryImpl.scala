package wotgraph.app.sensedv.infrastructure.repository

import org.scalactic.{Every, Or}
import play.api.libs.iteratee.Enumerator
import wotgraph.app.error.StorageError
import wotgraph.app.sensedv.domain.SensedValue
import wotgraph.app.sensedv.domain.repository.SensedValueRepository
import wotgraph.app.sensedv.infrastructure.repository.mongodb.SensedValueMongoDbRepository
import wotgraph.toolkit.repository.dsl._

import scala.concurrent.{ExecutionContext, Future}


case class SensedValueRepositoryImpl(
                                      sensedValueMongoDbRepository: SensedValueMongoDbRepository
                                    )
                                    (implicit ec: ExecutionContext)
  extends SensedValueRepository {

  override def findLastByNamespace(namespace: String): Future[Or[Option[SensedValue], Every[StorageError]]] =
    sensedValueMongoDbRepository.findLastByNamespace(namespace)

  override def create(sensed: SensedValue): Future[Or[SensedValue, Every[StorageError]]] =
    sensedValueMongoDbRepository.create(sensed)

  override def getAll(namespace: String): Future[Or[List[SensedValue], Every[StorageError]]] =
    sensedValueMongoDbRepository.getAll(namespace)

  override def getAll(namespace: String, orderedBy: Order): Future[Or[List[SensedValue], Every[StorageError]]] =
    sensedValueMongoDbRepository.getAll(namespace, orderedBy)

  override def getAllAsStream(namespace: String): Or[Enumerator[SensedValue], Every[StorageError]] =
    sensedValueMongoDbRepository.getAllAsStream(namespace)

  override def getAllAsStream(namespace: String, orderedBy: Order): Or[Enumerator[SensedValue], Every[StorageError]] =
    sensedValueMongoDbRepository.getAllAsStream(namespace, orderedBy)

  override def deleteAll(namespace: String): Future[Or[Boolean, Every[StorageError]]] =
    sensedValueMongoDbRepository.deleteAll(namespace)
}
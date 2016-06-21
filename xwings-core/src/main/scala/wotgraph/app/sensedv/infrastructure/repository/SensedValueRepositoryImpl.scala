package wotgraph.app.sensedv.infrastructure.repository

import org.scalactic.{Every, Or}
import play.api.libs.iteratee.Enumerator
import wotgraph.app.error.StorageError
import wotgraph.app.sensedv.domain.SensedValue
import wotgraph.app.sensedv.domain.repository.SensedValueRepository
import wotgraph.app.sensedv.infrastructure.repository.mongodb.SensedValueMongoDbRepository

import scala.concurrent.{ExecutionContext, Future}


case class SensedValueRepositoryImpl(
                                      sensedValueMongoDbRepository: SensedValueMongoDbRepository
                                    )
                                    (implicit ec: ExecutionContext)
  extends SensedValueRepository {
  override def create(sensed: SensedValue): Future[Or[SensedValue, Every[StorageError]]] =
    sensedValueMongoDbRepository.create(sensed)

  override def getAll(namespace: String): Future[Or[List[SensedValue], Every[StorageError]]] =
    sensedValueMongoDbRepository.getAll(namespace)

  override def getAllAsStream(namespace: String): Or[Enumerator[SensedValue], Every[StorageError]] =
    sensedValueMongoDbRepository.getAllAsStream(namespace)
}
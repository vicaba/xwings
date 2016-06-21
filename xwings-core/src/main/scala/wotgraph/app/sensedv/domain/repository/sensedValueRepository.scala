package wotgraph.app.sensedv.domain.repository

import org.scalactic.{Every, Or}
import play.api.libs.iteratee.Enumerator
import wotgraph.app.error.StorageError
import wotgraph.app.sensedv.domain.SensedValue

import scala.concurrent.Future


trait SensedValueRepository {

  def findLastByNamespace(namespace: String): Future[Or[Option[SensedValue], Every[StorageError]]]

  def create(sensed: SensedValue):  Future[SensedValue Or Every[StorageError]]

  def getAll(namespace: String): Future[List[SensedValue] Or Every[StorageError]]

  def getAllAsStream(namespace: String): Enumerator[SensedValue] Or Every[StorageError]


}

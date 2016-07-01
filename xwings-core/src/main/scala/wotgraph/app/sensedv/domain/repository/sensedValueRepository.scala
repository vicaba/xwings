package wotgraph.app.sensedv.domain.repository

import javassist.runtime.Desc

import org.scalactic.{Every, Or}
import play.api.libs.iteratee.Enumerator
import wotgraph.app.error.StorageError
import wotgraph.app.sensedv.domain.SensedValue

import scala.concurrent.Future

sealed trait FieldOrdering

object FieldOrdering {
  case object Ascendant extends FieldOrdering
  case object Descendant extends FieldOrdering
}

case class Order(field: String, order: FieldOrdering)

trait SensedValueRepository {

  def findLastByNamespace(namespace: String): Future[Or[Option[SensedValue], Every[StorageError]]]

  def create(sensed: SensedValue):  Future[SensedValue Or Every[StorageError]]

  def getAll(namespace: String): Future[List[SensedValue] Or Every[StorageError]]

  def getAllAsStream(namespace: String): Enumerator[SensedValue] Or Every[StorageError]

  def getAllAsStream(namespace: String, orderedBy: Order): Enumerator[SensedValue] Or Every[StorageError]

}

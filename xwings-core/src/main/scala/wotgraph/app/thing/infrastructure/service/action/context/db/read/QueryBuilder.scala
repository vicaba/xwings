package wotgraph.app.thing.infrastructure.service.action.context.db.read

import akka.NotUsed
import akka.stream.scaladsl.Source
import org.scalactic.{Bad, Every, Good, Or}
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.{JsValue, Writes}
import play.api.libs.streams.Streams
import wotgraph.app.error.StorageError
import wotgraph.app.sensedv.domain.SensedValue
import wotgraph.app.sensedv.domain.repository.SensedValueRepository
import wotgraph.app.thing.application.service.action._
import wotgraph.toolkit.repository.dsl._
import wotgraph.toolkit.scalactic.ErrorHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * DISCLAIMER! This is NOT a query builder. It's only purpose is to reduce code duplication and ease coding of
  * [[wotgraph.app.thing.infrastructure.service.action.context.db.read.ReadFromDatabaseContext]]
  *
  * @tparam Q This builder (the Builder that implements this trait)
  */
trait QueryBuilder[Q <: QueryBuilder[_]] {

  val thisBuilder: Q

  def execute(sensedValueRepository: SensedValueRepository): Future[ExecutionResult]

}

object QueryBuilder {
  def apply(query: String, namespace: String): QueryQueryBuilder = QueryQueryBuilder(query, namespace)

  def apply(namespace: String): QueryQueryBuilder = QueryQueryBuilder("", namespace)

  def apply(): (String, String) => QueryQueryBuilder = QueryQueryBuilder(_: String, _: String)
}

case class QueryQueryBuilder(query: String, namespace: String) extends QueryBuilder[QueryQueryBuilder] {

  import Queries._
  import QueryQueryBuilder._

  override val thisBuilder: QueryQueryBuilder = this

  override def execute(sensedValueRepository: SensedValueRepository): Future[ExecutionResult] = query match {
    case AllStream => getAllAsStream(sensedValueRepository, namespace)
    case All => getAll(sensedValueRepository, namespace)
    case _ => getLast(sensedValueRepository, namespace)
  }

  def sort(order: Order) = new SortQueryBuilder {
    override val queryQueryBuilder: QueryQueryBuilder = QueryQueryBuilder.this.thisBuilder
    override val _order: Order = order
  }

}

object QueryQueryBuilder {

  val AllStream = "allStream"
  val All = "all"

}

trait SortQueryBuilder extends QueryBuilder[SortQueryBuilder] {

  import Queries._
  import QueryQueryBuilder._

  override val thisBuilder: SortQueryBuilder = this

  val queryQueryBuilder: QueryQueryBuilder

  val _order: Order

  override def execute(sensedValueRepository: SensedValueRepository): Future[ExecutionResult] = {
    queryQueryBuilder.query match {
      case AllStream => getAllAsStream(sensedValueRepository, queryQueryBuilder.namespace, _order)
      case All => getAll(sensedValueRepository, queryQueryBuilder.namespace, _order)
      case _ => queryQueryBuilder.execute(sensedValueRepository)
    }
  }
}

private object Queries {

  def getLast(sensedValueRepository: SensedValueRepository, namespace: String): Future[ExecutionResult] = {
    sensedValueRepository.findLastByNamespace(namespace).map {
      case Good(svOpt) => svOpt.map(v => StringExecutionSuccess(v.data.toString)).getOrElse(StringExecutionSuccess(""))
      case Bad(errors) => errors2Failure(errors)
    }
  }

  def getAll(sensedValueRepository: SensedValueRepository, namespace: String) = {
    mapAsyncResult(sensedValueRepository.getAll(namespace))(_.data)

  }

  def getAll(sensedValueRepository: SensedValueRepository, namespace: String, order: Order) = {
    mapAsyncResult(sensedValueRepository.getAll(namespace, order))(_.data)
  }

  def getAllAsStream(sensedValueRepository: SensedValueRepository, namespace: String): Future[ExecutionResult] = {
    mapEnumeratorResult(sensedValueRepository.getAllAsStream(namespace))(_.data.toString)
  }

  def getAllAsStream(sensedValueRepository: SensedValueRepository, namespace: String, order: Order): Future[ExecutionResult] = {
    mapEnumeratorResult(sensedValueRepository.getAllAsStream(namespace, order))(_.data.toString)
  }

  private def mapAsyncResult[T](
                                 res: => Future[Seq[T] Or Every[StorageError]]
                               )
                               (
                                 f: T => JsValue
                               ): Future[ExecutionResult] = {
    res.map {
      case Good(l) => StringExecutionSuccess(Writes.seq[JsValue].writes(l.map(f)).toString)
      case Bad(errors) => errors2Failure(errors)
    }


  }

  private def mapEnumeratorResult[T](
                                      res: => Enumerator[T] Or Every[StorageError]
                                    )
                                    (
                                      f: T => String
                                    ): Future[ExecutionResult] = {

    Future.successful(res match {
      case Good(e) => StreamExecutionSuccess(enumeratorToStream(e).map(f))
      case Bad(errors) => errors2Failure(errors)
    })

  }


  private def errors2Failure(errors: Every[StorageError]): ExecutionFailure =
    ExecutionFailure(ErrorHelper.every2List(errors).map(_.toString))

  private def enumeratorToStream[T](e: Enumerator[T]): Source[T, NotUsed] =
    Source.fromPublisher(Streams.enumeratorToPublisher(e))

}


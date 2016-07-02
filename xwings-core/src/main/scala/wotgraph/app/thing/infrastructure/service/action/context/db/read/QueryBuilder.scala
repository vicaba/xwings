package wotgraph.app.thing.infrastructure.service.action.context.db.read

import akka.NotUsed
import akka.stream.scaladsl.Source
import org.scalactic.{Bad, Every, Good}
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
      case All => queryQueryBuilder.execute(sensedValueRepository)
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
    sensedValueRepository.getAll(namespace) map {
      case Good(l) => StringExecutionSuccess(Writes.list[JsValue].writes(l.map(v => v.data)).toString)
      case Bad(errors) => errors2Failure(errors)
    }
  }

  def getAllAsStream(sensedValueRepository: SensedValueRepository, namespace: String): Future[ExecutionResult] = {

    Future.successful(sensedValueRepository.getAllAsStream(namespace) match {
      case Good(e) => StreamExecutionSuccess(enumeratorToStream(e).map(_.data.toString))
      case Bad(errors) => errors2Failure(errors)
    })
  }

  def getAllAsStream(sensedValueRepository: SensedValueRepository, namespace: String, order: Order): Future[ExecutionResult] = {

    Future.successful(sensedValueRepository.getAllAsStream(namespace, order) match {
      case Good(e) => StreamExecutionSuccess(enumeratorToStream(e).map(_.data.toString))
      case Bad(errors) => errors2Failure(errors)
    })
  }

  private def errors2Failure(errors: Every[StorageError]): ExecutionFailure =
    ExecutionFailure(ErrorHelper.every2List(errors).map(_.toString))

  private def enumeratorToStream(e: Enumerator[SensedValue]): Source[SensedValue, NotUsed] =
    Source.fromPublisher(Streams.enumeratorToPublisher(e))

}


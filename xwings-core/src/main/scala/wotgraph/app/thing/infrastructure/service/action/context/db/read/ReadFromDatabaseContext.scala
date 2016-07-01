package wotgraph.app.thing.infrastructure.service.action.context.db.read

import play.api.libs.json._
import wotgraph.app.sensedv.domain.repository.{FieldOrdering, Order, SensedValueRepository}
import wotgraph.app.sensedv.infrastructure.serialization.keys.SensedValueKeys
import wotgraph.app.thing.application.service.action._
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.AvailableContexts

import scala.concurrent.Future
import scala.util.Try

object ReadFromDatabaseContext {

  private val GetWordPrefix = "getOf"

  private val Query = "query"

  private val SortField = "field"

  private val SortOrder = "order"


  def createAction(ta: ThingAndAction): List[Action] = {
    val a = ta.action
    val aName = GetWordPrefix + a.actionName.capitalize
    val action = Action(
      actionName = aName,
      contextId = AvailableContexts.ReadFromDatabaseContext,
      contextValue = Json.obj(SensedValueKeys.Namespace -> ta.namespace).toString)
    a :: action :: Nil
  }
}

class ReadFromDatabaseContext(sensedValueRepository: SensedValueRepository) extends ActionContext[SensedValueRepository] {

  import ReadFromDatabaseContext._

  override val context: SensedValueRepository = sensedValueRepository

  override def executeAction(ta: ThingAndAction,
                             contextValue: Map[String, String],
                             actionPayload: JsObject
                            ): Future[ExecutionResult] = {

    contextValue.get(SensedValueKeys.Namespace).fold[Future[ExecutionResult]] {
      Future.successful(StringExecutionSuccess(""))
    } { nspace =>
      (actionPayload \ Query).asOpt[String] match {
        case Some(query) => onQueryKey(QueryBuilder(query, nspace), actionPayload)
        case None => QueryBuilder(nspace).execute(sensedValueRepository)
      }
    }
  }

  private def onQueryKey(queryBuilder: QueryQueryBuilder, actionPayload: JsObject): Future[ExecutionResult] = {
    (actionPayload \ "sort").asOpt[JsObject] match {
      case Some(sort) =>

        (for {
          field <- (sort \ SortField).asOpt[String]
          order <- (sort \ SortOrder).asOpt[String].flatMap(o => Try(Integer.parseInt(o)).toOption)
        } yield {
          val ordering = if (order == 1) FieldOrdering.Ascendant else FieldOrdering.Descendant
          Order(s"${SensedValueKeys.Data}.$field", ordering)
        }).fold(queryBuilder.execute(sensedValueRepository))(queryBuilder.sort(_).execute(sensedValueRepository))

      case _ => queryBuilder.execute(sensedValueRepository)
    }
  }

}

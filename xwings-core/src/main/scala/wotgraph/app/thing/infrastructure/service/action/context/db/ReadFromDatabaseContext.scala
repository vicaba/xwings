package wotgraph.app.thing.infrastructure.service.action.context.db

import akka.NotUsed
import akka.stream.scaladsl.Source
import org.scalactic.{Bad, Good}
import play.api.libs.iteratee.Enumerator
import play.api.libs.json._
import play.api.libs.streams.Streams
import wotgraph.app.sensedv.domain.SensedValue
import wotgraph.app.sensedv.domain.repository.SensedValueRepository
import wotgraph.app.sensedv.infrastructure.serialization.keys.SensedValueKeys
import wotgraph.app.thing.application.service.action._
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.AvailableContexts
import wotgraph.toolkit.scalactic.ErrorHelper

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ReadFromDatabaseContext {

  private val GetWordPrefix = "getOf"

  private val Query = "query"
  private val AllStream = "allStream"
  private val All = "all"


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

class ReadFromDatabaseContext(sensedRepository: SensedValueRepository) extends ActionContext[SensedValueRepository] {

  import ReadFromDatabaseContext._

  override val context: SensedValueRepository = sensedRepository

  override def executeAction(ta: ThingAndAction,
                             contextValue: Map[String, String],
                             actionPayload: JsObject
                            ): Future[ExecutionResult] = {

    contextValue.get(SensedValueKeys.Namespace).fold[Future[ExecutionResult]] {
      Future.successful(StringExecutionSuccess(""))
    } { nspace =>
      (actionPayload \ Query).asOpt[String] match {
        case Some(query) => query match {
          case AllStream => getAllAsStream(nspace)
          case All => getAll(nspace)
          case _ => defaultQuery(nspace)
        }
        case None => defaultQuery(nspace)
      }
    }

  }

  private def defaultQuery(nspace: String) = getLast(nspace)

  private def getLast(namespace: String): Future[ExecutionResult] = {
    sensedRepository.findLastByNamespace(namespace).map {
      case Good(svOpt) => svOpt.map(v => StringExecutionSuccess(v.data.toString)).getOrElse(StringExecutionSuccess(""))
      case Bad(errors) => ExecutionFailure(ErrorHelper.every2List(errors).map(_.toString))
    }
  }

  private def getAll(namespace: String) = {
    sensedRepository.getAll(namespace) map {
      case Good(l) => StringExecutionSuccess(Writes.list[JsValue].writes(l.map(v => v.data)).toString)
      case Bad(errors) => ExecutionFailure(ErrorHelper.every2List(errors).map(_.toString))
    }
  }

  private def getAllAsStream(namespace: String): Future[ExecutionResult] = {

    def enumeratorToStream(e: Enumerator[SensedValue]): Source[SensedValue, NotUsed] =
      Source.fromPublisher(Streams.enumeratorToPublisher(e))

    Future.successful(sensedRepository.getAllAsStream(namespace) match {
      case Good(e) => StreamExecutionSuccess(enumeratorToStream(e).map(_.data.toString))
      case Bad(errors) => ExecutionFailure(ErrorHelper.every2List(errors).map(_.toString))
    })
  }

}

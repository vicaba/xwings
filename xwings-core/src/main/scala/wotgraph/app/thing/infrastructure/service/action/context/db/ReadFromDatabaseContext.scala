package wotgraph.app.thing.infrastructure.service.action.context.db

import org.scalactic.{Bad, Good}
import play.api.libs.json.{JsObject, Json}
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

  override val context: SensedValueRepository = sensedRepository

  override def executeAction(ta: ThingAndAction,
                             contextValue: Map[String, String],
                             actionPayload: JsObject
                            ): Future[ExecutionResult] = {

    contextValue.get(SensedValueKeys.Namespace).fold[Future[ExecutionResult]] {
      Future.successful(ExecutionSuccess(""))
    } { nspace =>
      sensedRepository.findLastByNamespace(nspace).map {
        case Good(svOpt) => svOpt.map(v => ExecutionSuccess(v.data.toString)).getOrElse(ExecutionSuccess(""))
        case Bad(errors) => ExecutionFailure(ErrorHelper.every2List(errors).map(_.toString))
      }
    }
  }
}

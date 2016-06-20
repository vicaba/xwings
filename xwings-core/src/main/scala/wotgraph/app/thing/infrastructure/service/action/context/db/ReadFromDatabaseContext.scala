package wotgraph.app.thing.infrastructure.service.action.context.db

import java.util.UUID

import play.api.libs.json.Json
import wotgraph.app.sensedv.domain.repository.SensedRepository
import wotgraph.app.thing.application.service.action.{ActionContext, ExecutionResult, ThingAndAction}
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.AvailableContexts

import scala.concurrent.Future

object ReadFromDatabaseContext {

  private val GetWordPrefix = "getOf"

  private val ReadFromDatabaseNamespaceKey = "nspace"

  def createAction(ta: ThingAndAction): List[Action] = {
    val a = ta.action
    val aName = GetWordPrefix + a.actionName.capitalize
    val action = Action(actionName = aName, contextId = AvailableContexts.ReadFromDatabaseContext, contextValue(ta))
    a :: action :: Nil
  }

  private def contextValue(ta: ThingAndAction): String =
    Json.obj(ReadFromDatabaseNamespaceKey -> namespace(ta)).toString()

  private def namespace(ta: ThingAndAction) = s"${ta.thingId}/${ta.action.actionName}"

}

class ReadFromDatabaseContext(sensedRepository: SensedRepository) extends ActionContext[SensedRepository] {

  override val context: SensedRepository = sensedRepository

  override def executeAction(thingId: UUID, contextValue: Map[String, String]): Future[ExecutionResult] = ???
}

package wotgraph.app.thing.infrastructure.service.action.context.db

import java.util.UUID

import wotgraph.app.sensedv.domain.repository.SensedValueRepository
import wotgraph.app.sensedv.infrastructure.serialization.keys.SensedValueKeys
import wotgraph.app.thing.application.service.action.{ActionContext, ExecutionResult, ThingAndAction}
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.AvailableContexts

import scala.concurrent.Future

object ReadFromDatabaseContext {

  private val GetWordPrefix = "getOf"

  private val ReadFromDatabaseNamespaceKey = SensedValueKeys.Namespace

  def createAction(ta: ThingAndAction): List[Action] = {
    val a = ta.action
    val aName = GetWordPrefix + a.actionName.capitalize
    val action = Action(actionName = aName, contextId = AvailableContexts.ReadFromDatabaseContext, "")
    a :: action :: Nil
  }
}

class ReadFromDatabaseContext(sensedRepository: SensedValueRepository) extends ActionContext[SensedValueRepository] {

  override val context: SensedValueRepository = sensedRepository

  override def executeAction(ta: ThingAndAction, contextValue: Map[String, String]): Future[ExecutionResult] = ???
}

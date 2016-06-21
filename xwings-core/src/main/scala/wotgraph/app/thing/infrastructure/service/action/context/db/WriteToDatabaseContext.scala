package wotgraph.app.thing.infrastructure.service.action.context.db

import java.util.UUID

import org.scalactic.{Bad, Good}
import play.api.libs.json.{JsObject, Json}
import wotgraph.app.sensedv.domain.SensedValue
import wotgraph.app.sensedv.domain.repository.SensedValueRepository
import wotgraph.app.thing.application.service.action._
import wotgraph.app.thing.domain.entity.Action
import wotgraph.app.thing.infrastructure.service.action.AvailableContexts
import wotgraph.toolkit.scalactic.ErrorHelper

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future


class WriteToDatabaseTransformer extends ActionTransformer {

  override val contextId: UUID = AvailableContexts.WriteToDatabaseContext

  override def transform(ta: ThingAndAction): List[Action] = ta.action :: ReadFromDatabaseContext.createAction(ta)
}

class WriteToDatabaseContext(sensedRepository: SensedValueRepository) extends ActionContext[SensedValueRepository] {

  override val context: SensedValueRepository = sensedRepository

  override def executeAction(
                              ta: ThingAndAction,
                              contextValue: Map[String, String],
                              actionPayload: JsObject
                            ): Future[ExecutionResult] = {

    val sv = toSensedValue(actionPayload, ta)
    val result = sensedRepository.create(sv)
    result.map {
      case Good(s) => ExecutionSuccess("Inserted")
      case Bad(l) => ExecutionFailure(ErrorHelper.every2List(l).map(_.toString))
    }
  }

  def toSensedValue(obj: JsObject, ta: ThingAndAction): SensedValue =
    toSensedValue(obj, ta.thingId, ta.action.actionName)

  def toSensedValue(obj: JsObject, thingId: UUID, actionName: String): SensedValue =
    SensedValue(namespace = ThingAndAction.namespace(thingId, actionName), data = obj)

}




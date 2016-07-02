package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalactic._
import play.api.libs.json.{JsObject, Json}
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.{AppError, ValidationError}
import wotgraph.app.thing.application.service.action.{ActionExecutor, ExecutionFailure, ExecutionResult, ThingAndAction}
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.thing.infrastructure.service.action.ContextProvider
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class ExecuteThingActionUseCase(thingRepository: ThingRepository, authorizationService: AuthorizationService) {

  def execute(thingId: String,
              actionName: String,
              actionPayload: JsObject = Json.obj())
             (executorAgentId: UUID): Future[Option[ExecutionResult] Or Every[AppError]] = {

    Try(UUID.fromString(thingId)) match {
      case Failure(_) => Future.successful(Bad(One(ValidationError.WrongUuidFormat)))
      case Success(uuid) =>
        AuthorizationService.asyncExecute(authorizationService, executorAgentId, ExecuteThingActionUseCase.permission.id) {
          thingRepository.findById(uuid).flatMap {
            case Some(thing) =>
              val action = thing.actions.find(_.actionName == actionName)
              action match {
                case Some(a) =>
                  ActionExecutor.executeAction(
                    ThingAndAction(uuid, a), actionPayload)(ContextProvider.injector
                  ).map(r => Good(Some(r)))
                case _ =>
                  Future.successful(Good(None))
              }
            case None => Future.successful(Good(None))
          }
        }
    }
  }
}

object ExecuteThingActionUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("9948d838-c3b2-4d47-b442-8c30c29dcd9d")
}




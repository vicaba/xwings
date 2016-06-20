package wotgraph.app.thing.application.usecase

import java.util.UUID

import org.scalactic._
import wotgraph.app.authorization.application.service.AuthorizationService
import wotgraph.app.error.{AppError, ValidationError}
import wotgraph.app.thing.application.service._
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.thing.infrastructure.service.action.ContextProvider
import wotgraph.toolkit.application.usecase.PermissionProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class ExecuteThingActionUseCase(thingRepository: ThingRepository, authorizationService: AuthorizationService) {

  def execute(id: String, actionName: String)(executorAgentId: UUID): Future[ExecutionResult Or Every[AppError]] = {

    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.successful(Bad(One(ValidationError.WrongUuidFormat)))
      case Success(uuid) =>
        AuthorizationService.asyncExecute(authorizationService, executorAgentId, ExecuteThingActionUseCase.permission.id) {
          thingRepository.findById(uuid).flatMap {
            case Some(thing) =>
              val action = thing.actions.find(_.actionName == actionName)
              action match {
                case Some(a) => ActionExecutor.executeAction(a)(ContextProvider.injector).map(Good(_))
                case _ => Future.successful(Good(ExecutionFailure(List("Action not found"))))
              }
            case None => Future(Good(ExecutionFailure(List("Thing not found"))))
          }
        }
    }
  }
}

object ExecuteThingActionUseCase extends PermissionProvider {
  override protected val permissionId: UUID = UUID.fromString("9948d838-c3b2-4d47-b442-8c30c29dcd9d")
}




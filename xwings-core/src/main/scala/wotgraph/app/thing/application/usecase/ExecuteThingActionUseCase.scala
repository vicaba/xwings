package wotgraph.app.thing.application.usecase

import java.util.UUID

import wotgraph.app.exceptions.ClientFormatException
import wotgraph.app.thing.domain.repository.ThingRepository
import wotgraph.app.thing.domain.service.{ActionExecutor, ExecutionFailure, ExecutionResult}
import wotgraph.app.user.domain.entity.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class ExecuteThingActionUseCase(thingRepository: ThingRepository) {

  def execute(id: String, actionName: String)(userId: User.Id): Future[ExecutionResult] = {

    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.failed(new ClientFormatException("UUID not provided"))
      case Success(uuid) =>
        thingRepository.findById(uuid).flatMap {
          case Some(thing) =>
            val action = thing.actions.find(_.actionName == actionName)
            action match {
              case Some(a) => ActionExecutor.executeAction(a)
              case _ => Future(ExecutionFailure(List("Action not found")))
            }
          case None => Future(ExecutionFailure(List("Thing not found")))
        }

    }

  }

}

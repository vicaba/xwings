package wotgraph.app.thing.infrastructure.http.api.controller

import play.api.libs.json.Json
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.exceptions.{ClientFormatException, DatabaseException}
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.application.usecase.ExecuteThingActionUseCase
import wotgraph.app.thing.domain.service.{ExecutionFailure, ExecutionSuccess}
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class ExecuteThingActionController extends Controller with PredefJsonMessages {

  lazy val executeThingActionUseCase: ExecuteThingActionUseCase = inject[ExecuteThingActionUseCase](identified by 'ExecuteThingActionUseCase)

  def execute(id: String, actionName: String) = AuthenticatedAction.async(parse.json) { r =>
    executeThingActionUseCase.execute(id, actionName)(r.userId) map {
      case success: ExecutionSuccess => Ok(Json.obj("data" -> success.message))
      case failure: ExecutionFailure => BadRequest(Json.obj("message" -> failure.errors))
    } recover {
      case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }
  }

}
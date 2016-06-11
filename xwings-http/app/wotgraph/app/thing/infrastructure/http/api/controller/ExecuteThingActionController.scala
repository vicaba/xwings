package wotgraph.app.thing.infrastructure.http.api.controller

import play.api.libs.json.Json
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.exceptions.{ClientFormatException, DatabaseException}
import wotgraph.app.thing.application.usecase.ThingUseCase
import wotgraph.app.thing.domain.service.{ExecutionFailure, ExecutionSuccess}
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class ExecuteThingActionController extends Controller with PredefJsonMessages {

  lazy val thingUseCase: ThingUseCase = inject[ThingUseCase](identified by 'ThingUseCase)

  def execute(id: String, actionName: String) = Action.async(parse.json) { request =>
    thingUseCase.executeThingAction(id, actionName) map {
      case success: ExecutionSuccess => Ok(Json.obj("data" -> success.message))
      case failure: ExecutionFailure => BadRequest(Json.obj("message" -> failure.errors))
    } recover {
      case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }
  }

}
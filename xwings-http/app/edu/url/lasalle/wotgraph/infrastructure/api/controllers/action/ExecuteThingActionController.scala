package edu.url.lasalle.wotgraph.infrastructure.api.controllers.action

import edu.url.lasalle.wotgraph.application.exceptions.{ClientFormatException, DatabaseException}
import edu.url.lasalle.wotgraph.application.usecase.ThingUseCase
import edu.url.lasalle.wotgraph.domain.entity.thing.action.{ExecutionFailure, ExecutionSuccess}
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import edu.url.lasalle.wotgraph.infrastructure.api.controllers.PredefJsonMessages
import play.api.libs.json.Json
import play.api.mvc._
import scaldi.Injectable._

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
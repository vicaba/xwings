package wotgraph.app.thing.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.application.service.action.{ExecutionFailure, ExecutionSuccess}
import wotgraph.app.thing.application.usecase.ExecuteThingActionUseCase
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExecuteThingActionController extends Controller with PredefJsonMessages {

  lazy val executeThingActionUseCase: ExecuteThingActionUseCase = inject[ExecuteThingActionUseCase](identified by 'ExecuteThingActionUseCase)

  def execute(id: String, actionName: String) = AuthenticatedAction.async(parse.json) { r =>

    r.body.asOpt[JsObject] match {
      case None => Future.successful(BadRequest(Json.obj(MessagesKey -> "No JSON Object found")))
      case Some(cValue) =>

        executeThingActionUseCase.execute(id, actionName, cValue)(r.userId) map {
          case Good(result) => result match {
            case success: ExecutionSuccess => Ok(Json.obj("data" -> success.message))
            case failure: ExecutionFailure => BadRequest(Json.obj("message" -> failure.errors))
          }
          case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
        }
    }

    }


}
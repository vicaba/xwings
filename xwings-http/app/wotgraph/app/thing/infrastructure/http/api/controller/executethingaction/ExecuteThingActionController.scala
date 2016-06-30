package wotgraph.app.thing.infrastructure.http.api.controller.executethingaction

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.scalactic.{Bad, Good}
import play.api.http.HttpEntity
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.application.service.action.{ExecutionFailure, ExecutionSuccess, StreamExecutionSuccess, StringExecutionSuccess}
import wotgraph.app.thing.application.usecase.ExecuteThingActionUseCase
import wotgraph.app.thing.infrastructure.http.api.controller.PredefJsonMessages
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExecuteThingActionController extends Controller with PredefJsonMessages {

  lazy val executeThingActionUseCase: ExecuteThingActionUseCase = inject[ExecuteThingActionUseCase](identified by 'ExecuteThingActionUseCase)

  def execute(id: String, actionName: String) = AuthenticatedAction.async(parse.json) { r =>

    r.body.asOpt[JsObject] match {
      case None => Future.successful(BadRequest(Json.obj(MessagesKey -> "No JSON Object found")))
      case Some(actionPayload) =>

        executeThingActionUseCase.execute(id, actionName, actionPayload)(r.userId) flatMap {
          case Good(result) => result match {
            case success: ExecutionSuccess[_] => handleExecutionSuccess(success)
            case failure: ExecutionFailure => Future.successful(BadRequest(Json.obj("message" -> failure.errors)))
          }
          case Bad(errors) => Future.successful(ErrorHelper.errorToHttpResponse(errors))
        }
    }

    }

  private def handleExecutionSuccess(es: ExecutionSuccess[_]): Future[Result] = es match {
    case StringExecutionSuccess(string) => Future.successful(Ok(Json.obj("data" -> string)))
    case StreamExecutionSuccess(stream) => Future.successful(buildResultFromStream(stream))
  }

  private def buildResultFromStream(stream: Source[String, akka.NotUsed]) = {
    val data = stream.map(s => ByteString(Json.stringify(Json.obj("data" -> s))))
    Result(ResponseHeader(OK), HttpEntity.Streamed(data, None, Some("text/event-stream")))
  }


}
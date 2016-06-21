package wotgraph.app.thing.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.Json
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.exceptions.{ClientFormatException, DatabaseException}
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.application.usecase.DeleteThingUseCase
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class DeleteThingController extends Controller with PredefJsonMessages {

  lazy val deleteThingUseCase: DeleteThingUseCase = inject[DeleteThingUseCase](identified by 'DeleteThingUseCase)

  def execute(id: String) = AuthenticatedAction.async { r =>
    deleteThingUseCase.execute(id)(r.userId) map {
      case Good(_id) => Ok(Json.obj(ThingKeys.Id -> _id))
      case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
    } recover {
      case e: ClientFormatException => BadRequest(Json.obj(MessagesKey -> e.msg))
      case e: DatabaseException => BadGateway(Json.obj(MessagesKey -> e.msg))
    }
  }

}
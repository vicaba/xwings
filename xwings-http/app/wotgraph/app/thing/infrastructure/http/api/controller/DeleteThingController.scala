package wotgraph.app.thing.infrastructure.http.api.controller

import play.api.libs.json.Json
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.exceptions.{ClientFormatException, DatabaseException}
import wotgraph.app.thing.application.usecase.DeleteThingUseCase
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class DeleteThingController extends Controller with PredefJsonMessages {

  lazy val deleteThingUseCase: DeleteThingUseCase = inject[DeleteThingUseCase](identified by 'DeleteThingUseCase)

  def execute(id: String) = Action.async { request =>
    deleteThingUseCase.execute(id) map { id =>
      Ok(Json.obj(ThingKeys.Id -> id))
    } recover {
      case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }
  }

}
package wotgraph.app.thing.infrastructure.http.api.controller

import play.api.libs.json.Json
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.exceptions.{ClientFormatException, DatabaseException}
import wotgraph.app.thing.application.usecase.ThingUseCase
import wotgraph.app.thing.infrastructure.serialization.format.json.ThingSerializer
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class DeleteThingController extends Controller with PredefJsonMessages {

  lazy val thingUseCase: ThingUseCase = inject[ThingUseCase](identified by 'ThingUseCase)

  def execute(id: String) = Action.async(parse.json) { request =>
    thingUseCase.deleteThing(id) map { id =>
      Ok(Json.obj(ThingSerializer.IdKey -> id))
    } recover {
      case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }
  }

}
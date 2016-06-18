package wotgraph.app.thing.infrastructure.http.api.controller

import play.api.libs.json.Json
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.exceptions.{ClientFormatException, DatabaseException}
import wotgraph.app.thing.application.usecase.ShowThingUseCase
import wotgraph.app.thing.infrastructure.http.serialization.format.json.ThingSerializer
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class ShowThingController extends Controller with PredefJsonMessages {

  lazy val showThingUseCase: ShowThingUseCase = inject[ShowThingUseCase](identified by 'ShowThingUseCase)

  def execute(id: String) = Action.async {

    showThingUseCase.execute(id) map {
      case Some(thing) => Ok(ThingSerializer.thingFormat.writes(thing))
      case None => NotFound(Json.obj())
    } recover {
      case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }

  }

}
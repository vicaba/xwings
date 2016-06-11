package wotgraph.app.thing.infrastructure.http.api.controller

import wotgraph.app.exceptions.{ClientFormatException, DatabaseException}
import wotgraph.app.thing.application.usecase.ThingUseCase
import wotgraph.app.thing.infrastructure.http.serialization.format.json.ThingSerializer
import wotgraph.toolkit.DependencyInjector._
import play.api.libs.json.Json
import play.api.mvc._
import scaldi.Injectable._

import scala.concurrent.ExecutionContext.Implicits.global

class ShowThingController extends Controller with PredefJsonMessages {

  lazy val thingUseCase: ThingUseCase = inject[ThingUseCase](identified by 'ThingUseCase)

  def execute(id: String) = Action.async {

    thingUseCase.getThing(id) map {
      case Some(thing) => Ok(ThingSerializer.thingFormat.writes(thing))
      case None => NotFound(Json.obj())
    } recover {
      case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }

  }

}
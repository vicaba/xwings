package edu.url.lasalle.wotgraph.infrastructure.api.controllers.thing

import edu.url.lasalle.wotgraph.application.exceptions.{ClientFormatException, DatabaseException}
import edu.url.lasalle.wotgraph.application.usecase.ThingUseCase
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import edu.url.lasalle.wotgraph.infrastructure.api.controllers.PredefJsonMessages
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.{ThingSerializer => BaseThingSerializer}
import play.api.libs.json.Json
import play.api.mvc._
import scaldi.Injectable._

import scala.concurrent.ExecutionContext.Implicits.global

class DeleteThingController extends Controller with PredefJsonMessages {

  lazy val thingUseCase: ThingUseCase = inject[ThingUseCase](identified by 'ThingUseCase)

  def execute(id: String) = Action.async(parse.json) { request =>
    thingUseCase.deleteThing(id) map { id =>
      Ok(Json.obj(BaseThingSerializer.IdKey -> id))
    } recover {
      case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }
  }

}
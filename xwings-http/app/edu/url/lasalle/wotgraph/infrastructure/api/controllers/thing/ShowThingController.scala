package edu.url.lasalle.wotgraph.infrastructure.api.controllers.thing

import edu.url.lasalle.wotgraph.application.exceptions.{ClientFormatException, DatabaseException}
import edu.url.lasalle.wotgraph.application.usecase.ThingUseCase
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import edu.url.lasalle.wotgraph.infrastructure.api.controllers.PredefJsonMessages
import edu.url.lasalle.wotgraph.infrastructure.api.serializers.json.ThingSerializer
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
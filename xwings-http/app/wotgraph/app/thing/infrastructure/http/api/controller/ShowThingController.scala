package wotgraph.app.thing.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.Json
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.exceptions.{ClientFormatException, DatabaseException}
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.application.usecase.ShowThingUseCase
import wotgraph.app.thing.infrastructure.http.serialization.format.json.ThingSerializer
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global

class ShowThingController extends Controller with PredefJsonMessages {

  lazy val showThingUseCase: ShowThingUseCase = inject[ShowThingUseCase](identified by 'ShowThingUseCase)

  def execute(id: String) = AuthenticatedAction.async { r =>

    showThingUseCase.execute(id)(r.userId) map {
      case Good(tOpt) => tOpt match {
        case Some(thing) => Ok(ThingSerializer.thingFormat.writes(thing))
        case None => NotFound(Json.obj())
      }
      case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
    } recover {
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }

  }

}
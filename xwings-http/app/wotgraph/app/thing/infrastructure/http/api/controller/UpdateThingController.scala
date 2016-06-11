package wotgraph.app.thing.infrastructure.http.api.controller

import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.exceptions.{ClientFormatException, CoherenceException, DatabaseException}
import wotgraph.app.thing.application.usecase.{CreateThing, ThingUseCase}
import wotgraph.app.thing.infrastructure.serialization.format.json.ThingSerializer
import wotgraph.app.thing.infrastructure.serialization.format.json.dto.Implicits._
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UpdateThingController extends Controller with PredefJsonMessages {

  lazy val thingUseCase: ThingUseCase = inject[ThingUseCase](identified by 'ThingUseCase)

  def execute(id: String) = Action.async(parse.json) { request =>

    val res = request.body.validate[CreateThing]
    res match {
      case JsSuccess(createThingDto, _) =>
        val f = thingUseCase.updateThing(id, createThingDto)
        f.map {
          case Some(t) => Ok(Json.obj(ThingSerializer.IdKey -> t._id))
          case None => NotFound(Json.obj())
        } recover {
          case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
          case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
          case e: CoherenceException => UnprocessableEntity(Json.obj(MessageKey -> e.msg))
        }
      case e: JsError => Future.successful(BadRequest(BadJsonFormatMessage))
    }
  }

}
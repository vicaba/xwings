package wotgraph.app.thing.infrastructure.http.api.controller

import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.exceptions.{CoherenceException, DatabaseException}
import wotgraph.app.thing.application.usecase.{CreateThing, ThingUseCase}
import wotgraph.app.thing.infrastructure.serialization.format.json.ThingSerializer
import wotgraph.app.thing.infrastructure.serialization.format.json.dto.Implicits._
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateThingController extends Controller with PredefJsonMessages {

  lazy val thingUseCase: ThingUseCase = inject[ThingUseCase](identified by 'ThingUseCase)

  def execute = Action.async(parse.json) { request =>

    val res = request.body.validate[CreateThing]
    res match {
      case JsSuccess(createThingDto, _) =>
        val f = thingUseCase.createThing(createThingDto)
        f.map { t =>
          Created(Json.obj(ThingSerializer.IdKey -> t._id))
        } recover {
          case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
          case e: CoherenceException => UnprocessableEntity(Json.obj(MessageKey -> e.msg))
        }
      case e: JsError => Future.successful(BadRequest(e.toString))
    }
  }

}
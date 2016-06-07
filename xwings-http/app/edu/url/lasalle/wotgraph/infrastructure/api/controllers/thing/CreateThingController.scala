package edu.url.lasalle.wotgraph.infrastructure.api.controllers.thing

import edu.url.lasalle.wotgraph.application.exceptions.{CoherenceException, DatabaseException}
import edu.url.lasalle.wotgraph.application.usecase.{CreateThing, ThingUseCase}
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import edu.url.lasalle.wotgraph.infrastructure.api.controllers.PredefJsonMessages
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.dto.Implicits._
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.{ThingSerializer => BaseThingSerializer}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import scaldi.Injectable._

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
          Created(Json.obj(BaseThingSerializer.IdKey -> t._id))
        } recover {
          case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
          case e: CoherenceException => UnprocessableEntity(Json.obj(MessageKey -> e.msg))
        }
      case e: JsError => Future.successful(BadRequest(e.toString))
    }
  }

}
package wotgraph.app.thing.infrastructure.http.api.controller

import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.exceptions.{ClientFormatException, CoherenceException, DatabaseException}
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.application.usecase.UpdateThingUseCase
import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.infrastructure.serialization.format.json.dto.Implicits._
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UpdateThingController extends Controller with PredefJsonMessages {

  lazy val updateThingUseCase: UpdateThingUseCase = inject[UpdateThingUseCase](identified by 'UpdateThingUseCase)

  def execute(id: String) = AuthenticatedAction.async(parse.json) { r =>

    val res = r.body.validate[CreateThing]
    res match {
      case JsSuccess(createThingDto, _) =>
        val f = updateThingUseCase.execute(id, createThingDto)(r.userId)
        f.map {
          case Some(t) => Ok(Json.obj(ThingKeys.Id -> t._id))
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
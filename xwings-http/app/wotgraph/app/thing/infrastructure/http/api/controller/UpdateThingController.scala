package wotgraph.app.thing.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.exceptions.{CoherenceException, DatabaseException}
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
        updateThingUseCase.execute(id, createThingDto)(r.userId).map {
          case Good(tOpt) => tOpt match {
          case Some(t) => Ok(Json.obj(ThingKeys.Id -> t._id))
          case None => NotFound(Json.obj())
        }
          case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
        } recover {
          case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
          case e: CoherenceException => UnprocessableEntity(Json.obj(MessageKey -> e.msg))
        }
      case e: JsError => Future.successful(BadRequest(BadJsonFormatMessage))
    }
  }

}
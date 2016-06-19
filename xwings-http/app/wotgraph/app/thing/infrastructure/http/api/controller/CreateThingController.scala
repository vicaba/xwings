package wotgraph.app.thing.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.exceptions.{CoherenceException, DatabaseException}
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.application.usecase.CreateThingUseCase
import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.infrastructure.serialization.format.json.dto.Implicits._
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateThingController extends Controller with PredefJsonMessages {

  lazy val createThingUseCase: CreateThingUseCase = inject[CreateThingUseCase](identified by 'CreateThingUseCase)

  def execute = AuthenticatedAction.async(parse.json) { implicit r =>

    val res = r.body.validate[CreateThing]
    res match {
      case JsSuccess(createThingDto, _) => createThingUseCase.execute(createThingDto)(r.userId).map {
        case Good(t) => Created(Json.obj(ThingKeys.Id -> t._id))
        case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
        } recover {
          case e: DatabaseException => InternalServerError(Json.obj(MessageKey -> e.msg))
          case e: CoherenceException => UnprocessableEntity(Json.obj(MessageKey -> e.msg))
        }
      case e: JsError => Future.successful(BadRequest(e.toString))
    }
  }

}
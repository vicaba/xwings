package wotgraph.app.role.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.exceptions.DatabaseException
import wotgraph.app.role.application.dto.CreateRole
import wotgraph.app.role.application.usecase.CreateRoleUseCase
import wotgraph.app.role.infrastructure.serialization.format.json.dto.Implicits._
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.infrastructure.http.api.controller.PredefJsonMessages
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class CreateRoleController extends Controller with PredefJsonMessages {

  lazy val createRoleUseCase: CreateRoleUseCase = inject[CreateRoleUseCase](identified by 'CreateRoleUseCase)

  def execute = AuthenticatedAction.async(parse.json) { r =>

    val res = r.body.validate[CreateRole]
    res match {
      case JsSuccess(createRoleDto, _) => createRoleUseCase.execute(createRoleDto)(r.userId).map {
        case Good(role) => Created(Json.obj(ThingKeys.Id -> role.id))
        case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
      } recover {
        case e: DatabaseException => BadGateway(Json.obj(MessagesKey -> e.msg))
      }
      case e: JsError => Future.successful(BadRequest(e.toString))
    }
  }

}
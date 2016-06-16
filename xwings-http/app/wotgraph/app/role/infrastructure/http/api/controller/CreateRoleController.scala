package wotgraph.app.role.infrastructure.http.api.controller

import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}
import scaldi.Injectable._
import wotgraph.app.exceptions.DatabaseException
import wotgraph.app.role.application.dto.CreateRole
import wotgraph.app.role.application.usecase.CreateRoleUseCase
import wotgraph.app.role.infrastructure.serialization.format.json.dto.Implicits._
import wotgraph.app.thing.infrastructure.http.api.controller.PredefJsonMessages
import wotgraph.app.thing.infrastructure.serialization.keys.ThingKeys
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class CreateRoleController extends Controller with PredefJsonMessages {

  lazy val createRoleUseCase: CreateRoleUseCase = inject[CreateRoleUseCase](identified by 'CreateRoleUseCase)

  def execute = Action.async(parse.json) { request =>

    val res = request.body.validate[CreateRole]
    res match {
      case JsSuccess(createRoleDto, _) =>
        val f = createRoleUseCase.execute(createRoleDto)
        f.map { r =>
          Created(Json.obj(ThingKeys.Id -> r.id))
        } recover {
          case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
        }
      case e: JsError => Future.successful(BadRequest(e.toString))
    }
  }

}
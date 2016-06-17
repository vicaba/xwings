package wotgraph.app.user.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.exceptions.DatabaseException
import wotgraph.app.thing.infrastructure.http.api.controller.PredefJsonMessages
import wotgraph.app.user.application.usecase.DeleteUserUseCase
import wotgraph.app.user.infrastructure.serialization.keys.UserKeys
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class DeleteUserController extends Controller with PredefJsonMessages {

  lazy val deleteUserUseCase: DeleteUserUseCase = inject[DeleteUserUseCase](identified by 'DeleteUserUseCase)

  def execute(id: String) = Action.async { request =>

    deleteUserUseCase.execute(id).map {
      case Good(u) => Created(Json.obj(UserKeys.Id -> u))
      case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
    } recover {
      case e: DatabaseException => InternalServerError(Json.obj(MessageKey -> e.msg))
    }

  }
}
package wotgraph.app.user.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.Writes._
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.infrastructure.http.api.controller.PredefJsonMessages
import wotgraph.app.user.application.usecase.CreateUserUseCase
import wotgraph.app.user.application.usecase.dto.CreateUser
import wotgraph.app.user.infrastructure.serialization.format.json.dto.Implicits._
import wotgraph.app.user.infrastructure.serialization.keys.UserKeys
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateUserController extends Controller with PredefJsonMessages {

    lazy val createUserUseCase: CreateUserUseCase = inject[CreateUserUseCase](identified by 'CreateUserUseCase)

    def execute = AuthenticatedAction.async(parse.json) { r =>

      val res = r.body.validate[CreateUser]

      res match {
        case JsSuccess(createUserDto, _) => createUserUseCase.execute(createUserDto)(r.userId).map {
          case Good(u) => Created(Json.obj(UserKeys.Id -> u.id))
          case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
        }
        case e: JsError => Future.successful(BadRequest(e.toString))
      }

    }

}

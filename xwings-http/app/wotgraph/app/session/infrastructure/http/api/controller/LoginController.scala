package wotgraph.app.session.infrastructure.http.api.controller

import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.{Action, Controller}
import scaldi.Injectable._
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.infrastructure.http.api.controller.PredefJsonMessages
import wotgraph.app.user.application.usecase.AuthenticateUserUseCase
import wotgraph.app.user.application.usecase.dto.UserCredentials
import wotgraph.app.user.infrastructure.serialization.format.json.dto.Implicits._
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginController extends Controller with PredefJsonMessages {

  lazy val authenticateUserUseCase: AuthenticateUserUseCase =
    inject[AuthenticateUserUseCase](identified by 'AuthenticateUserUseCase)

  lazy val encrypt: String => String = inject[String => String](identified by 'SessionEncrypter)


  def execute = Action.async(parse.json) { implicit r =>

    val res = r.body.validate[UserCredentials]
    res match {
      case JsSuccess(userCredentials, _) => authenticateUserUseCase.execute(userCredentials).map {
        case Some(u) => Created("").addingToSession(AuthenticatedAction.tokenKey -> encrypt(u.id.toString))
        case None => NotFound("")
      }
      case e: JsError => Future.successful(BadRequest(e.toString))
    }

  }

}

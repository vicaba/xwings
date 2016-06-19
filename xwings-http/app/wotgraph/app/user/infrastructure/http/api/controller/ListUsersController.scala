package wotgraph.app.user.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.exceptions.DatabaseException
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.infrastructure.http.api.controller.PredefJsonMessages
import wotgraph.app.user.application.usecase.ListUsersUseCase
import wotgraph.app.user.infrastructure.http.serialization.format.json.UserMinifiedSerializer
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class ListUsersController extends Controller with PredefJsonMessages {

  lazy val listUsersUseCase: ListUsersUseCase = inject[ListUsersUseCase](identified by 'ListUsersUseCase)

  def execute = AuthenticatedAction.async { r =>

    listUsersUseCase.execute()(r.userId).map {
      case Good(seqOfUsers) =>
        val json = UserMinifiedSerializer.userSeqWrites.writes(seqOfUsers)
        Ok(json)
      case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
    } recover {
      case e: DatabaseException => InternalServerError(Json.obj(MessageKey -> e.msg))
    }

  }
}

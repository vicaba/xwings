package wotgraph.app.user.infrastructure.http.api.controller

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scaldi.Injectable._
import wotgraph.app.exceptions.DatabaseException
import wotgraph.app.thing.infrastructure.http.api.controller.PredefJsonMessages
import wotgraph.app.user.application.usecase.ListUsersUseCase
import wotgraph.app.user.infrastructure.http.serialization.format.json.UserMinifiedSerializer
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class ListUsersController extends Controller with PredefJsonMessages {

  lazy val listUsersUseCase: ListUsersUseCase = inject[ListUsersUseCase](identified by 'ListUsersUseCase)

  def execute = Action.async { request =>

    listUsersUseCase.execute() map { seqOfUsers =>
      val json = UserMinifiedSerializer.userSeqWrites.writes(seqOfUsers)
      Ok(json)
    } recover {
      case e: DatabaseException => InternalServerError(Json.obj(MessageKey -> e.msg))
    }

  }
}

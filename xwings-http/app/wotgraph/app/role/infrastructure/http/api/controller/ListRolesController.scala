package wotgraph.app.role.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.exceptions.DatabaseException
import wotgraph.app.role.application.usecase.ListRolesUseCase
import wotgraph.app.role.infrastructure.serialization.format.json.RoleSerializer
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.infrastructure.http.api.controller.PredefJsonMessages
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class ListRolesController extends Controller with PredefJsonMessages {

  lazy val listRolesUseCase: ListRolesUseCase = inject[ListRolesUseCase](identified by 'ListRolesUseCase)

  def execute = AuthenticatedAction.async { r =>

    listRolesUseCase.execute()(r.userId) map {
      case Good(seqOfRoles) =>
        val json = RoleSerializer.roleSeqWrites.writes(seqOfRoles)
        Ok(json)
      case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
    } recover {
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }

  }

}

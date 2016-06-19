package wotgraph.app.permission.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.exceptions.DatabaseException
import wotgraph.app.permission.application.usecase.ListPermissionsUseCase
import wotgraph.app.permission.infrastructure.serialization.format.json.PermissionSerializer
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.infrastructure.http.api.controller.PredefJsonMessages
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class ListPermissionsController extends Controller with PredefJsonMessages {

  lazy val listPermissionsUseCase: ListPermissionsUseCase = inject[ListPermissionsUseCase](identified by 'ListPermissionsUseCase)

  def execute = AuthenticatedAction.async { r =>

    listPermissionsUseCase.execute()(r.userId) map {
      case Good(seqOfPerms) =>
        val json = PermissionSerializer.permissionSeqWrites.writes(seqOfPerms)
        Ok(json)
      case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
    } recover {
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }

  }

}

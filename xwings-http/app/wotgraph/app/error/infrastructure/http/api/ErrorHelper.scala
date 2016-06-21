package wotgraph.app.error.infrastructure.http.api

import org.scalactic.Every
import play.api.libs.json.Json
import play.api.mvc.Controller
import wotgraph.app.error.{AppError, AuthorizationDenied}
import wotgraph.toolkit.scalactic.{ErrorHelper => EH}

object ErrorHelper extends Controller {

  def errorToHttpResponse(e: Every[AppError]) = {
    val errorList = EH.every2List(e)
    val auth = errorList.filter(_.isInstanceOf[AuthorizationDenied])
    if (auth.nonEmpty) Unauthorized(Json.obj("errors" -> auth.map(_.msg)))
    else BadRequest(Json.obj("errors" -> errorList.map(_.msg)))
  }

}


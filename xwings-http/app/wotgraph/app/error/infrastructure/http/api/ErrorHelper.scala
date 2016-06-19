package wotgraph.app.error.infrastructure.http.api

import org.scalactic.{Bad, Every, Many, One}
import play.api.libs.json.Json
import play.api.mvc.Controller
import wotgraph.app.error.{AppError, AuthorizationDenied}

object ErrorHelper extends Controller {

  def many2List[T](many: Many[T]): List[T] =
    List[T](many.firstElement, many.secondElement) ::: many.otherElements.toList

  def errorToHttpResponse(e: Every[AppError]) = {
    val errorList = e match {
      case m : Many[AppError] => many2List(m)
      case One(o) => List(o)
    }
    val auth = errorList.filter(_.isInstanceOf[AuthorizationDenied])
    if (auth.nonEmpty) Unauthorized(Json.obj("errors" -> auth.map(_.msg)))
    else BadRequest(Json.obj("errors" -> errorList.map(_.msg)))
  }

}


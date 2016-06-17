package wotgraph.app.error.infrastructure.http.api

import org.scalactic.{Bad, Every, Many, One}
import play.api.libs.json.Json
import play.api.mvc.Controller
import wotgraph.app.error.AppError

object ErrorHelper extends Controller {

  def many2List[T](many: Many[T]): List[T] =
    List[T](many.firstElement, many.secondElement) ::: many.otherElements.toList

  def errorToHttpResponse(e: Every[AppError]) = e match {
    case m : Many[AppError] => BadRequest(Json.obj("errors" -> many2List(m).map(_.msg)))
    case One(o) => BadRequest(Json.obj("errors" -> List(o.msg)))
  }

}

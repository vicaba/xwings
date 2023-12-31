package wotgraph.app.thing.infrastructure.http.api.controller

import org.scalactic.{Bad, Good}
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.application.usecase.ListThingsUseCase
import wotgraph.app.thing.application.usecase.dto.GetThings
import wotgraph.app.thing.infrastructure.serialization.format.json.dto.Implicits._
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SearchThingController extends Controller with PredefJsonMessages {

  lazy val listThingsUseCase: ListThingsUseCase = inject[ListThingsUseCase](identified by 'ListThingsUseCase)

  def execute = AuthenticatedAction.async(parse.json) { r =>
    r.body.validate[GetThings] match {
      case JsSuccess(getThings, _) =>
        listThingsUseCase.execute(getThings)(r.userId).map {
          case Good(things) => Helper.seqOfThingsToHttpResponse(things)
          case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
        }
      case e: JsError => Future {
        BadRequest(BadJsonFormatMessage)
      }
    }
  }

}
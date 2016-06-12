package wotgraph.app.thing.infrastructure.http.api.controller

import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.thing.application.usecase.ListThingsUseCase
import wotgraph.app.thing.application.usecase.dto.GetThings
import wotgraph.app.thing.infrastructure.serialization.format.json.dto.Implicits._
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SearchThingController extends Controller with PredefJsonMessages {

  lazy val listThingsUseCase: ListThingsUseCase = inject[ListThingsUseCase](identified by 'ListThingsUseCase)

  def execute = Action.async(parse.json) { request =>
    request.body.validate[GetThings] match {
      case JsSuccess(getThings, _) =>
        val f = listThingsUseCase.execute(getThings)
        Helper.seqOfThingsToHttpResponse(f)
      case e: JsError => Future {
        BadRequest(BadJsonFormatMessage)
      }
    }
  }

}
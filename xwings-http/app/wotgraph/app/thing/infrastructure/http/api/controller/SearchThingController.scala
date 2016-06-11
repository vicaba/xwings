package wotgraph.app.thing.infrastructure.http.api.controller

import wotgraph.app.thing.application.usecase.{GetThings, ThingUseCase}
import wotgraph.app.thing.infrastructure.serialization.format.json.dto.Implicits._
import wotgraph.toolkit.DependencyInjector._
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc._
import scaldi.Injectable._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SearchThingController extends Controller with PredefJsonMessages {

  lazy val thingUseCase: ThingUseCase = inject[ThingUseCase](identified by 'ThingUseCase)

  def execute = Action.async(parse.json) { request =>
    request.body.validate[GetThings] match {
      case JsSuccess(getThings, _) =>
        val f = thingUseCase.getThings(getThings)
        Helper.seqOfThingsToHttpResponse(f)
      case e: JsError => Future {
        BadRequest(BadJsonFormatMessage)
      }
    }
  }

}
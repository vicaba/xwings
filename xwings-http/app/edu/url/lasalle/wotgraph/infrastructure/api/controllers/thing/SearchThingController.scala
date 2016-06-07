package edu.url.lasalle.wotgraph.infrastructure.api.controllers.thing

import edu.url.lasalle.wotgraph.application.usecase.{GetThings, ThingUseCase}
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import edu.url.lasalle.wotgraph.infrastructure.api.controllers.PredefJsonMessages
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.dto.Implicits._
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
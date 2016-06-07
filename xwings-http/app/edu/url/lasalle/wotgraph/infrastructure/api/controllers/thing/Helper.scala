package edu.url.lasalle.wotgraph.infrastructure.api.controllers.thing

import edu.url.lasalle.wotgraph.application.exceptions.DatabaseException
import edu.url.lasalle.wotgraph.domain.entity.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.api.controllers.PredefJsonMessages
import edu.url.lasalle.wotgraph.infrastructure.api.serializers.json.ThingMinifiedSerializer
import play.api.libs.json.Json
import play.api.mvc.{Controller, Result}

import scala.concurrent.{ExecutionContext, Future}


object Helper
  extends Controller
    with PredefJsonMessages {

  def seqOfThingsToHttpResponse(thingsF: Future[Seq[Thing]])(implicit ec: ExecutionContext): Future[Result] = {
    thingsF map { setOfThings =>
      val json = ThingMinifiedSerializer.thingSeqFormat.writes(setOfThings)
      Ok(json)
    } recover {
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }
  }

}

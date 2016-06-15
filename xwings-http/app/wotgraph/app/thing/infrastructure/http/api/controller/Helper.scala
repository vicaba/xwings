package wotgraph.app.thing.infrastructure.http.api.controller

import wotgraph.app.exceptions.DatabaseException
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.infrastructure.http.serialization.format.json.ThingMinifiedSerializer
import play.api.libs.json.Json
import play.api.mvc.{Controller, Result}

import scala.concurrent.{ExecutionContext, Future}


object Helper
  extends Controller
    with PredefJsonMessages {

  def seqOfThingsToHttpResponse(thingsF: Future[Seq[Thing]])(implicit ec: ExecutionContext): Future[Result] = {
    thingsF map { seqOfThings =>
      val json = ThingMinifiedSerializer.thingSeqFormat.writes(seqOfThings)
      Ok(json)
    } recover {
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }
  }

}

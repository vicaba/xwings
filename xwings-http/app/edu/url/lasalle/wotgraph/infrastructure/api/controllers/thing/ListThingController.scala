package edu.url.lasalle.wotgraph.infrastructure.api.controllers.thing

import akka.stream.scaladsl.Source
import akka.util.ByteString
import edu.url.lasalle.wotgraph.application.usecase.ThingUseCase
import edu.url.lasalle.wotgraph.domain.entity.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import edu.url.lasalle.wotgraph.infrastructure.api.controllers.PredefJsonMessages
import edu.url.lasalle.wotgraph.infrastructure.api.serializers.json.ThingMinifiedSerializer
import org.reactivestreams.Publisher
import play.api.http.HttpEntity
import play.api.libs.streams.Streams
import play.api.mvc._
import scaldi.Injectable._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListThingController extends Controller with PredefJsonMessages {

  lazy val thingUseCase: ThingUseCase = inject[ThingUseCase](identified by 'ThingUseCase)

  def execute = Action.async { request =>

    val f = thingUseCase.getThings()
    Helper.seqOfThingsToHttpResponse(f)

  }

  def executeAsStream = Action.async { request =>

    val publisher: Publisher[Thing] = Streams.enumeratorToPublisher(thingUseCase.getThingsAsStream)
    val data: Source[ByteString, _] = Source.fromPublisher(publisher).map { thing =>
      ByteString(ThingMinifiedSerializer.thingFormat.writes(thing).toString)
    }

    Future {
      Result(ResponseHeader(OK), HttpEntity.Streamed(data, None, Some("text/event-stream")))
    }

  }

}
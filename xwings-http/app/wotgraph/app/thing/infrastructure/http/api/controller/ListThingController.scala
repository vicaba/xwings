package wotgraph.app.thing.infrastructure.http.api.controller

import akka.stream.scaladsl.Source
import akka.util.ByteString
import wotgraph.app.thing.application.usecase.ThingUseCase
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.infrastructure.http.serialization.format.json.ThingMinifiedSerializer
import wotgraph.toolkit.DependencyInjector._
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
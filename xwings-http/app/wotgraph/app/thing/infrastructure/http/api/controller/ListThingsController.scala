package wotgraph.app.thing.infrastructure.http.api.controller

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.reactivestreams.Publisher
import play.api.http.HttpEntity
import play.api.libs.streams.Streams
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.thing.application.usecase.ListThingsUseCase
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.infrastructure.http.serialization.format.json.ThingMinifiedSerializer
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListThingsController extends Controller with PredefJsonMessages {

  lazy val listThingsUseCase: ListThingsUseCase = inject[ListThingsUseCase](identified by 'ListThingsUseCase)

  def execute = Action.async { request =>

    val f = listThingsUseCase.execute()
    Helper.seqOfThingsToHttpResponse(f)

  }

  def executeAsStream = Action.async { request =>

    val publisher: Publisher[Thing] = Streams.enumeratorToPublisher(listThingsUseCase.executeAsStream)
    val data: Source[ByteString, _] = Source.fromPublisher(publisher).map { thing =>
      ByteString(ThingMinifiedSerializer.thingFormat.writes(thing).toString)
    }

    Future {
      Result(ResponseHeader(OK), HttpEntity.Streamed(data, None, Some("text/event-stream")))
    }

  }

}
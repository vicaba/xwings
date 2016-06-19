package wotgraph.app.thing.infrastructure.http.api.controller

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.reactivestreams.Publisher
import org.scalactic.{Bad, Good}
import play.api.http.HttpEntity
import play.api.libs.streams.Streams
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.application.usecase.ListThingsUseCase
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.infrastructure.http.serialization.format.json.ThingMinifiedSerializer
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global

class ListThingsController extends Controller with PredefJsonMessages {

  lazy val listThingsUseCase: ListThingsUseCase = inject[ListThingsUseCase](identified by 'ListThingsUseCase)

  def execute = AuthenticatedAction.async { r =>

    listThingsUseCase.execute()(r.userId).map {
      case Good(things) => Helper.seqOfThingsToHttpResponse(things)
      case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
    }
  }

  def executeAsStream = AuthenticatedAction.async { r =>

    listThingsUseCase.executeAsStream(r.userId).map {
      case Good(thingEnum) =>
        val publisher: Publisher[Thing] = Streams.enumeratorToPublisher(thingEnum)
        val data: Source[ByteString, _] = Source.fromPublisher(publisher).map { thing =>
          ByteString(ThingMinifiedSerializer.thingFormat.writes(thing).toString)
        }
        Result(ResponseHeader(OK), HttpEntity.Streamed(data, None, Some("text/event-stream")))
      case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
    }
  }

}
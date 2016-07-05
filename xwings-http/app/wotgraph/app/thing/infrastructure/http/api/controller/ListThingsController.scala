package wotgraph.app.thing.infrastructure.http.api.controller

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import com.google.inject.Inject
import org.reactivestreams.Publisher
import org.scalactic.{Bad, Good}
import play.api.http.HttpEntity
import play.api.libs.streams.{ActorFlow, Streams}
import play.api.mvc._
import scaldi.Injectable._
import wotgraph.app.error.infrastructure.http.api.ErrorHelper
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.application.usecase.ListThingsUseCase
import wotgraph.app.thing.domain.entity.Thing
import wotgraph.app.thing.infrastructure.http.api.controller.executethingaction.StreamActor.ExecuteThingActionPayload
import wotgraph.app.thing.infrastructure.http.serialization.format.json.ThingMinifiedSerializer
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListThingsController @Inject()(implicit system: ActorSystem, materializer: Materializer) extends Controller with PredefJsonMessages {

  lazy val listThingsUseCase: ListThingsUseCase = inject[ListThingsUseCase](identified by 'ListThingsUseCase)

  def execute = AuthenticatedAction.async { r =>

    listThingsUseCase.execute()(r.userId).map {
      case Good(things) => Helper.seqOfThingsToHttpResponse(things)
      case Bad(errors) => ErrorHelper.errorToHttpResponse(errors)
    }
  }

  def executeAsStream = WebSocket.acceptOrResult[String, String] { r =>
    AuthenticatedAction.sessionAuthenticate(r).flatMap {
      case Right(s) => Future.successful(Left(Unauthorized("")))
      case Left(agentId) =>
      listThingsUseCase.executeAsStream(agentId).map {
        case Good(thingEnum) =>
          val publisher: Publisher[Thing] = Streams.enumeratorToPublisher(thingEnum)
          val data: Source[String, _] = Source.fromPublisher(publisher).map { thing =>
            ThingMinifiedSerializer.thingFormat.writes(thing).toString
          }
          Right(Flow.fromSinkAndSource(Sink.ignore, data))
        case Bad(errors) => Left(ErrorHelper.errorToHttpResponse(errors))
      }
    }
  }

}
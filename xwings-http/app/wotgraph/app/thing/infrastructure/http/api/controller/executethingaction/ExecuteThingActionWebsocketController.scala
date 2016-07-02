package wotgraph.app.thing.infrastructure.http.api.controller.executethingaction

import java.util.UUID

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.google.inject.Inject
import org.scalactic.Good
import play.api.libs.json.{JsObject, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Controller, WebSocket}
import wotgraph.app.thing.infrastructure.http.api.controller.PredefJsonMessages
import scaldi.Injectable._
import wotgraph.app.session.infrastructure.http.AuthenticatedAction
import wotgraph.app.thing.application.service.action.StreamExecutionSuccess
import wotgraph.app.thing.application.usecase.ExecuteThingActionUseCase
import wotgraph.app.thing.infrastructure.http.api.controller.executethingaction.StreamActor.ExecuteThingActionPayload
import wotgraph.toolkit.DependencyInjector._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Try}

class ExecuteThingActionWebSocketController @Inject()(id: String, actionName: String)(implicit system: ActorSystem, materializer: Materializer) extends Controller with PredefJsonMessages {

  lazy val executeThingActionUseCase: ExecuteThingActionUseCase = inject[ExecuteThingActionUseCase](identified by 'ExecuteThingActionUseCase)

  def execute(id: String, action: String) = WebSocket.acceptOrResult[String, String] { r =>
    AuthenticatedAction.sessionAuthenticate(r).map {
      case Right(s) => Left(Unauthorized(""))
      case Left(agentId) =>
        val payload = ExecuteThingActionPayload(id, action, agentId)
        val webSocketProps = StreamActor.props(executeThingActionUseCase, payload, materializer) _
        Right(ActorFlow.actorRef(out => webSocketProps(out)))
    }
  }

}

object StreamActor {

  case class ExecuteThingActionPayload(thingId: String, actionName: String, executorAgentId: UUID)

  def props(
             executeThingActionUseCase: ExecuteThingActionUseCase,
             payload: ExecuteThingActionPayload,
             streamMaterializer: Materializer
           )
           (out: ActorRef) =
    Props(new StreamActor(executeThingActionUseCase, payload, streamMaterializer)(out))
}

class StreamActor(
                   executeThingActionUseCase: ExecuteThingActionUseCase,
                   payload: ExecuteThingActionPayload,
                   streamMaterializer: Materializer
                 )
                 (out: ActorRef)
  extends Actor {

  implicit val mat = streamMaterializer

  override def receive: Receive = {
    case s: String =>
      Try(Json.parse(s).as[JsObject]) match {
        case Success(jsObj) =>
          executeThingActionUseCase.execute(payload.thingId, payload.actionName, jsObj)(payload.executorAgentId) map {
            case Good(Some(stream: StreamExecutionSuccess)) =>
              stream.value.runWith(Sink.actorRef(out, onCompleteMessage = PoisonPill))
            case _ => self ! PoisonPill
          }
        case _ => self ! PoisonPill
      }
  }

}

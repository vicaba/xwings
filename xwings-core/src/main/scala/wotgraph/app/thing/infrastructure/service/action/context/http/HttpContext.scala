package wotgraph.app.thing.infrastructure.service.action.context.http

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.apache.commons.validator.routines.UrlValidator
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcWSClient
import wotgraph.app.thing.application.service.action.{ActionContext, ExecutionFailure, ExecutionResult, ExecutionSuccess}

import scala.concurrent.{ExecutionContext, Future}

case class HttpContext()(implicit ec: ExecutionContext) extends ActionContext[WSClient] {

  val HttpMethodKey = "httpMethod"

  val AllowedHttpMethods = List("GET", "POST", "PUT", "DELETE")

  val UrlKey = "url"

  val BodyKey = "body"

  override val context: WSClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))

  override def executeAction(thingId: UUID, contextValue: Map[String, String]): Future[ExecutionResult] = {

    val requestOpt = for {
      httpMethod <- contextValue get HttpMethodKey if AllowedHttpMethods contains httpMethod
      url <- contextValue get UrlKey if new UrlValidator() isValid url
    } yield {
      val request = context.url(url).withMethod(httpMethod)
      if (!(httpMethod equalsIgnoreCase "GET"))
        contextValue.get(BodyKey).fold(request)(request.withBody(_))
      else
        request
    }

    requestOpt.fold[Future[ExecutionResult]] {
      Future(ExecutionFailure(List("HttpMethod and URL fields are mandatory")))
    } { req =>
      req.execute().map { resp =>
        ExecutionSuccess(resp.body)
      } recover {
        case e: Throwable => ExecutionFailure(List(e.getMessage))
      }
    }

  }

}
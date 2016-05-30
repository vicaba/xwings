package edu.url.lasalle.wotgraph.domain.entity.thing.action

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.apache.commons.validator.routines.UrlValidator
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.{ExecutionContext, Future}

case class HttpContext()(implicit ec: ExecutionContext) extends ActionContext[AhcWSClient] {

  val HTTP_METHOD_KEY = "httpMethod"

  val ALLOWED_HTTP_METHODS = List("GET", "POST", "PUT", "DELETE")

  val URL_KEY = "url"

  val BODY_KEY = "body"

  override val context: AhcWSClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))

  override def executeAction(contextValue: Map[String, String]): Future[ExecutionResult] = {

    val requestOpt = for {
      httpMethod <- contextValue get HTTP_METHOD_KEY if ALLOWED_HTTP_METHODS contains httpMethod
      url <- contextValue get URL_KEY if new UrlValidator() isValid url
    } yield {
      val request = context.url(url).withMethod(httpMethod)
      if (!(httpMethod equalsIgnoreCase "GET"))
        contextValue.get(BODY_KEY).fold(request)(request.withBody(_))
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
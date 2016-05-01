package edu.url.lasalle.wotgraph.domain.thing.action

import org.apache.commons.validator.routines.UrlValidator
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.{ExecutionContext, Future}

case class HttpContext()(implicit ec: ExecutionContext) extends ActionContext[NingWSClient] {

  val HTTP_METHOD_KEY = "httpMethod"

  val ALLOWED_HTTP_METHODS = List("GET", "POST", "PUT", "DELETE")

  val URL_KEY = "url"

  val DATA_KEY = "data"

  override val context: NingWSClient = NingWSClient()

  override def executeAction(contextValue: Map[String, String]): Future[ExecutionResult] = {

    val requestOpt = for {
      httpMethod <- contextValue get HTTP_METHOD_KEY if ALLOWED_HTTP_METHODS contains httpMethod
      url <- contextValue get URL_KEY if new UrlValidator() isValid url
    } yield {
      val request = context.url(url).withMethod(httpMethod)

      if (httpMethod != "GET")
        contextValue.get(DATA_KEY).fold(request)(request.withBody(_))
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
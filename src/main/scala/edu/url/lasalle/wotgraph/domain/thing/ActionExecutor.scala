package edu.url.lasalle.wotgraph.domain.thing

import java.util.UUID

import org.apache.commons.validator.routines.UrlValidator
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object ActionExecutor {

  def executeAction(a: Action): Future[ExecutionResult] = ContextProvider.provideContextFor(a.contextId) match {
      case Some(c) =>

        val contextValue = a.contextValue.as[Map[String, String]]
        c.executeAction(contextValue)

      case None => Future(ExecutionFailure(List("Context Not found")))

    }


}

object ContextProvider {

  val httpContext = "1416c196-e837-4dae-b2ab-a64328e578b7"

  val contextMapper: PartialFunction[String, ActionContext[_]] = {
    case id if id == httpContext => HttpContext()
  }

  def provideContextFor(id: String): Option[ActionContext[_]] = contextMapper.lift(id)

  def provideContextFor(id: UUID): Option[ActionContext[_]] = provideContextFor(id.toString)

}

trait ActionContext[Context] {

  val context: Context

  def executeAction(contextValue: Map[String, String]): Future[ExecutionResult]

}

case class HttpContext() extends ActionContext[NingWSClient] {

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

trait ExecutionResult

sealed case class ExecutionSuccess(message: String) extends ExecutionResult

sealed case class ExecutionFailure(errors: List[String] = Nil) extends ExecutionResult

object Main {

  def main(args: Array[String]) {

    val contextValue = Map("httpMethod"-> "GET", "url" -> "https://es.wikipedia.org/wiki/Wikipedia:Portada")

    ActionExecutor.executeAction(
      Action("a", UUID.fromString("1416c196-e837-4dae-b2ab-a64328e578b7"), Json.toJson(contextValue).as[JsObject])
    ) map(println)
  }
}

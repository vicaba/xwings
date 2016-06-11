package wotgraph.app.thing.domain.service

import java.util.UUID

import wotgraph.app.thing.domain.entity.Action
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


object ActionExecutor {

  def executeAction(a: Action)(implicit ec: ExecutionContext): Future[ExecutionResult] = ContextProvider.provideContextFor(a.contextId) match {
    case Some(c) =>

      val contextValue = Json.parse(a.contextValue).asOpt[Map[String, String]] getOrElse
        Json.obj("rawData" -> a.contextValue).as[Map[String, String]]

      c.executeAction(contextValue)

    case None => Future(ExecutionFailure(List("Context Not Found")))

  }

}

object ContextProvider {

  val HTTP_CONTEXT = "1416c196-e837-4dae-b2ab-a64328e578b7"

  val contextResolver: PartialFunction[String, ActionContext[_]] = {
    case id if id == HTTP_CONTEXT => HttpContext()
  }

  def provideContextFor(id: String)(implicit ec: ExecutionContext): Option[ActionContext[_]] = contextResolver.lift(id)

  def provideContextFor(id: UUID)(implicit ec: ExecutionContext): Option[ActionContext[_]] = provideContextFor(id.toString)

}

trait ActionContext[Context] {

  val context: Context

  def executeAction(contextValue: Map[String, String]): Future[ExecutionResult]

}

trait ExecutionResult

sealed case class ExecutionSuccess(message: String) extends ExecutionResult

sealed case class ExecutionFailure(errors: List[String] = Nil) extends ExecutionResult {

  def +(error: String) = this.copy(this.errors.::(error))

}

//object Main {
//
//  def main(args: Array[String]) {
//
//    val contextValue = Map("httpMethod"-> "GET", "url" -> "https://es.wikipedia.org/wiki/Wikipedia:Portada")
//
//    ActionExecutor.executeAction(
//      Action("a", UUID.fromString("1416c196-e837-4dae-b2ab-a64328e578b7"), Json.toJson(contextValue).as[JsObject])
//    ) map(println)
//  }
//}

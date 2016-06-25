package wotgraph.app.thing.application.service.action

import java.util.UUID

import akka.stream.scaladsl.{Flow, Source}
import play.api.libs.json.{JsObject, Json}
import scaldi.Injectable._
import scaldi.{CanBeIdentifier, Identifier, Module}

import scala.concurrent.Future
import scala.util.Try

object UUIDCanBeIdentifier extends CanBeIdentifier[UUID] {
  override def toIdentifier(target: UUID): Identifier = target.toString
  implicit val uuidIdentifier = UUIDCanBeIdentifier
}

object ActionExecutor {

  def executeAction(ta: ThingAndAction, actionPayload: JsObject)(contextProvider: Module) = {

    implicit val cp = contextProvider

    import UUIDCanBeIdentifier._

    val a = ta.action

    Try(inject[ActionContext[_]](identified by a.contextId)).toOption match {
      case Some(c) =>

        val jsContextValue = Try(Json.parse(a.contextValue)).getOrElse(Json.obj())

        val contextValue = jsContextValue.asOpt[Map[String, String]]
          .getOrElse(Json.obj("rawData" -> a.contextValue).as[Map[String, String]])

        c.executeAction(ta, contextValue, actionPayload)

      case None => Future.successful(ExecutionFailure(List("Context Not Found")))
    }

  }

}

trait ActionContext[Context] {

  val context: Context

  def executeAction(thingId: ThingAndAction, contextValue: Map[String, String], actionPayload: JsObject): Future[ExecutionResult]

}

sealed trait ExecutionResult

trait ExecutionSuccess[T] extends ExecutionResult {
  val value: T
}

case class StringExecutionSuccess(override val value: String) extends ExecutionSuccess[String]

case class StreamExecutionSuccess(override val value: Source[String, akka.NotUsed]) extends ExecutionSuccess[Source[String, akka.NotUsed]]

case class ExecutionFailure(errors: List[String] = Nil) extends ExecutionResult {

  def +(error: String) = this.copy(this.errors.::(error))

}

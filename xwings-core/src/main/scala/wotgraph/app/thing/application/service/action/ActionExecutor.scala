package wotgraph.app.thing.application.service.action

import java.util.UUID

import play.api.libs.json.Json
import scaldi.Injectable._
import scaldi.{CanBeIdentifier, Identifier, Module}

import scala.concurrent.Future
import scala.util.Try

object UUIDCanBeIdentifier extends CanBeIdentifier[UUID] {
  override def toIdentifier(target: UUID): Identifier = target.toString
  implicit val uuidIdentifier = UUIDCanBeIdentifier
}

object ActionExecutor {

  def executeAction(ta: ThingAndAction)(contextProvider: Module) = {

    implicit val cp = contextProvider

    import UUIDCanBeIdentifier._

    val a = ta.action

    Try(inject[ActionContext[_]](identified by a.contextId)).toOption match {
      case Some(c) =>

        val contextValue = Json.parse(a.contextValue).asOpt[Map[String, String]] getOrElse
          Json.obj("rawData" -> a.contextValue).as[Map[String, String]]

        c.executeAction(ta, contextValue)

      case None => Future.successful(ExecutionFailure(List("Context Not Found")))
    }

  }

}

trait ActionContext[Context] {

  val context: Context

  def executeAction(thingId: ThingAndAction, contextValue: Map[String, String]): Future[ExecutionResult]

}

sealed trait ExecutionResult

sealed case class ExecutionSuccess(message: String) extends ExecutionResult

sealed case class ExecutionFailure(errors: List[String] = Nil) extends ExecutionResult {

  def +(error: String) = this.copy(this.errors.::(error))

}

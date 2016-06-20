package wotgraph.app.thing.application.service

import java.util.UUID

import play.api.libs.json.Json
import scaldi.{CanBeIdentifier, Identifier, Module}
import wotgraph.app.thing.domain.entity.Action
import scaldi.Injectable._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object UUIDCanBeIdentifier extends CanBeIdentifier[UUID] {
  override def toIdentifier(target: UUID): Identifier = target.toString
}

object ActionExecutor {

  def executeAction(a: Action)(contextProvider: Module) = {

    implicit val cp = contextProvider
    implicit val uuidIdentifier = UUIDCanBeIdentifier

    Try(inject[ActionContext[_]](identified by a.contextId)).toOption match {
      case Some(c) =>

        val contextValue = Json.parse(a.contextValue).asOpt[Map[String, String]] getOrElse
          Json.obj("rawData" -> a.contextValue).as[Map[String, String]]

        c.executeAction(contextValue)

      case None => Future.successful(ExecutionFailure(List("Context Not Found")))
    }

  }

}

trait ActionContext[Context] {

  val context: Context

  def executeAction(contextValue: Map[String, String]): Future[ExecutionResult]

}

sealed trait ExecutionResult

sealed case class ExecutionSuccess(message: String) extends ExecutionResult

sealed case class ExecutionFailure(errors: List[String] = Nil) extends ExecutionResult {

  def +(error: String) = this.copy(this.errors.::(error))

}

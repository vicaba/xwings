package edu.url.lasalle.wotgraph.application.usecase

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions._
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.domain.entity.thing.action.{ActionExecutor, ExecutionFailure, ExecutionResult}
import edu.url.lasalle.wotgraph.domain.entity.thing.{Action, Metadata, Thing}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

case class CreateThing(metadata: Metadata, actions: Set[Action] = Set.empty, children: Set[UUID] = Set.empty)

object CreateThing {

  def toThing(c: CreateThing): Thing = {

    val metadata = Some(c.metadata)

    val children = c.children.map(Thing(_))

    val actions = c.actions

    Thing(metadata = metadata, actions = actions, children = children)
  }

}

case class GetThings(pageNumber: Int, itemPerPage: Int)

case class ThingUseCase(repo: ThingRepository) {

  def createThing(c: CreateThing): Future[Thing] = {

    val thing = CreateThing.toThing(c)

    repo.create(thing)
  }

  def updateThing(id: String, c: CreateThing): Future[Option[Thing]] = {

    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.failed(new ClientFormatException("UUID not provided"))
      case Success(uuid) =>
        val thing = CreateThing.toThing(c).copy(_id = uuid)
        repo.update(thing)
    }

  }

  def getThings(g: GetThings = GetThings(0, 100))(implicit ec: ExecutionContext): Future[List[Thing]] =
    repo.getAll(g.itemPerPage * g.pageNumber, g.itemPerPage)

  def getThingsAsStream = repo.getAllAsStream

  def getThing(id: String)(implicit ec: ExecutionContext): Future[Option[Thing]] = {
    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.failed(new ClientFormatException("UUID not provided"))
      case Success(uuid) => repo.findById(uuid)
    }
  }

  def deleteThing(id: String): Future[UUID] = {
    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.failed(new ClientFormatException("UUID not provided"))
      case Success(uuid) => repo.delete(uuid)
    }
  }

  def executeThingAction(id: String, actionName: String)(implicit ec: ExecutionContext): Future[ExecutionResult] = {

    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.failed(new ClientFormatException("UUID not provided"))
      case Success(uuid) =>
        repo.findById(uuid).flatMap {
          case Some(thing) =>
            val action = thing.actions.find(_.actionName == actionName)
            action match {
              case Some(a) => ActionExecutor.executeAction(a)
              case _ => Future(ExecutionFailure(List("Action not found")))
            }
          case None => Future(ExecutionFailure(List("Thing not found")))
        }

    }

  }

}

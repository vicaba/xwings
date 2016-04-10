package edu.url.lasalle.wotgraph.application.usecase

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions._
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.domain.thing.{Action, Metadata, Thing}

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

    repo.createThing(thing)
  }

  def updateThing(c: CreateThing): Future[Thing] = {

    val thing = CreateThing.toThing(c)

    repo.updateThing(thing)
  }

  def getThings(g: GetThings = GetThings(0, 100))(implicit ec: ExecutionContext): Future[List[Thing]] =
    repo.getThings(g.itemPerPage * g.pageNumber, g.itemPerPage)

  def getThingsAsStream = repo.getThingsAsStream

  def getThing(id: String)(implicit ec: ExecutionContext): Future[Option[Thing]] = {
    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.failed(new ClientFormatException("UUID not provided"))
      case Success(uuid) => repo.findThingById(uuid)
    }
  }

  def deleteThing(id: String): Future[UUID] = {
    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.failed(new ClientFormatException("UUID not provided"))
      case Success(uuid) => repo.deleteThing(uuid)
    }
  }

}

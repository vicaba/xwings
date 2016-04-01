package edu.url.lasalle.wotgraph.application.usecase

import java.util.UUID

import domain.thing.repository.ThingRepository
import edu.url.lasalle.wotgraph.application.exceptions._
import edu.url.lasalle.wotgraph.domain.thing.{Metadata, Thing}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters._

case class ThingUseCase(repo: ThingRepository) {

  def getThings()(implicit ec: ExecutionContext): Future[Set[Thing]] = {

    def removeActions(leftSet: Set[Thing], rightSet: Set[Thing]): Set[Thing] = {
      val current = leftSet.headOption
      current match {
        case Some(c) =>
          val actions = c.actions.asScala
          val remaining = leftSet.tail
          val parsed = rightSet.+(c).diff(actions)
          removeActions(remaining, parsed)
        case None => rightSet
      }
    }

    val allThings = repo.getThings()

    allThings.map { listOfThings =>
      removeActions(listOfThings.toSet, Set.empty)
    }

  }

  def getThing(id: String)(implicit ec: ExecutionContext): Future[Option[Thing]] = {
    Try(UUID.fromString(id)) match {
      case Failure(_) => Future.failed(new ClientFormatException("UUID not provided"))
      case Success(uuid) =>

        val thingFuture = repo.getThing(uuid)
        val metadataFuture = repo.getThingInfo(uuid)

        thingFuture flatMap {
          case Some(thing) =>
            metadataFuture map {
              case Some(metadata) => thing.setMetadata(Metadata(metadata)); thing
              case None => thing
            } recover { case _ => thing } map { t => Some(thing) }
          case None => Future.successful(None)
        } recover { case _ => throw new ServiceUnavailableException("Service unavailable") }
    }
  }
}

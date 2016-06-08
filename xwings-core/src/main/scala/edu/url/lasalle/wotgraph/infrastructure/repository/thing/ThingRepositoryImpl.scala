package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.CoherenceException
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.domain.entity.thing.Thing
import play.api.libs.iteratee.Enumerator

import scala.concurrent.{ExecutionContext, Future}

case class ThingRepositoryImpl(
                                thingNeo4jRepository: ThingNeo4jRepository,
                                thingMongoDbRepository: ThingMongoDbRepository
                              )
                              (implicit ec: ExecutionContext)
  extends ThingRepository {

  override def findById(id: UUID): Future[Option[Thing]] = {

    val thingNodeF = thingNeo4jRepository.findById(id)
    val thingDataF = thingMongoDbRepository.findById(id)

    thingNodeF.flatMap {

      case Some(thingNode) =>

        thingDataF.map {

          case Some(thingData) =>

            val thing = thingData.copy(_id = thingNode._id, children = thingNode.children)
            Some(thing)

          case None => None

        }

      case None => Future.successful(None)

    }

  }

  override def getAll(skip: Int = 0, limit: Int = 1000): Future[List[Thing]] =
    thingMongoDbRepository.getAll.map(_.toList)

  override def getAllAsStream: Enumerator[Thing] = thingMongoDbRepository.getAllAsStream

  override def create(thing: Thing): Future[Thing] = {

    def createThingNode(t: Thing): Future[Thing] = {

      val thingDataF = thingMongoDbRepository.create(t)
      val thingNodeF = thingNeo4jRepository.create(t)

      thingNodeF zip thingDataF map { _ => t }

    }

    val unidentifiedChildrenInNeo4j = thing.children

    thingNeo4jRepository.getSome(unidentifiedChildrenInNeo4j).flatMap { identifiedChildren =>

      if ((unidentifiedChildrenInNeo4j -- identifiedChildren.toSet).isEmpty) {

        val thingWithIdentifiedChildren = thing.copy(children = identifiedChildren.toSet)
        createThingNode(thingWithIdentifiedChildren)

      } else {

        Future.failed(new CoherenceException("Some children were not found"))
      }

    }

  }

  override def update(thing: Thing): Future[Option[Thing]] = {

    def updateThingNode(t: Thing): Future[Thing] = {

      val thingDataF = thingMongoDbRepository.update(t)
      val thingNodeF = thingNeo4jRepository.update(t)

      thingNodeF zip thingDataF map { _ => t }

    }

    val unidentifiedChildrenInNeo4j = thing.children

    val childrenF = thingNeo4jRepository.getSome(unidentifiedChildrenInNeo4j)

    thingNeo4jRepository.findById(thing._id).flatMap {

      case Some(t) =>

        childrenF.flatMap { identifiedChildren =>

          thingNeo4jRepository.getSome(unidentifiedChildrenInNeo4j).flatMap { identifiedChildren =>

            if ((unidentifiedChildrenInNeo4j -- (identifiedChildren toSet)) isEmpty) {

              val thingToUpdate = thing.copy(id = t.id, children = identifiedChildren.toSet)
              updateThingNode(thingToUpdate).map(Some(_))

            } else {

              Future.failed(new CoherenceException("Some children were not found"))

            }

          }

        }

      case None => Future.successful(None)

    }
  }

  override def delete(id: UUID): Future[UUID] = {

    val thingNodeF = thingNeo4jRepository.delete(id)
    val thingDataF = thingMongoDbRepository.delete(id)

    thingNodeF zip thingDataF map { _ => id }

  }

  override def deleteAll(): Unit = {
    thingMongoDbRepository.deleteAll()
    thingNeo4jRepository.deleteAll()
  }

}


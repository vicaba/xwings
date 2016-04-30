package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.{CoherenceException, ReadOperationException}
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.domain.thing.{Action, Metadata, Thing}
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.{JsObject, Json}
import reactivemongo.core.errors.DatabaseException
import scaldi.Injectable._

import scala.concurrent.{Await, ExecutionContext, Future}

case class ThingRepositoryImpl(
                                thingNeo4jRepository: ThingNeo4jRepository,
                                thingMongoDbRepository: ThingMongoDbRepository
                              )
                              (implicit ec: ExecutionContext)
  extends ThingRepository {

  override def findThingById(id: UUID): Future[Option[Thing]] = {

    val thingNodeF = thingNeo4jRepository.findThingById(id)
    val thingDataF = thingMongoDbRepository.findThingById(id)

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

  override def getThings(skip: Int = 0, limit: Int = 1000): Future[List[Thing]] =
    thingMongoDbRepository.getThings.map(_.toList)

  override def getThingsAsStream: Enumerator[Thing] = thingMongoDbRepository.getThingsAsStream

  override def createThing(thing: Thing): Future[Thing] = {

    def createThingNode(t: Thing): Future[Thing] = {

      val thingDataF = thingMongoDbRepository.createThing(t)
      val thingNodeF = thingNeo4jRepository.createThing(t)

      thingNodeF zip thingDataF map { _ => t }

    }

    val unidentifiedChildrenInNeo4j = thing.children

    thingNeo4jRepository.getThings(unidentifiedChildrenInNeo4j).flatMap { identifiedChildren =>

      if ((unidentifiedChildrenInNeo4j diff(identifiedChildren toSet)) isEmpty) {

        val thingWithIdentifiedChildren = thing.copy(children = identifiedChildren.toSet)
        createThingNode(thingWithIdentifiedChildren)

      } else {

        Future.failed(new CoherenceException("Some children were not found"))
      }


    }

  }

  override def updateThing(thing: Thing): Future[Option[Thing]] = {

    def updateThingNode(t: Thing): Future[Thing] = {

      val thingDataF = thingMongoDbRepository.updateThing(t)
      val thingNodeF = thingNeo4jRepository.updateThing(t)

      thingNodeF zip thingDataF map { _ => t }

    }

    val unidentifiedChildrenInNeo4j = thing.children

    val childrenF = thingNeo4jRepository.getThings(unidentifiedChildrenInNeo4j)

    thingNeo4jRepository.findThingById(thing._id).flatMap {

      case Some(t) =>

        childrenF.flatMap { identifiedChildren =>

          thingNeo4jRepository.getThings(unidentifiedChildrenInNeo4j).flatMap { identifiedChildren =>

            if ((unidentifiedChildrenInNeo4j diff(identifiedChildren toSet)) isEmpty) {

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

  override def deleteThing(id: UUID): Future[UUID] = {

    val thingNodeF = thingNeo4jRepository.deleteThing(id)
    val thingDataF = thingMongoDbRepository.deleteThing(id)

    thingNodeF zip thingDataF map { _ => id }

  }

}

object Main {

  def createThing(identifier: Int): Thing = {

    val id = UUID.randomUUID()
    val actions = Set(Action("getConsume", UUID.randomUUID(), Json.obj("a" -> "a")))
    val metadata = Json.parse("""{"position":{"type":"Feature","geometry":{"type":"Point","coordinates":[42.6,32.1]},"properties":{"name":"Dinagat Islands"}},"ip":"192.168.22.19"}""")
    val t = new Thing(id, Some(Metadata(metadata.as[JsObject])), actions)

    t

  }


  def main(args: Array[String]) {
    implicit val ec = scala.concurrent.ExecutionContext.global
    import scala.concurrent.duration._

    val repo: ThingRepository = inject[ThingRepository](identified by 'ThingRepository)

    def createNodes() = {
      var i = 0
      while (i < 1) {
        i = i + 1
        val t = createThing(1)
        val t2 = createThing(2)
        val t3 = createThing(3)
        val t4 = createThing(4)
        val tWithChildren = t.copy(children = Set(t2))
        val t2WithChildren = t2.copy(children = Set(t3))
        val t3WithChildren = t3.copy(children = Set(t4))

        val f1 = repo.createThing(t4)
        Await.result(f1, 3.seconds)
        val f2 = repo.createThing(t3WithChildren)
        Await.result(f2, 3.seconds)
        val f3 = repo.createThing(t2WithChildren)
        Await.result(f3, 3.seconds)
        val f4 = repo.createThing(tWithChildren)
        Await.result(f4, 3.seconds)
      }
    }

    //createNodes()

    repo.findThingById(UUID.fromString("3e838ec2-d6d0-4765-acf9-4b0ec7b1d7d5")).map(println) recover {
      case t: ReadOperationException => println(s"EXC: ${t.msg}")
    }

    /*

    val child1 = Thing(_id = UUID.fromString("e8d4f376-0dbf-4860-a05a-60539998cd12"))

    val child2 = Thing(_id = UUID.fromString("7edf9150-6a63-4bb3-b65e-e901042d07b2"))

    repo.updateThing(Thing(_id = UUID.fromString("24b0b344-3d6c-48ac-aa1b-d8f57826fad4")).copy(children = Set(child1, child2))).map { t =>
      println(t)
    }

    */

  }
}


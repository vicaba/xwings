package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.{CoherenceException, ReadOperationException}
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.domain.entity.thing.action.ContextProvider
import edu.url.lasalle.wotgraph.domain.entity.thing.{Action, Metadata, Thing}
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.{JsObject, Json}
import scaldi.Injectable._

import scala.concurrent.{Await, ExecutionContext, Future}

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

}

object Main {

  def createThing(identifier: Int): Thing = {

    val id = UUID.randomUUID()
    val contextValue = Map("httpMethod"-> "GET", "url" -> "https://es.wikipedia.org/wiki/Wikipedia:Portada")
    val actions = Set(
      Action(
        "getConsume", UUID.fromString(ContextProvider.HTTP_CONTEXT), Json.toJson(contextValue).as[JsObject].toString()
      )
    )
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

        val f1 = repo.create(t4)
        Await.result(f1, 3.seconds)
        val f2 = repo.create(t3WithChildren)
        Await.result(f2, 3.seconds)
        val f3 = repo.create(t2WithChildren)
        Await.result(f3, 3.seconds)
        val f4 = repo.create(tWithChildren)
        Await.result(f4, 3.seconds)
      }
    }

    createNodes()

    //    repo.findThingById(UUID.fromString("3e838ec2-d6d0-4765-acf9-4b0ec7b1d7d5")).map(println) recover {
    //      case t: ReadOperationException => println(s"EXC: ${t.msg}")
    //    }

    //repo.createThing(Thing(children = Set(Thing(_id = UUID.fromString("3e838ec2-d6d0-4765-acf9-4b0ec7b1d7d5")))))

    /*

    val child1 = Thing(_id = UUID.fromString("e8d4f376-0dbf-4860-a05a-60539998cd12"))

    val child2 = Thing(_id = UUID.fromString("7edf9150-6a63-4bb3-b65e-e901042d07b2"))

    repo.updateThing(Thing(_id = UUID.fromString("24b0b344-3d6c-48ac-aa1b-d8f57826fad4")).copy(children = Set(child1, child2))).map { t =>
      println(t)
    }

    */

  }
}


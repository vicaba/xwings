package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.SaveException
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.domain.thing.{Action, Metadata, Thing}
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import play.api.libs.json.{JsObject, Json}
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
            val thing = thingData.copy(_id = thingNode._id, children = thingData.children)
            Some(thing)

          case None => None

        }

      case None => Future.successful(None)
    }

  }

  override def getThings(skip: Int = 0, limit: Int = 1000): Future[List[Thing]] =
    thingMongoDbRepository.getThings.map(_.toList)

  override def createThing(thing: Thing): Future[Thing] = {

    def createThingNode(t: Thing): Future[Thing] = {

      val thingDataF = thingMongoDbRepository.createThing(t)

      val thingNodeF = thingNeo4jRepository.createThing(t)

      thingNodeF zip thingDataF map { _ => t }

    }

    val unidentifiedChildrenInNeo4j = thing.children

    thingNeo4jRepository.getThings(unidentifiedChildrenInNeo4j).flatMap { identifiedChildren =>
      println("-----")
      println(identifiedChildren)
      val thingWithIdentifiedChildren = thing.copy(children = identifiedChildren.toSet)
      createThingNode(thingWithIdentifiedChildren)
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

    val actions = Set(Action("getConsume", UUID.randomUUID(), "a"))

    val metadata = Json.parse("""{"position":{"type":"Feature","geometry":{"type":"Point","coordinates":[42.6,32.1]},"properties":{"name":"Dinagat Islands"}},"ip":"192.168.22.19"}""")

    val t = new Thing(id, Some(Metadata(metadata.as[JsObject])), actions)

    t

  }

  def main(args: Array[String]) {
    implicit val ec = scala.concurrent.ExecutionContext.global
    import scala.concurrent.duration._

    val repo: ThingRepository = inject[ThingRepository](identified by 'ThingRepository)


    val t = createThing(1)
    val t2 = createThing(2)
    val t3 = createThing(3)
    val tWithChildren = t.copy(children = Set(t2, t3))

    val f1 = repo.createThing(t2)
    Await.result(f1, 3.seconds)
    val f2 = repo.createThing(t3)
    Await.result(f2, 3.seconds)
    val f3 = repo.createThing(tWithChildren)
    Await.result(f3, 3.seconds)

/*
    val ta = createThing(4)
    val t2a = createThing(5)
    val t3a = createThing(6)
    val tWithChildrena = ta.copy(children = Set(t2a, t3a))


    repo.createThing(t2a)
    repo.createThing(t3a)
    repo.createThing(tWithChildrena)
    */

    repo.getThings().map { l =>
      println(l)
    }

    repo.deleteThing(UUID.fromString("d3c66b40-c840-487a-ac62-3d4f0bf65713")).map { t =>
      println(t)
    }

  }
}


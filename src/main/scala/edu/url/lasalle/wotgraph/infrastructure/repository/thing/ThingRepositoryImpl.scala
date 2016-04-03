package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.{PartialUpdateException, ServiceUnavailableException}
import edu.url.lasalle.wotgraph.domain.repository.thing.ThingRepository
import edu.url.lasalle.wotgraph.domain.thing.{Action, Metadata, Thing}
import org.neo4j.ogm.cypher.query.Pagination
import org.neo4j.ogm.cypher.Filter
import play.api.libs.json.{JsObject, Json}
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.Implicits._
import scaldi.Injectable._
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import edu.url.lasalle.wotgraph.infrastructure.repository.action.ActionMongoRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.metadata.MetadataMongoRepository
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper
import edu.url.lasalle.wotgraph.infrastructure.repository.thing.neo4j.{Neo4jThing, Neo4jThingHelper}

import scala.concurrent.{ExecutionContext, Future}

case class ThingRepositoryImpl(
                                override val neo4jconf: Neo4jConf.Config,
                                metadataMongoRepository: MetadataMongoRepository,
                                actionMongoRepository: ActionMongoRepository
                              )
                              (implicit ec: ExecutionContext)
  extends ThingRepository
    with Neo4jOGMHelper {


  private def getThingActions(id: UUID): Future[Option[Set[Action]]] = actionMongoRepository.findById(id)

  override def getThing(id: UUID): Future[Option[Thing]] = {

    val session = getSession()

    val thingF = Future {
      session.loadAll(classOf[Neo4jThing], new Filter("_id", id.toString))
    }

    val actionsF = getThingActions(id)
    val metadataF = getThingMetadata(id)

    thingF.flatMap { thing =>
      collectionToList(thing).headOption.map(Neo4jThingHelper.neo4jThingAsThingView) match {
        case None => Future.successful(None)
        case Some(t) =>
          actionsF.flatMap {
            case Some(actions) =>
              metadataF.map {
                case Some(m) =>
                  val metadata = Some(m)
                  Some(t.copy(actions = actions, metadata = metadata))
                case None => Some(t.copy(actions = actions))
              }
            case None => Future.successful(Some(t))
          }
      }
    } recover { case _ => throw new ServiceUnavailableException() }
  }

  override def getThings(skip: Int = 0, limit: Int = 1000): Future[List[Thing]] = {
    val session = getSession()

    Future(session.loadAll(classOf[Neo4jThing], new Pagination(skip, limit))) flatMap {
      case collection =>
        val listOfThings = collectionToList(collection).map(Neo4jThingHelper.neo4jThingAsThingView)
        val listOfIds = listOfThings.map(_._id).toSet
        val listOfActionsF = actionMongoRepository.getActionsForThingIds(listOfIds)

        val listOfThingsWithActionsF = listOfActionsF.map { listOfActions =>
          val maps = listOfActions.map(s => s.head.thingId -> s).toMap
          listOfThings.map { thing =>
            maps.get(thing._id).map { actions =>
              thing.copy(actions = actions)
            }.getOrElse(thing)
          }
        }

        listOfThingsWithActionsF
    } recover { case _ => throw new ServiceUnavailableException() }
  }

  override def getThingMetadata(id: UUID): Future[Option[Metadata]] = {
    metadataMongoRepository.findById(id)
  }

  private def saveMetadataAndActionsForThing(t: Thing) = {
    val metadataMongoF = t.metadata match {
      case Some(metadata) => metadataMongoRepository.create(metadata.copy(thingId = t._id)).flatMap {
        case Left(wr) => Future.failed(new PartialUpdateException("Thing partially created, metadata not set"))
        case Right(m) => Future.successful(Some(metadata))
      }
      case _ => Future.successful(None)
    }

    val actionMongoF = actionMongoRepository.create(t.actions).flatMap {
      case Left(wr) => Future.failed(new PartialUpdateException("Thing partially created, actions not set"))
      case Right(actions) => Future.successful(actions)
    }

    for {
      metadata <- metadataMongoF
      actions <- actionMongoF
    } yield {
      t
    }

  }

  override def createThing(t: Thing): Future[Thing] = {

    import edu.url.lasalle.wotgraph.infrastructure.repository.thing.neo4j.Neo4jThingHelper._

    val session = getSession()
    val neo4JThing: Neo4jThing = t

    val thingNeo4jF = Future {
      session.save[Neo4jThing](neo4JThing)
      t
    }

    val metadataMongoF = t.metadata match {
      case Some(metadata) => metadataMongoRepository.create(metadata.copy(thingId = t._id)).flatMap {
        case Left(wr) => Future.failed(new PartialUpdateException("Thing partially created, metadata not set"))
        case Right(m) => Future.successful(Some(metadata))
      }
      case _ => Future.successful(None)
    }

    val actionMongoF = actionMongoRepository.create(t.actions).flatMap {
      case Left(wr) => Future.failed(new PartialUpdateException("Thing partially created, actions not set"))
      case Right(actions) => Future.successful(actions)
    }

    t.children.map(saveMetadataAndActionsForThing)

    for {
      thingNeo4j <- thingNeo4jF
      metadata <- metadataMongoF
      actions <- actionMongoF
    } yield {
      val thing = Neo4jThingHelper.neo4jThingAsThingView(thingNeo4j)
      thing.copy(metadata = metadata, actions = actions)
    }

  }

}

object Main {

  def createThing(identifier: Int): Thing = {

    def hName(id: Int) = s"Thing_$id"

    val id = UUID.randomUUID()

    val actions = Set(Action("getConsume", UUID.randomUUID(), "a", id))

    val metadata = Json.parse("""{"metadata":{"position":{"type":"Feature","geometry":{"type":"Point","coordinates":[42.6,32.1]},"properties":{"name":"Dinagat Islands"}},"ip":"192.168.22.19"}}""")

    val t = new Thing(id, hName(identifier), Some(Metadata(metadata.as[JsObject], id)), actions)

    t

  }

  def main(args: Array[String]) {
    implicit val ec = scala.concurrent.ExecutionContext.global

    val repo: ThingRepository = inject[ThingRepository](identified by 'ThingRepository)

    val t = createThing(1)
    val t2 = createThing(2)
    val t3 = createThing(3)
    val tWithChildren = t.copy(children = Set(t2, t3))

    repo.createThing(tWithChildren)

    val ta = createThing(4)
    val t2a = createThing(5)
    val t3a = createThing(6)
    val tWithChildrena = ta.copy(children = Set(t2a, t3a))

    repo.createThing(tWithChildrena)

    repo.getThings().map { l =>
      println(l)
    }s

    repo.getThing(UUID.fromString("a1384531-7fcc-4a8e-a1b2-52a5e03dfdc6")).map { t =>
      println(t)
    }

  }
}


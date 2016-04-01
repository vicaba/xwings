package edu.url.lasalle.wotgraph.infrastructure.thing.repository

import java.util.UUID

import domain.thing.repository.ThingRepository
import edu.url.lasalle.infrastructure.serializers.json.Implicits
import edu.url.lasalle.wotgraph.application.exceptions.ServiceUnavailableException
import edu.url.lasalle.wotgraph.domain.thing.{Metadata, Thing}
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.MongoDbConfig
import Implicits._
import org.neo4j.ogm.cypher.query.Pagination
import org.neo4j.ogm.cypher.Filter
import play.api.libs.json.Reads._
import play.api.libs.json.{JsObject, Json, Writes}
import play.modules.reactivemongo.json._
import scaldi.Injectable._
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

case class ThingRepositoryImpl(
                                override val neo4jconf: Neo4jConf.Config,
                                mongoDbConfig: MongoDbConfig
                              )
                              (implicit ec: ExecutionContext)
  extends ThingRepository
    with Neo4jOGMHelper {

  val mongoDbCollection = mongoDbConfig.collection

  override def getThing(id: UUID): Future[Option[Thing]] = {
    val session = getSession()
    Future {
      val result = session.loadAll(classOf[Thing], new Filter("_id", id.toString))
      collectionToList(result).headOption
    }
  }

  override def getThings(skip: Int = 0, limit: Int = 1000): Future[List[Thing]] = {
    val session = getSession()

    Try(session.loadAll(classOf[Thing], new Pagination(skip, limit))) match {
      case Failure(e) => Future.failed(new ServiceUnavailableException())
      case Success(collection) => Future.successful(collectionToList(collection))
    }
  }


  override def getThingInfo(id: UUID): Future[Option[JsObject]] = {
    mongoDbCollection.find(Json.obj("_id" -> id)).one[JsObject]
  }

  override def createThing(hName: String, metadata: Metadata, children: Set[UUID]): Future[Thing] = {

  }
}

object Main {

  def main(args: Array[String]) {
    implicit val ec = scala.concurrent.ExecutionContext.global

    val repo: ThingRepository = inject[ThingRepository](identified by 'ThingRepository)

    import scala.collection.JavaConverters._

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

    /*    val res = repo.getThings().map(_.map { t =>
          ThingSerializer.ThingWrites.writes(t)
        })*/

    val res = repo.getThings()

    res.map { listOfThings =>
      val result = removeActions(listOfThings.toSet, Set.empty)
      val json = Writes.set[Thing].writes(result).toString
      json
    }

    /*    repo.getThing(UUID.fromString("87ffdcc2-c28c-434f-9f4f-bc3ac0da21b3")).map(_.foreach { t =>
          val res = ThingSerializer.ThingWrites.writes(t)
          println(res)
        })*/

  }
}


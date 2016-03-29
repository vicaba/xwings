package edu.url.lasalle.wotgraph.infrastructure.thing.repository

import java.util
import java.util.UUID

import domain.thing.repository.ThingRepository
import edu.url.lasalle.wotgraph.domain.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.AppConfig
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.MongoDbConfig
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.serializers.Implicits._
import org.neo4j.ogm.cypher.query.Pagination
import org.neo4j.ogm.cypher.Filter
import org.neo4j.ogm.session.SessionFactory
import play.api.libs.json.Reads._
import play.api.libs.json.{JsObject, Json, Writes}
import play.modules.reactivemongo.json._
import scaldi.Injectable._
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf

import scala.concurrent.{ExecutionContext, Future}

case class ThingRepositoryImpl(
                                neo4jconf: Neo4jConf.Config,
                                mongoDbConfig: MongoDbConfig
                              )
                              (implicit ec: ExecutionContext)
  extends ThingRepository
{
  val mongoDbCollection = mongoDbConfig.collection

  def getSession(): org.neo4j.ogm.session.Session = {
    val sessionFactory = new SessionFactory(neo4jconf.packages:_*)
    sessionFactory.openSession(neo4jconf.server.toString, neo4jconf.credentials.username, neo4jconf.credentials.password)
  }

  def collectionToList(coll: util.Collection[Thing]): List[Thing] = {
    coll.toArray.toList.asInstanceOf[List[Thing]]
  }

  override def getThing(id: UUID): Future[Option[Thing]] = {
    val session = getSession()
    Future {
      val result = session.loadAll(classOf[Thing], new Filter("_id", id.toString))
      collectionToList(result).headOption
    }
  }

  override def getThings(skip: Int = 0, limit: Int = 1000): Future[List[Thing]] = {
    val session = getSession()
    Future {
      val result = session.loadAll(classOf[Thing], new Pagination(skip,limit))
      collectionToList(result)
    }
  }

  override def createThing(thing: Thing): Future[Thing] = ???

  override def getThingInfo(id: UUID): Future[Option[JsObject]] = {
    mongoDbCollection.find(Json.obj("_id" -> id)).one[JsObject]
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


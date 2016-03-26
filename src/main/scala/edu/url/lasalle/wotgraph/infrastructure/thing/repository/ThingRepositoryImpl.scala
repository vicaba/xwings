package edu.url.lasalle.wotgraph.infrastructure.thing.repository

import java.util
import java.util.UUID

import domain.thing.repository.ThingRepository
import edu.url.lasalle.wotgraph.infrastructure.AppConfig
import edu.url.lasalle.wotgraph.infrastructure.repository.mongodb.{MongoDbConfig, ThingsMongoEnvironment}
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConfig
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.serializers.Implicits._
import edu.url.lasalle.wotgraph.infrastructure.thing.repository.neo4j.mappings.Thing
import org.neo4j.ogm.cypher.query.Pagination
import org.neo4j.ogm.cypher.{Filter, Filters}
import org.neo4j.ogm.session.SessionFactory
import play.api.libs.json.Reads._
import play.api.libs.json.{JsObject, JsValue, Json, Writes}
import play.api.libs.ws.ning.NingWSClient
import play.modules.reactivemongo.json._

import scala.concurrent.{ExecutionContext, Future}

case class ThingRepositoryImpl(
                                neo4jConfig: Neo4jConfig,
                                mongoDbConfig: MongoDbConfig
                              )
                              (implicit ec: ExecutionContext)
  extends ThingRepository
{
  val mongoDbCollection = mongoDbConfig.collection

  def getSession(): org.neo4j.ogm.session.Session = {
    val sessionFactory = new SessionFactory("edu.url.lasalle.wotgraph.infrastructure.thing.repository.neo4j.mappings")
    sessionFactory.openSession(s"http://${AppConfig.defaultConf.getString("neo4j.server")}", "neo4j", "xneo4j")
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

  override def getThingInfo(id: UUID): Future[Option[JsValue]] = {
    mongoDbCollection.find(Json.obj("_id" -> id)).one[JsValue]
  }

}

object Main {

  def main(args: Array[String]) {
    val wsClient = NingWSClient()
    val conf = AppConfig.defaultConf
    val neo4jConfig = Neo4jConfig(wsClient, conf.getString("neo4j.server"), "http", "neo4j", "xneo4j")
    val mongoDbConfig = MongoDbConfig(ThingsMongoEnvironment(AppConfig.defaultConf).db.collection("metadata"))
    implicit val ec = scala.concurrent.ExecutionContext.global
    val repo = ThingRepositoryImpl(neo4jConfig, mongoDbConfig)

    //(createRequestForNeo4j _ andThen createNodesTest)(wsClient).execute().map(r => println(r))

/*    repo.createThing(
      Thing(
        humanName = "SomeThing"
        , action = Action("", UUID.randomUUID(), "")
        , labels = Nil
        , relations = Nil)
    ).map(r => println(r))*/

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


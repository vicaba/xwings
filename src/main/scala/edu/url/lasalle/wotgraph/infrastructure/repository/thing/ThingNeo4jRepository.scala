package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.SaveException
import edu.url.lasalle.wotgraph.domain.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf.Config
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.ThingSerializer
import org.neo4j.ogm.cypher.{BooleanOperator, Filter, Filters}

import java.util

import scala.concurrent.{ExecutionContext, Future}

case class ThingNeo4jRepository(
                                 override val neo4jConf: Config
                               )(implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  val DefaultQueryDepth = 0

  def findById(id: UUID): Future[Option[Thing]] = {

    Future {
      iterableToList(session.loadAll(classOf[Neo4jThing], new Filter(ThingSerializer.IdKey, id.toString), DefaultQueryDepth))
        .headOption.map(Neo4jThingHelper.neo4jThingAsThingView)
    }

  }

  def create(t: Thing): Future[Thing] = {

    Future {
      session.save[Neo4jThing](Neo4jThingHelper.thingAsNeo4jThingView(t))
      t
    }
  }

  def getThings(o: Iterable[Thing]): Future[Iterable[Thing]] = {

    def getThingsForNonEmptyIterable(o: Iterable[Thing]): Future[Iterable[Thing]] = {

      def idFilterProducer(id: UUID) = new Filter(ThingSerializer.IdKey, id.toString)

      val firstQueryPart = """MATCH (n:Thing) WHERE"""

      val queryFilters = o.map(_._id.toString).mkString(s"""n._id = """", s"""" OR n._id = """", """"""")

      val queryEnd = "RETURN n"

      val query = s"$firstQueryPart $queryFilters $queryEnd"

      val emptyMap = new util.HashMap[String, Object]

      Future {
        iterableToList(session.query(classOf[Neo4jThing], query, emptyMap)) map Neo4jThingHelper.neo4jThingAsThingView
      }
    }

    o match {
      case _ if o.isEmpty => Future.successful(o)
      case _ => getThingsForNonEmptyIterable(o)
    }

  }

  def deleteThing(id: UUID): Future[UUID] = {

    val query = s"""MATCH (n:Thing { _id: "${id.toString}"}) DETACH DELETE (n)"""

    val emptyMap = new util.HashMap[String, Object]

    Future {
      session.query(query, emptyMap)
    } flatMap { r =>
      if (r.queryStatistics.getNodesDeleted == 1)
        Future.successful(id)
      else
        Future.failed(new SaveException(s"Can't delete thing with id: ${id.toString}"))
    }

  }
}

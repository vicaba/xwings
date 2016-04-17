package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.{DeleteException, ReadException, SaveException}
import edu.url.lasalle.wotgraph.domain.thing.Thing
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf.Config
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.ThingSerializer
import org.neo4j.ogm.cypher.{BooleanOperator, Filter, Filters}
import java.util
import scala.collection.JavaConverters._

import scala.concurrent.{ExecutionContext, Future}

case class ThingNeo4jRepository(
                                 override val neo4jConf: Config
                               )(implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  val DefaultQueryDepth = 1

  def findThingById(id: UUID): Future[Option[Thing]] = {

    Future {

      val query = s"""MATCH (n:Thing {_id: "c166c0d2-1cfd-4479-a178-325cab4fce7e"}) OPTIONAL MATCH (n)-[r:CHILD]->(n2) RETURN n AS node ,n2._id AS childrenIds""";

      val queryResult = session.query(query, createEmptyMap)

      val result = queryResult.queryResults().asScala.map(_.asScala)

      result.headOption.map { head =>

        val thingId = UUID.fromString(head.get("node").get.asInstanceOf[String])

        val children = result.tail.flatMap(_.get("childrenIds")).map(_.asInstanceOf[String]).map(id => Thing(UUID.fromString(id)))

        Thing(_id = thingId, children = children.toSet)

      }

    } recover { case e: Throwable => throw new ReadException(s"Can't get Thing with id: $id") }

  }


  def createThing(thing: Thing): Future[Thing] = {

    Future {
      session.save[Neo4jThing](Neo4jThingHelper.thingAsNeo4jThingView(thing))
      thing
    } recover { case e: Throwable => throw new SaveException(s"sCan't create Thing with id: ${thing._id}") }
  }

  def getThings(things: Iterable[Thing]): Future[Iterable[Thing]] = {

    def getThingsForNonEmptyIterable(ts: Iterable[Thing]): Future[Iterable[Thing]] = {

      def idFilterProducer(id: UUID) = new Filter(ThingSerializer.IdKey, id.toString)

      val firstQueryPart = """MATCH (n:Thing) WHERE"""

      val queryFilters = ts.map(_._id.toString).mkString(s"""n._id = """", s"""" OR n._id = """", """"""")

      val queryEnd = "RETURN n"

      val query = s"$firstQueryPart $queryFilters $queryEnd"

      val emptyMap = new util.HashMap[String, Object]

      Future {
        iterableToList(session.query(classOf[Neo4jThing], query, emptyMap)) map Neo4jThingHelper.neo4jThingAsThingView
      } recover { case e: Throwable => throw new ReadException("Can't get Things") }
    }

    things match {
      case _ if things.isEmpty => Future.successful(things)
      case _ => getThingsForNonEmptyIterable(things)
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
        Future.failed(new DeleteException(s"Can't delete thing with id: ${id.toString}"))
    }
  }

  private def createEmptyMap = new util.HashMap[String, Object]
}

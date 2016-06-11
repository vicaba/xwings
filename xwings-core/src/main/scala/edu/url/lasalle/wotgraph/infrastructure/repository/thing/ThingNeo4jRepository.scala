package edu.url.lasalle.wotgraph.infrastructure.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.{DeleteException, ReadException, SaveException}
import edu.url.lasalle.wotgraph.domain.entity.thing.Thing
import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Permission
import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Permission.Keys
import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Role.Keys._
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf.Config
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.entity.Neo4jThing
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper
import edu.url.lasalle.wotgraph.infrastructure.repository.permission.PermissionNeo4jRepository
import org.neo4j.ogm.cypher.{BooleanOperator, Filter, Filters}

import scala.collection.JavaConverters._
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.ThingSerializer
import org.neo4j.ogm.session.Session

import scala.concurrent.{ExecutionContext, Future}

case class ThingNeo4jRepository(
                                 session: Session
                               )
                               (implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  val DefaultQueryDepth = 1

  val ThingLabel = "Thing"

  val IdKey = ThingSerializer.IdKey

  val ChildrenKey = ThingSerializer.ChildrenKey

  val ChildRelKey = "CHILD"

  def findById(id: UUID): Future[Option[Thing]] = {

    Future {

      val query =
        s"""
           |MATCH (n:$ThingLabel {_id: "$id"}) OPTIONAL MATCH (n)-[r:CHILD]->(n2)
           | RETURN n.$IdKey AS $IdKey, n2._id AS $ChildrenKey""".stripMargin;

      val queryResult = session.query(query, emptyMap)
      val result = resultCollectionAsScalaCollection(queryResult)

      result.headOption.map { head =>

        val thingId = UUID.fromString(head.get(IdKey).get.asInstanceOf[String])
        val children = result.flatMap(_.get(ChildrenKey).flatMap(Option(_))).map(_.asInstanceOf[String]).map(id => Thing(UUID.fromString(id)))

        Thing(_id = thingId, children = children.toSet)

      }

    } recover { case e: Throwable => throw new ReadException(s"Neo4j: Can't get Thing with id: $id") }

  }


  def create(thing: Thing): Future[Thing] = {

    val createQuery = createAndLink1QueryFactory(
      nodeDefinition = s"""(n:$ThingLabel {$IdKey: "${thing._id}"})""",
      relatees = thing.children,
      relateeQueryMatchDefinition = (i: Int, t: Thing) =>
        s"""(n$i:$ThingLabel {$IdKey: "${t._id}"})""",
      relationDefinition = (i: Int) =>
        s"""(n)-[r$i:$ChildRelKey]->(n$i)"""
    )

    Future {
      session.query(createQuery, emptyMap)
      thing
    } recover { case e: Throwable => throw new SaveException(s"sCan't create Thing with id: ${thing._id}") }
  }

  def update(thing: Thing): Future[Thing] = {

    val thingMatch = s"""(n:$ThingLabel {$IdKey: "${thing._id}"})"""
    val thingChildren = thing.children

    val deleteChildrenQuery: String =
      s"""${Keywords.Match} $thingMatch-[r:CHILD]->() DELETE r"""

    val linkToChildrenQuery = matchAndLink1QueryFactory(
      nodeDefinition =  thingMatch,
      relatees = thingChildren,
      relateeQueryMatchDefinition = (i: Int, t: Thing) =>
        s"""(n$i:$ThingLabel {$IdKey: "${t._id}"})""",
      relationDefinition = (i: Int) =>
        s"""(n)-[r$i:$ChildRelKey]->(n$i)"""
    )

    lazy val deleteChildrenF = Future {
      session.query(deleteChildrenQuery, emptyMap)
      thing
    } recover { case e: Throwable => throw new DeleteException(s"Can't delete relationships of Thing with id: ${thing._id}") }

    lazy val createChildrenF = Future {
      session.query(linkToChildrenQuery, emptyMap)
      thing
    } recover { case e: Throwable => throw new SaveException(s"sCan't create relationships of Thing with id: ${thing._id}") }

    if (thingChildren.isEmpty)
      deleteChildrenF
    else
      deleteChildrenF zip createChildrenF map (_ => thing)

  }

  def getSome(things: Iterable[Thing]): Future[Iterable[Thing]] = {

    def getThingsForNonEmptyIterable(ts: Iterable[Thing]): Future[Iterable[Thing]] = {

      val firstQueryPart = s"""MATCH (n:$ThingLabel) WHERE"""
      val queryFilters = ts.map(_._id.toString).mkString(s"""n.$IdKey = """", s"""" OR n.$IdKey = """", """"""")
      val queryEnd = "RETURN n"

      val query = s"$firstQueryPart $queryFilters $queryEnd"

      Future {
        iterableToList(session.query(classOf[Neo4jThing], query, emptyMap)) map Neo4jThingHelper.neo4jThingAsThingView
      } recover { case e: Throwable => throw new ReadException("Can't get Things") }
    }

    things match {
      case _ if things.isEmpty => Future.successful(things)
      case _ => getThingsForNonEmptyIterable(things)
    }

  }

  def delete(id: UUID): Future[UUID] = {

    val query = s"""MATCH (n:$ThingLabel { $IdKey: "${id.toString}"}) DETACH DELETE (n)"""

    Future {
      session.query(query, emptyMap)
    } flatMap { r =>
      if (r.queryStatistics.getNodesDeleted == 1)
        Future.successful(id)
      else
        Future.failed(new DeleteException(s"Can't delete thing with id: ${id.toString}"))
    }
  }

  def deleteAll(): Unit = Future {
    session.query(s"""MATCH (n:$ThingLabel) DETACH DELETE n""", emptyMap)
  }

}

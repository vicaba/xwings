package wotgraph.app.permission.infrastructure.repository.neo4j

import java.util.UUID

import org.neo4j.ogm.session.Session
import wotgraph.app.exceptions.{DeleteException, ReadException, SaveException}
import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.permission.infrastructure.serialization.keys.PermissionKeys._
import wotgraph.toolkit.repository.neo4j.helpers.Neo4jOGMHelper

import scala.concurrent.{ExecutionContext, Future}

object PermissionNeo4jRepository {

  object Keys {
    val PermLabel = "Perm"
  }

}

case class PermissionNeo4jRepository(
                                      session: Session
                                    )
                                    (implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  import Neo4jHelper._
  import PermissionNeo4jRepository.Keys._

  def findById(id: UUID): Future[Option[Permission]] = {

    Future {

      val query =
        s"""MATCH (n:$PermLabel { $Id: "$id" }) RETURN n.$Id AS $Id, n.$Desc AS $Desc"""

      val queryResult = session.query(query, emptyMap)

      val result = resultCollectionAsScalaCollection(queryResult)

      result.headOption.map { head =>

        val permId = UUID.fromString(head.get(Id).get.asInstanceOf[String])
        val permDesc = head.get(Desc).get.asInstanceOf[String]

        Permission(permId, permDesc)

      }

    } recover { case e: Throwable => throw new ReadException(s"Neo4j: Can't get Permission with id: $id") }

  }

  def getSome(perms: Iterable[Permission]): Future[Iterable[Permission]] = {

    def getPermissionsForNonEmptyIterable(perms: Iterable[Permission]): Future[Iterable[Permission]] = {

      val firstQueryPart = s"""MATCH (n:$PermLabel) WHERE"""
      val queryFilters = perms.map(_.id.toString).mkString(s"""n.$Id = """", s"""" OR n.$Id = """", """"""")
      val queryEnd = "RETURN n"

      val query = s"$firstQueryPart $queryFilters $queryEnd"

      Future {
        val queryResult = session.query(query, emptyMap)
        val result = resultCollectionAsScalaCollection(queryResult).map(mapAsPermission)
        result

      } recover { case e: Throwable => throw new ReadException("Can't get Things") }
    }

    perms match {
      case _ if perms.isEmpty => Future.successful(perms)
      case _ => getPermissionsForNonEmptyIterable(perms)
    }

  }

  def update(perm: Permission): Future[Permission] = {

    val permId = perm.id
    val permDesc = perm.desc

    val query = s"""MATCH (n:$PermLabel { $Id: "$permId" }) SET n.$Desc = "$permDesc""""

    Future {
      session.query(query, emptyMap)
      perm
    } recover { case e: Throwable => throw new SaveException(s"sCan't update Permission with id: $permId") }

  }

  def create(perm: Permission): Future[Permission] = {

    val permId = perm.id
    val permDesc = perm.desc

    val createQuery =
      s"""CREATE (p:$PermLabel { $Id: "$permId", $Desc: "$permDesc" })"""

    Future {
      session.query(createQuery, emptyMap)
      perm
    } recover { case e: Throwable => throw new SaveException(s"sCan't create Permission with id: $permId") }

  }

  def delete(id: UUID): Future[UUID] = {

    val query = s"""MATCH (n:$PermLabel { $Id: "$id" }) DETACH DELETE n"""

    Future {
      session.query(query, emptyMap)
    } flatMap { r =>
      if (r.queryStatistics.getNodesDeleted == 1)
        Future.successful(id)
      else
        Future.failed(new DeleteException(s"Can't delete Permission with id: ${id.toString}"))
    }

  }

  def getAll: Future[List[Permission]] = {
    val query = s"""MATCH (n:$PermLabel)"""

    Future {

      val queryResult = session.query(query, emptyMap)
      val result = resultCollectionAsScalaCollection(queryResult)

      result.map(mapAsPermission).toList

    }
  }

  def deleteAll(): Unit = Future {
    session.query(s"""MATCH (n:$PermLabel) DETACH DELETE n""", emptyMap)
  }

}

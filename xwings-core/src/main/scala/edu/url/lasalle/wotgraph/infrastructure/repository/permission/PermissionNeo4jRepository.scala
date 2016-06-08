package edu.url.lasalle.wotgraph.infrastructure.repository.permission

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.{DeleteException, ReadException, SaveException}
import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Permission
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper
import org.neo4j.ogm.session.Session

import scala.concurrent.{ExecutionContext, Future}

case class PermissionNeo4jRepository(
                                 session: Session
                               )
                               (implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  private val PermLabel = "Perm"

  private val IdKey = "_id"

  private val DescKey = "desc"

  def findById(id: UUID): Future[Option[Permission]] = {

    Future {

      val query =
        s"""MATCH (n:$PermLabel { $IdKey: "$id" }) RETURN n.$IdKey AS $IdKey, n.$DescKey AS $DescKey"""

      val queryResult = session.query(query, createEmptyMap)

      val result = resultCollectionAsScalaCollection(queryResult)

      result.headOption.map { head =>

        val permId = UUID.fromString(head.get(IdKey).get.asInstanceOf[String])
        val permDesc = head.get(DescKey).get.asInstanceOf[String]

        Permission(permId, permDesc)

      }

    } recover { case e: Throwable => throw new ReadException(s"Neo4j: Can't get Permission with id: $id") }

  }

  def update(perm: Permission): Future[Permission] = {

    val permId = perm.id
    val permDesc = perm.desc

    val query = s"""MATCH (n:$PermLabel { $IdKey: "$permId" }) SET n.$DescKey = "$permDesc""""

    Future {
      session.query(query, createEmptyMap)
      perm
    } recover { case e: Throwable => throw new SaveException(s"sCan't update Permission with id: $permId") }

  }

  def create(perm: Permission): Future[Permission] = {

    val permId = perm.id
    val permDesc = perm.desc

    def createQuery: String = {

      s"""CREATE (p:$PermLabel { $IdKey: "$permId", $DescKey: "$permDesc" })"""
    }

    Future {
      session.query(createQuery, createEmptyMap)
      perm
    } recover { case e: Throwable => throw new SaveException(s"sCan't create Permission with id: $permId") }

  }

  def delete(id: UUID): Future[UUID] = {

    val query = s"""MATCH (n:$PermLabel { $IdKey: "$id" }) DETACH DELETE n"""

    Future {
      session.query(query, createEmptyMap)
    } flatMap { r =>
      if (r.queryStatistics.getNodesDeleted == 1)
        Future.successful(id)
      else
        Future.failed(new DeleteException(s"Can't delete Permission with id: ${id.toString}"))
    }

  }

  def deleteAll(): Unit = Future {
    session.query(s"""MATCH (n:$PermLabel) DETACH DELETE n""", createEmptyMap)
  }
}

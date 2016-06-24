package wotgraph.app.role.infrastructure.repository.neo4j

import java.util.UUID

import wotgraph.app.exceptions.{DeleteException, ReadException, SaveException}
import wotgraph.toolkit.repository.neo4j.helpers.Neo4jOGMHelper
import org.neo4j.ogm.session.Session
import org.slf4j.LoggerFactory
import wotgraph.app.permission.domain.entity.Permission
import wotgraph.app.permission.infrastructure.repository.neo4j.PermissionNeo4jRepository
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.infrastructure.serialization.keys.RoleKeys._
import wotgraph.app.permission.infrastructure.serialization.keys.PermissionKeys

import scala.concurrent.{ExecutionContext, Future}

object RoleNeo4jRepository {

  object Keys {

    val RoleLabel = "Role"

    val PermissionRelKey = "PERM"

  }

}


case class RoleNeo4jRepository(
                                session: Session
                              )
                              (implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  import RoleNeo4jRepository.Keys._
  import scala.concurrent._

  val logger = LoggerFactory.getLogger(classOf[RoleNeo4jRepository]);

  def findById(id: UUID): Future[Option[Role]] = {

    val roleId = s"r$Id"

    val query =
      s"""
         |MATCH (n:$RoleLabel { $Id: "$id" }), (n)-[r:$PermissionRelKey]->(n2)
         |RETURN n.$Id AS $roleId, n.$Name AS $Name,
         |n2.${PermissionKeys.Id} AS ${PermissionKeys.Id}, n2.${PermissionKeys.Desc} AS ${PermissionKeys.Desc}
         |""".stripMargin

    val mapAsPermission = wotgraph.app.permission.infrastructure.repository.neo4j.Neo4jHelper.mapAsPermission _

    (Future {
      blocking(session.query(query, emptyMap))
    } recover {
      case e: Throwable => throw new ReadException(s"Neo4j: Can't get Role with id: $id")
    }).map { qr =>
      val result = resultCollectionAsScalaCollection(qr)

      val perms = result.map(mapAsPermission).toSet
      logger.debug(perms.toString())

      result.headOption.map { head =>

        val roleId = UUID.fromString(head.get(Id).get.asInstanceOf[String])
        val roleName = head.get(Name).get.asInstanceOf[String]

        Role(roleId, roleName, perms)

      }
    }

  }

  def update(role: Role): Future[Role] = ???

  def create(role: Role): Future[Role] = {

    val roleId = role.id
    val roleName = role.name

    val createQuery = createAndLink1QueryFactory(
      nodeDefinition = s"""(n:$RoleLabel { $Id: "$roleId", $Name: "$roleName" })""",
      relatees = role.permissions,
      relateeQueryMatchDefinition = (i: Int, p: Permission) =>
        s"""(n$i:${PermissionNeo4jRepository.Keys.PermLabel} {${PermissionKeys.Id}: "${p.id}"})""",
      relationDefinition = (i: Int) =>
        s"""(n)-[r$i:$PermissionRelKey]->(n$i)"""
    )

    (Future {
      blocking(session.query(createQuery, emptyMap))
    } recover {
      case e: Throwable => throw new SaveException(s"sCan't create Role with id: $roleId")
    }).map(_ => role)
  }

  def delete(id: UUID): Future[UUID] = {

    val query = s"""MATCH (n:$RoleLabel { $Id: "${id.toString}"}) DETACH DELETE (n)"""

    Future {
      blocking(session.query(query, emptyMap))
    } flatMap { r =>
      if (r.queryStatistics.getNodesDeleted == 1)
        Future.successful(id)
      else
        Future.failed(new DeleteException(s"Can't delete thing with id: ${id.toString}"))
    }
  }

  def getAll: Future[List[Role]] = {
    val query = s"""MATCH (n:$RoleLabel) RETURN n.$Id AS $Id, n.$Name AS $Name"""

    Future {
      blocking(session.query(query, emptyMap))
    }.map { qr =>

      val result = resultCollectionAsScalaCollection(qr)

      result.map { e =>
        val roleId = UUID.fromString(e.get(Id).get.asInstanceOf[String])
        val roleName = e.get(Name).get.asInstanceOf[String]
        Role(roleId, roleName)
      }.toList

    }
  }

  def deleteAll: Unit = Future {
    blocking(session.query(s"""MATCH (n:$RoleLabel) DETACH DELETE n""", emptyMap))
  }

}
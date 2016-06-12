package wotgraph.app.user.infrastructure.repository.neo4j

import java.util.UUID

import wotgraph.app.user.domain.entity.User
import wotgraph.app.exceptions.{DeleteException, ReadException, SaveException}
import wotgraph.toolkit.repository.neo4j.helpers.Neo4jOGMHelper
import org.neo4j.ogm.session.Session
import org.scalactic._
import org.slf4j.LoggerFactory
import wotgraph.app.role.domain.entity.Role
import wotgraph.app.role.infrastructure.repository.neo4j.RoleNeo4jRepository
import wotgraph.app.user.infrastructure.serialization.keys.UserKeys._
import wotgraph.app.role.infrastructure.serialization.keys.RoleKeys


import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

object UserNeo4jRepository {

  object Keys {

    val UserLabel = "User"

    val RoleRelKey = "ROLE"

  }

}

case class UserNeo4jRepository(
                                session: Session
                              )
                              (implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  import UserNeo4jRepository.Keys._


  val logger = LoggerFactory.getLogger(classOf[UserNeo4jRepository]);

  /**
    * Finds a User by its id
    *
    * @param id
    * @return
    */
  def findById(id: UUID): Future[Option[User]] = {

    Future {

      val query =
        s"""MATCH (n:$UserLabel {$Id: "$id"}), (n)-[r:$RoleRelKey]->(n2)
            | RETURN n.$Id AS $Id, n2.${RoleKeys.Id} AS ${RoleKeys.Id} n2.${RoleKeys.Name} AS ${RoleKeys.Name}""".stripMargin;

      val queryResult = session.query(query, emptyMap)

      val result = queryResult.queryResults().asScala.map(_.asScala)

      result.headOption.map { head =>

        val userId = UUID.fromString(head.get(Id).get.asInstanceOf[String])
        val roleId = UUID.fromString(head.get({RoleKeys.Id}).get.asInstanceOf[String])
        val roleName = head.get({RoleKeys.Id}).get.asInstanceOf[String]
        // The role must exist
        val role = Role(roleId, roleName)

        User(id = userId, role = role)

      }

    } recover { case e: Throwable => throw new ReadException(s"Neo4j: Can't get User with id: $id") }

  }

  /**
    * Updates a User
    *
    * @param user
    * @return
    */
  def update(user: User): Future[User] = {

    val userId = user.id

    val firstQueryMatch = "MATCH"
    val currentUserMatch = s"""(n:$UserLabel {$Id: "$userId"})"""

    def deleteRoleRelationQuery: String = {
      val query = s"""$firstQueryMatch $currentUserMatch-[r:$RoleRelKey]->() DELETE r"""
      query
    }

    def createRoleRelationQuery: String = {

      val roleId = user.role.id
      val roleMatch = s"""(n2:${RoleNeo4jRepository.Keys.RoleLabel} {${RoleKeys.Id}: $roleId})"""

      val relationshipCreate = s"""(n)->[r:$RoleRelKey]->(n2)"""

      val query = s"""$firstQueryMatch $currentUserMatch, $roleMatch CREATE $relationshipCreate"""

      query
    }

    lazy val deleteRoleF = Future {
      session.query(deleteRoleRelationQuery, emptyMap)
      user
    } recover { case e: Throwable => throw new DeleteException(s"Can't delete relationships of User with id: $userId") }

    lazy val createRoleF = Future {
      session.query(createRoleRelationQuery, emptyMap)
      user
    } recover { case e: Throwable => throw new SaveException(s"sCan't create relationships of User with id: $userId") }

    deleteRoleF zip createRoleF map (_ => user)

  }

  /**
    * Creates a User
    *
    * @param user
    * @return
    */
  def create(user: User): Future[User Or Every[String]] = {

    val roleId = user.role.id

    def createQuery: String = {

      s"""MATCH (role:${RoleNeo4jRepository.Keys.RoleLabel}) WHERE role.${RoleKeys.Id} = "$roleId"
          |CREATE (n:$UserLabel {$Id: "${user.id.toString}"})-[r:$RoleRelKey]->(role)""".stripMargin
    }


    Future {
      val r = session.query(createQuery, emptyMap)
      if (r.queryStatistics().getNodesCreated == 1) Good(user) else Bad(One("User not created"))
    } recover { case e: Throwable => throw new SaveException(s"sCan't create User with id: ${user.id}") }
  }

  /**
    * Deletes a User
    *
    * @param id
    * @return
    */
  def delete(id: UUID): Future[UUID] = {

    val query = s"""MATCH (n:$UserLabel { $Id: "${id.toString}"}) DETACH DELETE n"""

    Future {
      session.query(query, emptyMap)
    } flatMap { r =>
      if (r.queryStatistics.getNodesDeleted == 1)
        Future.successful(id)
      else
        Future.failed(new DeleteException(s"Can't delete User with id: ${id.toString}"))
    }
  }

}

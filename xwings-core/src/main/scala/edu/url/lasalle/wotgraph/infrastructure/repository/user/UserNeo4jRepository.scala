package edu.url.lasalle.wotgraph.infrastructure.repository.user

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.{DeleteException, ReadException, SaveException}
import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Role
import edu.url.lasalle.wotgraph.domain.entity.user.User
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper
import edu.url.lasalle.wotgraph.infrastructure.repository.role.RoleNeo4jRepository
import org.neo4j.ogm.session.Session
import org.scalactic._
import org.slf4j.LoggerFactory

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

  import User.Keys._
  import UserNeo4jRepository.Keys._
  import Role.{Keys => RoleK}


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
        s"""MATCH (n:$UserLabel {$IdKey: "$id"}), (n)-[r:$RoleRelKey]->(n2)
            | RETURN n.$IdKey AS $IdKey, n2.${RoleK.IdKey} AS ${RoleK.IdKey} n2.${RoleK.NameKey} AS ${RoleK.NameKey}""".stripMargin;

      val queryResult = session.query(query, createEmptyMap)

      val result = queryResult.queryResults().asScala.map(_.asScala)

      result.headOption.map { head =>

        val userId = UUID.fromString(head.get(IdKey).get.asInstanceOf[String])
        val roleId = UUID.fromString(head.get({RoleK.IdKey}).get.asInstanceOf[String])
        val roleName = head.get({RoleK.IdKey}).get.asInstanceOf[String]
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
    val currentUserMatch = s"""(n:$UserLabel {$IdKey: "$userId"})"""

    def deleteRoleRelationQuery: String = {
      val query = s"""$firstQueryMatch $currentUserMatch-[r:$RoleRelKey]->() DELETE r"""
      query
    }

    def createRoleRelationQuery: String = {

      val roleId = user.role.id
      val roleMatch = s"""(n2:${RoleNeo4jRepository.Keys.RoleLabel} {${RoleK.IdKey}: $roleId})"""

      val relationshipCreate = s"""(n)->[r:$RoleRelKey]->(n2)"""

      val query = s"""$firstQueryMatch $currentUserMatch, $roleMatch CREATE $relationshipCreate"""

      query
    }

    lazy val deleteRoleF = Future {
      session.query(deleteRoleRelationQuery, createEmptyMap)
      user
    } recover { case e: Throwable => throw new DeleteException(s"Can't delete relationships of User with id: $userId") }

    lazy val createRoleF = Future {
      session.query(createRoleRelationQuery, createEmptyMap)
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

      s"""MATCH (role:${RoleNeo4jRepository.Keys.RoleLabel}) WHERE role.${RoleK.IdKey} = "$roleId"
          |CREATE (n:$UserLabel {$IdKey: "${user.id.toString}"})-[r:$RoleRelKey]->(role)""".stripMargin
    }


    Future {
      val r = session.query(createQuery, createEmptyMap)
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

    val query = s"""MATCH (n:$UserLabel { $IdKey: "${id.toString}"}) DETACH DELETE n"""

    Future {
      session.query(query, createEmptyMap)
    } flatMap { r =>
      if (r.queryStatistics.getNodesDeleted == 1)
        Future.successful(id)
      else
        Future.failed(new DeleteException(s"Can't delete User with id: ${id.toString}"))
    }
  }

}

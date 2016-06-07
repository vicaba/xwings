package edu.url.lasalle.wotgraph.infrastructure.repository.user

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.{DeleteException, ReadException, SaveException}
import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Role
import edu.url.lasalle.wotgraph.domain.entity.user.User
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.Neo4jConf.Config
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

case class UserNeo4jRepository(
                                override val neo4jConf: Config
                              )
                              (implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  val UserLabel = "User"

  val RoleLabel = "Role"

  val IdKey = "_id"

  val RoleIdKey = "_id"

  val RoleNameKey = "name"

  val RoleRelKey = "ROLE"


  /**
    * Finds a User by its id
    * @param id
    * @return
    */
  def findById(id: UUID): Future[Option[User]] = {

    Future {

      val query =
        s"""
           |MATCH (n:$UserLabel {$IdKey: "$id"}), (n)-[r:$RoleRelKey]->(n2)
           | RETURN n.$IdKey AS $IdKey, n2.$RoleIdKey AS $RoleIdKey n2.$RoleNameKey AS $RoleNameKey""".stripMargin;

      val queryResult = session.query(query, createEmptyMap)

      val result = queryResult.queryResults().asScala.map(_.asScala)

      result.headOption.map { head =>

        val userId = UUID.fromString(head.get(IdKey).get.asInstanceOf[String])
        val roleId = UUID.fromString(head.get(RoleIdKey).get.asInstanceOf[String])
        val roleName = head.get(RoleNameKey).get.asInstanceOf[String]
        // The role must exist
        val role = Role(roleId, roleName)

        User(id = userId, role = role)

      }

    } recover { case e: Throwable => throw new ReadException(s"Neo4j: Can't get User with id: $id") }

  }

  /**
    * Updates a User
    * @param user
    * @return
    */
  def update(user: User): Future[User] = {

    val userId = user.id

    val firstQueryMatch = "MATCH"
    val currentUserMatch = s"""(n:$UserLabel {$IdKey: "$userId"})"""

    def deleteRoleQuery: String = {
      val query = s"""$firstQueryMatch $currentUserMatch-[r:$RoleRelKey]->() DELETE r"""
      query
    }

    def createRoleQuery: String = {

      val roleId = user.role.id
      val roleMatch = s"""(n2:$RoleLabel {$RoleIdKey: $roleId})"""

      val relationshipCreate = s"""(n)->[r:$RoleRelKey]->(n2)"""

      val query = s"""$firstQueryMatch $currentUserMatch, $roleMatch CREATE $relationshipCreate"""

      query
    }

    lazy val deleteRoleF = Future {
      session.query(deleteRoleQuery, createEmptyMap)
      user
    } recover { case e: Throwable => throw new DeleteException(s"Can't delete relationships of User with id: $userId") }

    lazy val createRoleF = Future {
      session.query(createRoleQuery, createEmptyMap)
      user
    } recover { case e: Throwable => throw new SaveException(s"sCan't create relationships of User with id: $userId") }

    deleteRoleF zip createRoleF map (_ => user)

  }

  /**
    * Creates a User
    * @param user
    * @return
    */
  def create(user: User): Future[User] = {

    def createQuery: String = {

      val role = user.role.id

      val createNodeQuery =
        s"""MATCH (role:Role) WHERE role._id = $role
            |CREATE (n:$UserLabel {$IdKey: "${user.id.toString}"})-[r:ROLE]->(role)""".stripMargin

      createNodeQuery
    }

    Future {
      session.query(createQuery, createEmptyMap)
      user
    } recover { case e: Throwable => throw new SaveException(s"sCan't create User with id: ${user.id}") }

  }

  /**
    * Deletes a User
    * @param id
    * @return
    */
  def delete(id: UUID): Future[UUID] = {

    val query = s"""MATCH (n:$User { $IdKey: "${id.toString}"}) DETACH DELETE (n)"""

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
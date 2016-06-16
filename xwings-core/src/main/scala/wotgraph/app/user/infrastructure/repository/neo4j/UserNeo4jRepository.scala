package wotgraph.app.user.infrastructure.repository.neo4j

import java.util.UUID

import org.neo4j.ogm.model.Result
import wotgraph.app.user.domain.entity.User
import wotgraph.app.exceptions.{DeleteException, ReadException, SaveException}
import wotgraph.toolkit.repository.neo4j.helpers.Neo4jOGMHelper
import org.neo4j.ogm.session.Session
import org.scalactic._
import org.slf4j.LoggerFactory
import wotgraph.app.user.infrastructure.repository.neo4j.Neo4jHelper
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
    * @param id The user id
    * @return
    */
  def findById(id: UUID): Future[Option[User]] = {

    val roleIdKey = s"r${RoleKeys.Id}"
    val roleNameKey = s"r${RoleKeys.Name}"

    Future {

      val query =
        s"""${Keywords.Match} (n:$UserLabel {$Id: "${n(Id)}"}), (n)-[r:$RoleRelKey]->(n2)
            | RETURN n.$Id AS $Id, n2.$roleIdKey AS $roleIdKey n2.$roleNameKey AS $roleNameKey""".stripMargin;

      val queryResult = session.query(query, Map(Id -> s"$id").asJava)
      val result = queryResult.queryResults().asScala.map(_.asScala)

      result.headOption.map { head =>

        val roleId = UUID.fromString(head.get(roleIdKey).get.asInstanceOf[String])
        val roleName = head.get(roleNameKey).get.asInstanceOf[String]

        Neo4jHelper.mapAsUser(head)(Role(roleId, roleName))

      }

    } recover { case e: Throwable => throw new ReadException(s"Neo4j: Can't get User with id: $id") }

  }

  /**
    * Creates a User
    *
    * @param user The user to create
    * @return
    */
  def create(user: User): Future[User Or Every[String]] = {

    val roleId = user.role.id
    val roleIdKey = s"r${RoleKeys.Id}"

    def createQuery: LazyResult = {

      val query = s"""${Keywords.Match} (role:${RoleNeo4jRepository.Keys.RoleLabel}) WHERE role.${RoleKeys.Id} = ${n(roleIdKey)}
                     |${Keywords.Create} (n:$UserLabel { $Id: ${n(Id)}, $Name: ${n(Name)}, $Password: ${n(Password)} })-[r:$RoleRelKey]->(role)""".stripMargin

      val params = Map(
        roleIdKey -> roleId.toString,
        Id -> user.id.toString,
        Name -> user.name.toString,
        Password -> user.password.toString
      )

      () => session.query(query, params.asJava)
    }

    Future {
      val r = createQuery()
      if (r.queryStatistics().getNodesCreated == 1) Good(user) else Bad(One("User not created"))
    } //recover { case e: Throwable => throw new SaveException(s"sCan't create User with id: ${user.id}") }
  }

  /**
    * Updates a User
    *
    * @param user The user to update
    * @return
    */
  def update(user: User): Future[User] = {

    val userId = user.id

    val currentUserMatch = s"""(n:$UserLabel {$Id: ${n(Id)})"""

    def deleteRoleRelationQuery: LazyResult = {
      val query = s"""${Keywords.Match} $currentUserMatch-[r:$RoleRelKey]->() DELETE r"""
      () => session.query(query, Map(Id -> userId.toString).asJava)
    }

    def createRoleRelationAndUpdateUserQuery: LazyResult = {

      val roleId = user.role.id
      val roleMatch = s"""(n2:${RoleNeo4jRepository.Keys.RoleLabel} {${RoleKeys.Id}: ${n(RoleKeys.Id)})"""

      val relationshipCreate = s"""(n)->[r:$RoleRelKey]->(n2)"""

      val query =
        s"""${Keywords.Match} $currentUserMatch, $roleMatch
           |${Keywords.Match} $relationshipCreate SET n.$Name = ${n(Name)}, n.$Password = ${n(Password)} """.stripMargin

      val params = Map(
        RoleKeys.Id -> roleId.toString,
        Name -> user.name.toString,
        Password -> user.password.toString
      )
      () => session.query(query, params.asJava)
    }

    lazy val deleteRoleRelF = Future {
      deleteRoleRelationQuery()
      user
    } recover { case e: Throwable => throw new DeleteException(s"Can't delete relationships of User with id: $userId") }

    lazy val createRoleAndUserF = Future {
      createRoleRelationAndUpdateUserQuery()
      user
    } recover { case e: Throwable => throw new SaveException(s"sCan't create relationships of User with id: $userId") }

    deleteRoleRelF zip createRoleAndUserF map (_ => user)

  }

  /**
    * Deletes a User
    *
    * @param id The user id to delete
    * @return
    */
  def delete(id: UUID): Future[UUID] = {

    val query = s"""${Keywords.Match} (n:$UserLabel { $Id: "${n(Id)}"}) DETACH DELETE n"""
    val params = Map(Id -> id.toString)

    Future {
      session.query(query, params.asJava)
    } flatMap { r =>
      if (r.queryStatistics.getNodesDeleted == 1)
        Future.successful(id)
      else
        Future.failed(new DeleteException(s"Can't delete User with id: ${id.toString}"))
    }
  }

}

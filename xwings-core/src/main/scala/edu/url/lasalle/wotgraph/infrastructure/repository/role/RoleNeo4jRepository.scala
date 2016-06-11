package edu.url.lasalle.wotgraph.infrastructure.repository.role

import java.util.UUID

import edu.url.lasalle.wotgraph.application.exceptions.{ReadException, SaveException}
import edu.url.lasalle.wotgraph.domain.entity.user.authorization.{Role, Permission}
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper
import edu.url.lasalle.wotgraph.infrastructure.repository.permission.PermissionNeo4jRepository
import org.neo4j.ogm.session.Session

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

  import Role.Keys._
  import RoleNeo4jRepository.Keys._
  import Permission.{Keys => Perm}


  def findById(id: UUID): Future[Option[Role]] = Future {

    val query =
      s"""MATCH (n:$RoleLabel { $IdKey: "$id" }), (n)-[r:$PermissionRelKey]->(n2)
          |RETURN n.$IdKey AS $IdKey, n.$NameKey AS $NameKey,
          |n2.${Perm.IdKey} AS ${Perm.IdKey}, n2.${Perm.DescKey} AS ${Perm.DescKey}""".stripMargin

    val queryResult = session.query(query, emptyMap)

    val result = resultCollectionAsScalaCollection(queryResult)

    result.headOption.map { head =>

      val roleId = UUID.fromString(head.get(IdKey).get.asInstanceOf[String])
      val roleName = head.get(NameKey).get.asInstanceOf[String]

      // TODO: Add Permissions to the role object

      Role(roleId, roleName)

    }

  } recover { case e: Throwable => throw new ReadException(s"Neo4j: Can't get Role with id: $id") }

  def update(role: Role): Future[Role] = ???

  def create(role: Role): Future[Role] = {

    val roleId = role.id
    val roleName = role.name

    val createQuery = createAndLink1QueryFactory(
      nodeDefinition = s"""(p:$RoleLabel { $IdKey: "$roleId", $NameKey: "$roleName" })""",
      relatees = role.permissions,
      relateeQueryMatchDefinition = (i: Int, p: Permission) =>
        s"""(n$i:${PermissionNeo4jRepository.Keys.PermLabel} {${Perm.IdKey}: "${p.id}"})""",
      relationDefinition = (i: Int) =>
        s"""(n)-[r$i:$PermissionRelKey]->(n$i)"""
    )

    Future {
      session.query(createQuery, emptyMap)
      role
    } recover { case e: Throwable => throw new SaveException(s"sCan't create Role with id: $roleId") }
  }

  def delete(id: UUID): Future[UUID] = ???

}
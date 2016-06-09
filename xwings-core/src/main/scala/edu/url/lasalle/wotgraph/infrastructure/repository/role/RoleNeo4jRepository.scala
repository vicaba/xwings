package edu.url.lasalle.wotgraph.infrastructure.repository.role

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.entity.user.authorization.Role
import edu.url.lasalle.wotgraph.infrastructure.repository.neo4j.helpers.Neo4jOGMHelper
import org.neo4j.ogm.session.Session

import scala.concurrent.{ExecutionContext, Future}


case class RoleNeo4jRepository(
                                session: Session
                              )
                              (implicit ec: ExecutionContext)
  extends Neo4jOGMHelper {

  val RoleLabel = "Role"

  val IdKey = "_id"

  val RoleNameKey = "name"

  val RoleRelKey = "ROLE"

  def findById(id: UUID): Future[Option[Role]] = ???

  def update(role: Role): Future[Role] = ???

  def create(role: Role): Future[Role] = {

  }

  def delete(id: UUID): Future[UUID] = ???

}